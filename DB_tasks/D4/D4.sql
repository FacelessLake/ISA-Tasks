DROP TABLE IF EXISTS prices;
CREATE TABLE prices AS (
	 SELECT
            flights.flight_id AS flight_id,
			flights.departure_airport AS departure,
			flights.arrival_airport AS arrival,
            flights.aircraft_code AS plane,
            pass.seat_no AS seat,
			ticket.fare_conditions AS tariff,
            ticket.amount AS price
           
        FROM flights
        JOIN ticket_flights ticket ON ticket.flight_id = flights.flight_id
        JOIN boarding_passes pass ON ticket.ticket_no = pass.ticket_no AND pass.flight_id = flights.flight_id
		ORDER BY flight_id, tariff, seat
);