import psycopg
from flask import Flask, g, make_response, jsonify
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
            return make_response(jsonify({'error': 'Airports not found'}), 404)
        return result


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
            return make_response(jsonify({'error': 'Airports not found'}), 404)
        return result


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
            return make_response(jsonify({'error': 'Airports not found'}), 404)
        return result


api.add_resource(Cities, '/cities')
api.add_resource(Airports, '/airports')
api.add_resource(CityAirports, '/airports/<city>')
api.add_resource(InboundSchedule, '/airports/<airport>/inbound')
api.add_resource(OutboundSchedule, '/airports/<airport>/outbound')
app.run(debug=True)
