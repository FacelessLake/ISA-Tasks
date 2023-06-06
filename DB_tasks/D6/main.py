import datetime
import os
from binascii import b2a_hex

import psycopg
from flask import Flask, g, make_response, jsonify, request
from flask_restful import Resource, Api


def get_db() -> psycopg.Connection:
    if 'db' not in g:
        g.db = psycopg.connect('postgresql://postgres:1477@127.0.0.1/demo')
    return g.db


def close_db(e=None):
    db = g.pop('db', None)
    if db is not None:
        db.close()


app = Flask(__name__)
app.teardown_appcontext(close_db)
api = Api(app)


# List all the available source and destination cities
class Cities(Resource):
    def get(self):
        cursor = get_db().cursor()
        cursor.execute('SELECT city, coordinates, timezone FROM airports_data ORDER BY city')
        cities = cursor.fetchall()
        cursor.close()

        result = list(map(lambda a: {
            "name": a[0],
            "coordinates": a[1],
            "timezone": a[2]
        }, cities))
        if len(result) == 0:
            return make_response(jsonify({'error': 'Cities list not found'}), 404)
        return result


# List all the available source and destination airports
class Airports(Resource):
    def get(self):
        cursor = get_db().cursor()
        cursor.execute('SELECT airport_name, airport_code, timezone FROM airports_data ORDER BY airport_name')
        airports = cursor.fetchall()
        cursor.close()

        result = list(map(lambda a: {
            "name": a[0],
            "abbreviate": a[1],
            "timezone": a[2]
        }, airports))
        if len(result) == 0:
            return make_response(jsonify({'error': 'Airports list not found'}), 404)
        return result


# List the airports within a city
class CityAirports(Resource):
    def get(self, city):
        cursor = get_db().cursor()
        cursor.execute(
            """SELECT airport_name, airport_code, timezone FROM airports_data 
            WHERE city->>'en' = %s OR city->>'ru' = %s ORDER BY airport_name""",
            [city, city])
        airports = cursor.fetchall()
        cursor.close()

        result = list(map(lambda a: {
            "name": a[0],
            "abbreviate": a[1],
            "timezone": a[2]
        }, airports))
        if len(result) == 0:
            return make_response(jsonify({'error': 'No city called ' + city + ' has been found'}), 404)
        return result


# List the inbound schedule for an airport:
class InboundSchedule(Resource):
    def get(self, airport):
        cursor = get_db().cursor()
        cursor.execute(
            """SELECT flight_no, departure_airport, scheduled_arrival, actual_arrival, arrival_airport FROM flights 
           WHERE arrival_airport = %s ORDER BY scheduled_arrival""", [airport])
        inbound_schedule = cursor.fetchall()
        cursor.close()

        result = list(map(lambda a: {
            "flight_no": a[0],
            "origin": a[1],
            "arrival_date": (a[2] if a[3] is None else a[3]).strftime('%Y-%m-%d %H:%M:%S %z')
        }, inbound_schedule))
        if len(result) == 0:
            return make_response(jsonify({'error': 'Airport ' + airport + ' is not found'}), 404)
        return result


# List the outbound schedule for an airport:
class OutboundSchedule(Resource):
    def get(self, airport):
        cursor = get_db().cursor()
        cursor.execute(
            """SELECT flight_no, arrival_airport, scheduled_departure, actual_departure, departure_airport FROM flights 
           WHERE departure_airport = %s ORDER BY scheduled_arrival""", [airport])
        inbound_schedule = cursor.fetchall()
        cursor.close()

        result = list(map(lambda a: {
            "flight_no": a[0],
            "destination": a[1],
            "departure_date": (a[2] if a[3] is None else a[3]).strftime('%Y-%m-%d %H:%M:%S %z')
        }, inbound_schedule))
        if len(result) == 0:
            return make_response(jsonify({'error': 'Airport ' + airport + ' is not found'}), 404)
        return result


