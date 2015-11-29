/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function() {
    'use strict';
    angular
        .module('app')
        .controller('AppController', AppController)
        .controller('TaxiAppController', TaxiAppController)
        .controller('HotelAppController', HotelAppController)
        .controller('flightAppController', flightAppController)
        .controller('BookingAppController', BookingAppController);

    AppController.$inject = ['$scope', '$filter', 'Contact', 'messageBag'];
    TaxiAppController.$inject = ['$scope', '$filter', 'Taxi', 'messageBag'];
    HotelAppController.$inject = ['$scope', '$filter', 'Hotel', 'messageBag'];
    BookingAppController.$inject = ['$scope', '$filter', 'Booking', 'messageBag'];
    flightAppController.$inject = ['$scope', '$filter', 'Flight', 'messageBag'];
    
    function AppController($scope, $filter, Contact, messageBag) {
        //Assign Contact service to $scope variable
        $scope.contacts = Contact;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide contact list into several sub lists according to the first character of their firstName property
        var getHeadings = function(contacts) {
            var headings = {};
            for(var i = 0; i<contacts.length; i++) {
                //Get the first letter of a contact's firstName
                var startsWithLetter = contacts[i].name.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the contact to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(contacts[i]);
                } else {
                    headings[startsWithLetter] = [contacts[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of Contacts and their letter headings
        $scope.contacts.data = $scope.contacts.query(
            //Successful query
            function(data) {
                $scope.contacts.data = data;
                $scope.contactsList = getHeadings($scope.contacts.data);
                //Keep the contacts list headings in sync with the underlying contacts
                $scope.$watchCollection('contacts.data', function(newContacts, oldContacts) {
                    $scope.contactsList = getHeadings(newContacts);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the contacts are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the contacts list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.contactsList = getHeadings($filter('filter')($scope.contacts.data, $scope.search));
        });
    }
    
        function TaxiAppController($scope, $filter, Taxi, messageBag) {
        //Assign Taxi service to $scope variable
        $scope.taxis = Taxi;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide taxi list into several sub lists according to the first character of their registration property
        var getHeadings = function(taxis) {
            var headings = {};
            for(var i = 0; i<taxis.length; i++) {
                //Get the first letter of a taxi's registration
                var startsWithLetter = taxis[i].registration.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the registration to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(taxis[i]);
                } else {
                    headings[startsWithLetter] = [taxis[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of Taxis and their letter headings
        $scope.taxis.data = $scope.taxis.query(
            //Successful query
            function(data) {
                $scope.taxis.data = data;
                $scope.taxisList = getHeadings($scope.taxis.data);
                //Keep the taxis list headings in sync with the underlying contacts
                $scope.$watchCollection('taxis.data', function(newTaxis, oldTaxis) {
                    $scope.taxisList = getHeadings(newTaxis);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the taxis are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the taxis list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.taxisList = getHeadings($filter('filter')($scope.taxis.data, $scope.search));
        });
    }
    
            function HotelAppController($scope, $filter, Hotel, messageBag) {
        //Assign Taxi service to $scope variable
        $scope.hotels = Hotel;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide taxi list into several sub lists according to the first character of their registration property
        var getHeadings = function(hotels) {
            var headings = {};
            for(var i = 0; i<hotels.length; i++) {
                //Get the first letter of a taxi's registration
                var startsWithLetter = hotels[i].hotel.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the registration to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(hotels[i]);
                } else {
                    headings[startsWithLetter] = [hotels[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of Taxis and their letter headings
        $scope.hotels.data = $scope.hotels.query(
            //Successful query
            function(data) {
                $scope.hotels.data = data;
                $scope.hotelsList = getHeadings($scope.hotels.data);
                //Keep the taxis list headings in sync with the underlying contacts
                $scope.$watchCollection('hotels.data', function(newHotels, oldHotels) {
                    $scope.hotelsList = getHeadings(newHotels);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the taxis are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the taxis list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.hotelsList = getHeadings($filter('filter')($scope.hotels.data, $scope.search));
        });
    }
    
        function flightAppController($scope, $filter, Flight, messageBag) {
        //Assign Contact service to $scope variable
        $scope.flights = Flight;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide contact list into several sub lists according to the first character of their firstName property
        var getHeadings = function(flights) {
            var headings = {};
            for(var i = 0; i<flights.length; i++) {
                //Get the first letter of a contact's firstName
                var startsWithLetter = flights[i].flightNumber.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the contact to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(flights[i]);
                } else {
                    headings[startsWithLetter] = [flights[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of Contacts and their letter headings
        $scope.flights.data = $scope.flights.query(
            //Successful query
            function(data) {
                $scope.flights.data = data;
                $scope.flightsList = getHeadings($scope.flights.data);
                //Keep the contacts list headings in sync with the underlying contacts
                $scope.$watchCollection('flights.data', function(newFlights, oldFlights) {
                    $scope.flightsList = getHeadings(newFlights);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the contacts are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the contacts list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.flightsList = getHeadings($filter('filter')($scope.flights.data, $scope.search));
        });
    }
    
    function BookingAppController($scope, $filter, Booking, messageBag) {
        //Assign Booking service to $scope variable
        $scope.bookings = Booking;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide booking list into several sub lists according to the first character of their date property
        var getHeadings = function(bookings) {
            var headings = {};
            for(var i = 0; i<bookings.length; i++) {
                //Get the first letter of a booking's date
                var startsWithLetter = bookings[i].date.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the taxi to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(bookings[i]);
                } else {
                    headings[startsWithLetter] = [bookings[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of Bookings and their letter headings
        $scope.bookings.data = $scope.bookings.query(
            //Successful query
            function(data) {
                $scope.bookings.data = data;
                $scope.bookingsList = getHeadings($scope.bookings.data);
                //Keep the bookings list headings in sync with the underlying bookings
                $scope.$watchCollection('bookings.data', function(newBookings, oldBookings) {
                    $scope.bookingsList = getHeadings(newBookings);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the bookings are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the bookings list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.bookingsList = getHeadings($filter('filter')($scope.bookings.data, $scope.search));
        });
    }
})();
