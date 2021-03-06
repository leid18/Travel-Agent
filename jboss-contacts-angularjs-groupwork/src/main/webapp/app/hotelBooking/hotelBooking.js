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
        .factory('HotelBooking', HotelBooking);

    HotelBooking.$inject = ['$resource'];

    function HotelBooking($resource) {
        //Declares Booking as a class of Resource, whose instance.$methods and Class.methods may be used
        // to easily interact with the RESTful rest/bookings endpoint via $http.
        var HotelBooking = $resource(
            'rest/hotelBookings/:hotelBookingId',
            {hotelBookingId: '@id'},
            {
                'update': {method: 'PUT'}
            }
        );
        //Declare public class variable to act as a pseudo-cache TODO: use proper $cacheFactor cache in Booking
        HotelBooking.data = [];
        return HotelBooking;
    }
})();