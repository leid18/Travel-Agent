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
        .module('app.hotelBooking')
        .controller('HotelBookingController', HotelBookingController);

    HotelBookingController.$inject = ['$scope', '$routeParams', '$location', 'HotelBooking', 'messageBag'];

    function HotelBookingController($scope, $routeParams, $location, HotelBooking, messageBag) {
        //Assign Booking service to $scope variable
        $scope.hotelBookings = HotelBooking;
        //Assign messageBag service to $scope variable
        $scope.messages = messageBag;

        $scope.date = Date.now();

        $scope.hotelBooking = {};
        $scope.create = true;

        //If $routeParams has :bookingID then load the specified booking, and display edit controls on bookingForm
        if($routeParams.hasOwnProperty('hotelBookingId')) {
            $scope.hotelBooking = $scope.hotelBookings.get({hotelBookingId: $routeParams.hotelBookingId});
            $scope.create = false;
        }


        // Define a reset function, that clears the prototype new Booking object, and
        // consequently, the form
        $scope.reset = function() {
            // Sets the form to it's pristine state
            if($scope.hotelBookingForm) {
                $scope.hotelBookingForm.$setPristine();
            }

            // Clear input fields. If $scope.booking was set to an empty object {},
            // then invalid form values would not be reset.
            // By specifying all properties, input fields with invalid values are also reset.
            $scope.hotelBooking = {customer: "", hotel: "", date: ""};

            // clear messages
            $scope.messages.clear();
        };

        // Define an addBooking() function, which creates a new booking via the REST service,
        // using those details provided and displaying any error messages
        $scope.addHotelBooking = function() {
            $scope.messages.clear();

            $scope.hotelBookings.save($scope.hotelBooking,
                //Successful query
                function(data) {

                    // Update the list of bookings
                    $scope.hotelBookings.data.push(data);

                    // Clear the form
                    $scope.reset();

                    //Add success message
                    $scope.messages.push('success', 'hotelBooking added');
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
        $scope.saveHotelBooking = function() {
            $scope.messages.clear();
            $scope.hotelBooking.$update(
                //Successful query
                function(data) {
                    //Find the booking locally by id and update it
                    var idx = _.findIndex($scope.hotelBookings.data, {'id': $scope.hotelBooking.id});
                    $scope.hotelBookings.data[idx] = data;
                    //Add success message
                    $scope.messages.push('success', 'hotel Booking saved');
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
        $scope.deleteHotelBooking = function() {
            $scope.messages.clear();

            //Send the DELETE request
            $scope.hotelBooking.$delete(
                //Successful query
                function() {
                    //TODO: Fix the wonky imitation of a cache by replacing with a proper $cacheFactory cache.
                    //Find the contact locally by id and remove it
                    var idx = _.findIndex($scope.hotelBookings.data, {'id': $scope.hotelBooking.id});
                    $scope.hotelBookings.data.splice(idx, 1);
                    //Mark success on the editContact form
                    $scope.messages.push('success', 'Hotel Booking removed');
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