def make_booking_reference(cursor):
    cursor.execute("""SELECT book_ref from bookings""")
    taken = list(map(lambda a: a[0], cursor.fetchall()))
    result = "00" + str(b2a_hex(os.urandom(4))[0:4])[2:6].upper()
    while result in taken:
        result = "00" + str(b2a_hex(os.urandom(4))[0:4])[2:6].upper()
    return result


def make_ticket_number(cursor, i):
    cursor.execute("""SELECT ticket_no from ticket_flights ORDER BY ticket_no DESC LIMIT 1""")
    new_ticket_no = cursor.fetchall().pop()[0]
    return '000' + str(int(new_ticket_no) + i)


def get_seats_total(cursor, flight_id, fare_conditions):
    cursor.execute(
        """SELECT count(seat_no) from flights
        JOIN seats on flights.aircraft_code = seats.aircraft_code
        WHERE flight_id = %s AND fare_conditions = %s
        GROUP BY flight_id, flights.aircraft_code, fare_conditions""", [flight_id, fare_conditions])
    seats = cursor.fetchall()
    if seats is []:
        return 0
    return int(seats.pop()[0])


def get_seats_booked(cursor, flight_id, fare_conditions):
    cursor.execute(
        """SELECT count(ticket_no) from ticket_flights
        WHERE flight_id = %s AND fare_conditions = %s
        GROUP BY flight_id, fare_conditions""", [flight_id, fare_conditions])
    seats = cursor.fetchall()
    if len(seats) == 0:
        return 0
    print(seats)
    return int(seats.pop()[0])


def get_price(cursor, flight_id, fare_conditions):
    cursor.execute(
        """SELECT prices.price from prices
        JOIN flights on prices.flight_no = flights.flight_no
        WHERE flights.flight_id = %s AND tariff=%s
        GROUP BY flights.flight_id, flights.flight_no, prices.tariff, prices.price
        ORDER BY  prices.price DESC """, [flight_id, fare_conditions])
    a = cursor.fetchall()
    if len(a) == 0:
        return 0
    return int(a.pop()[0])


def handle_one_flight(cursor, flight_id, fare_conditions, i):
    cursor.execute("""SELECT status from flights WHERE status = 'Scheduled' AND flight_id = %s""", [flight_id])
    status = cursor.fetchall()

    if len(status) == 0:
        return 400, "Flight " + flight_id + " is not available for booking"

    total_seats = get_seats_total(cursor, flight_id, fare_conditions)
    taken_seats = get_seats_booked(cursor, flight_id, fare_conditions)
    if total_seats == taken_seats:
        return 400, "Flight " + flight_id + " is full"

    price = get_price(cursor, flight_id, fare_conditions)

    return 200, {"ticket_no": make_ticket_number(cursor, i), "amount": price}


# Create a booking for a selected route for a single passenger
class Booking(Resource):
    def put(self):
        json = request.json
        passenger_name = json['passenger_name']
        passenger_id = json['passenger_id']
        flight_ids = json['flight_ids']
        fare_conditions = json['fare_conditions']

        cursor = get_db().cursor()

        book_ref = make_booking_reference(cursor)
        book_date = datetime.datetime.today().strftime('%Y-%m-%d %H:%M:%S %z')

        ticket_flights = []
        tickets = []
        total_amount = 0
        i = 1

        for flight in flight_ids:
            response_code, info = handle_one_flight(cursor, flight, fare_conditions, i)
            if response_code >= 400:
                return make_response(jsonify({'error': info}), response_code)
            ticket_flight = {"ticket_no": info["ticket_no"],
                             "flight_id": int(flight),
                             "fare_conditions": fare_conditions,
                             "amount": info["amount"]}
            ticket_flights.append(ticket_flight)

            ticket = {"ticket_no": info["ticket_no"],
                      "book_ref": book_ref,
                      "passenger_id": passenger_id,
                      "passenger_name": passenger_name}
            tickets.append(ticket)
            total_amount += ticket_flight["amount"]
            i += 1

        cursor.execute("""INSERT INTO bookings VALUES (%s, %s, %s)""", [book_ref, book_date, total_amount])

        for ticket in tickets:
            cursor.execute("""INSERT INTO tickets VALUES (%s, %s, %s, %s)""",
                           [ticket["ticket_no"], book_ref, passenger_id, passenger_name])

        for ticket_flight in ticket_flights:
            cursor.execute("""INSERT INTO ticket_flights VALUES (%s, %s, %s, %s)""",
                           [ticket_flight["ticket_no"], ticket_flight["flight_id"],
                            ticket_flight["fare_conditions"], ticket_flight["amount"]])
        get_db().commit()

        booking = {
            "book_ref": book_ref,
            "total_amount": total_amount
        }
        cursor.close()

        return {"booking": booking,
                "tickets": tickets,
                "ticket_flights": ticket_flights}, 200


