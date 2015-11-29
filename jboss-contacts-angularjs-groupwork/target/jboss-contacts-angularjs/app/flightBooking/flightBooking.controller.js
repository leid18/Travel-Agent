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
        .module('app.flightBooking')
        .controller('FlightBookingController', FlightBookingController);

    FlightBookingController.$inject = ['$scope', '$routeParams', '$location', 'FlightBooking', 'messageBag'];

    function FlightBookingController($scope, $routeParams, $location, FlightBooking, messageBag) {
        //Assign Booking service to $scope variable
        $scope.flightBookings = FlightBooking;
        //Assign messageBag service to $scope variable
        $scope.messages = messageBag;

        $scope.date = Date.now();

        $scope.flightBooking = {};
        $scope.create = true;

        //If $routeParams has :bookingID then load the specified booking, and display edit controls on bookingForm
        if($routeParams.hasOwnProperty('flightBookingId')) {
            $scope.flightBooking = $scope.flightBookings.get({flightBookingId: $routeParams.flightBookingId});
            $scope.create = false;
        }


        // Define a reset function, that clears the prototype new Booking object, and
        // consequently, the form
        $scope.reset = function() {
            // Sets the form to it's pristine state
            if($scope.flightBookingForm) {
                $scope.flightBookingForm.$setPristine();
            }

            // Clear input fields. If $scope.booking was set to an empty object {},
            // then invalid form values would not be reset.
            // By specifying all properties, input fields with invalid values are also reset.
            $scope.flightBooking = {customer: "", flight: "", date: ""};

            // clear messages
            $scope.messages.clear();
        };

        // Define an addBooking() function, which creates a new booking via the REST service,
        // using those details provided and displaying any error messages
        $scope.addFlightBooking = function() {
            $scope.messages.clear();

            $scope.flightBookings.save($scope.flightBooking,
                //Successful query
                function(data) {

                    // Update the list of bookings
                    $scope.flightBookings.data.push(data);

                    // Clear the form
                    $scope.reset();

                    //Add success message
                    $scope.messages.push('success', 'Flight Booking added');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };

        // Define a saveBooking() function, which saves the current contact using the REST service
        // and displays any error messages
        $scope.saveFlightBooking = function() {
            $scope.messages.clear();
            $scope.flightBooking.$update(
                //Successful query
                function(data) {
                    //Find the booking locally by id and update it
                    var idx = _.findIndex($scope.flightBookings.data, {'id': $scope.flightBooking.id});
                    $scope.flightBookings.data[idx] = data;
                    //Add success message
                    $scope.messages.push('success', 'flightBooking saved');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            )
        };

        // Define a deleteBooking() function, which saves the current booking using the REST service
        // and displays any error messages
        $scope.deleteFlightBooking = function() {
            $scope.messages.clear();

            //Send the DELETE request
            $scope.flightBooking.$delete(
                //Successful query
                function() {
                    //TODO: Fix the wonky imitation of a cache by replacing with a proper $cacheFactory cache.
                    //Find the contact locally by id and remove it
                    var idx = _.findIndex($scope.flightBookings.data, {'id': $scope.flightBooking.id});
                    $scope.flightBookings.data.splice(idx, 1);
                    //Mark success on the editContact form
                    $scope.messages.push('success', 'flightBooking removed');
                    //Redirect back to /bookingHome
                    $location.path('/bookingHome');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };
    }
})();