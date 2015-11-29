--
-- JBoss, Home of Professional Open Source
-- Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
-- contributors by the @authors tag. See the copyright.txt in the
-- distribution for a full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements
-- Since the database doesn't know to increase the Sequence to match what is manually loaded here it starts at 1 and tries
--  to enter a record with the same PK and create an error.  If we use a high we don't interfere with the sequencing (at least until later).
-- NOTE: this file should be removed for production systems. 
--insert into Contact (id, name, email, phone_number, state) values (10001, 'John', 'john.smith@mailinator.com', '04836723332', 'NY')
--insert into Contact (id, name, email, phone_number, state) values (10002, 'Davey', 'davey.jones@locker.com', '06352846372', 'NY')
--insert into Contact (id, name, email, phone_number, state) values (10003, 'Jiaying', 'rutut@locker.com', '04625375645', 'NY')

--insert into Taxi (id, registration, seats, state) values (10001, 'FFF7878', 4, 'NY')
--insert into Taxi (id, registration, seats, state) values (10002, 'GFF9678', 8, 'NY')
--insert into Taxi (id, registration, seats, state) values (10003, 'PFF7878', 3, 'NY')
--insert into TaxiBooking (id, customerID, taxiID, date, state) values (10007, 10001, 10001,'2015-11-11', 'NY')
--insert into TaxiBooking (id, customerID, taxiID, date, state) values (10008, 10001, 10002,'2015-11-10', 'NY')

--insert into Hotel (id, hotelName, phonenumber, postcode) values (10002, 'GTI', '04637482649', 'DH12HD')
--insert into HotelBooking (id, customerID, hotelID, date, state) values (10001, 10001, 10002,'2015-11-10', 'NY')

--insert into Flight (id, flightNumber, departurePoint, flightDestination, state) values (10001, 'GTI88', 'GGG', 'KKK', 'NY')
--insert into FlightBooking (id, contact_id, flight_id, bookingDate, state) values (10008, 10001, 10001,'2015-11-10', 'NY')

insert into Booking (id, customerID, taxiBooking, flightBooking, hotelBooking, date, state) values (10001, 9, 1, 4, 6, '2015-11-10', 'NY')