def find_free_seat(all_seats, taken_seats):
    for s in all_seats:
        if s not in taken_seats:
            return s
    return make_response(jsonify({'error': 'No seats are available for this flight'}), 400)


class CheckIn(Resource):
    def put(self):
        json = request.json
        ticket_no = json["ticket_no"]
        flight_id = json["flight_id"]

        cursor = get_db().cursor()
        cursor.execute(
            """SELECT status from flights 
            WHERE (status = 'On Time' OR flights.status = 'Delayed') AND flight_id = %s""", [flight_id])
        status = cursor.fetchall()

        if len(status) == 0:
            return make_response(jsonify(
                {'error': 'Flight ' + flight_id + ' is not available for registration'}), 400)

        cursor.execute(
            """SELECT fare_conditions from ticket_flights 
            WHERE flight_id = %s AND ticket_no = %s""", [flight_id, ticket_no])
        tariff = cursor.fetchall()

        if len(tariff) == 0:
            return make_response(jsonify(
                {'error': 'No tickets with number ' + ticket_no + ' were booked for flight ' + flight_id}), 404)

        fare_cond = tariff.pop()[0]

        cursor.execute("""SELECT seat_no from boarding_passes WHERE flight_id = %s""", [flight_id])
        taken_seats = list(map(lambda a: a[0], cursor.fetchall()))

        cursor.execute(
            """SELECT seats.seat_no from flights
            JOIN seats ON flights.aircraft_code = seats.aircraft_code
            WHERE flights.flight_id = %s AND fare_conditions = %s""", [flight_id, fare_cond])
        all_seats = list(map(lambda a: a[0], cursor.fetchall()))

        seat_no = find_free_seat(all_seats, taken_seats)

        cursor.execute(
            """SELECT boarding_no from boarding_passes WHERE flight_id = %s
            ORDER BY boarding_no DESC LIMIT 1""", [flight_id])
        boarding_no = cursor.fetchall()

        if len(boarding_no) == 0:
            boarding_no = 1
        else:
            boarding_no = boarding_no.pop()[0] + 1

        cursor.execute(
            """SELECT * from boarding_passes WHERE ticket_no = %s""", [ticket_no])
        boarding_pass = cursor.fetchall()

        if len(boarding_pass) != 0:
            return make_response(jsonify(
                {'error': 'Ticket number ' + ticket_no + ' is already booked for the flight ' + flight_id}), 400)
        else:
            cursor.execute(
                """INSERT INTO boarding_passes VALUES (%s, %s, %s, %s)""", [ticket_no, flight_id, boarding_no, seat_no])

        get_db().commit()

        return {"ticket_no": ticket_no,
                "flight_id": flight_id,
                "boarding_no": boarding_no,
                "seat_no": seat_no}, 200


api.add_resource(Cities, '/cities')
api.add_resource(Airports, '/airports')
api.add_resource(CityAirports, '/airports/<city>')
api.add_resource(InboundSchedule, '/airports/<airport>/inbound')
api.add_resource(OutboundSchedule, '/airports/<airport>/outbound')
api.add_resource(Booking, '/booking')
api.add_resource(CheckIn, '/check_in')
app.run(debug=True)
