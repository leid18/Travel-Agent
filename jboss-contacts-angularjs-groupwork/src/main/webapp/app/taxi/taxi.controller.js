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
        .module('app.taxi')
        .controller('TaxiController', TaxiController);

    TaxiController.$inject = ['$scope', '$routeParams', '$location', 'Taxi', 'messageBag'];

    function TaxiController($scope, $routeParams, $location, Taxi, messageBag) {
        //Assign Taxi service to $scope variable
        $scope.taxis = Taxi;
        //Assign messageBag service to $scope variable
        $scope.messages = messageBag;

        $scope.date = Date.now();

        $scope.taxi = {};
        $scope.create = true;

        //If $routeParams has :taxiId then load the specified taxi, and display edit controls on taxiForm
        if($routeParams.hasOwnProperty('taxiId')) {
            $scope.taxi = $scope.taxis.get({taxiId: $routeParams.taxiId});
            $scope.create = false;
        }


        // Define a reset function, that clears the prototype new Taxi object, and
        // consequently, the form
        $scope.reset = function() {
            // Sets the form to it's pristine state
            if($scope.taxiForm) {
                $scope.taxiForm.$setPristine();
            }

            // Clear input fields. If $scope.contact was set to an empty object {},
            // then invalid form values would not be reset.
            // By specifying all properties, input fields with invalid values are also reset.
            $scope.taxi = {registration: "", seats: ""};

            // clear messages
            $scope.messages.clear();
        };

        // Define an addTaxi() function, which creates a new taxi via the REST service,
        // using those details provided and displaying any error messages
        $scope.addTaxi = function() {
            $scope.messages.clear();

            $scope.taxis.save($scope.taxi,
                //Successful query
                function(data) {

                    // Update the list of taxis
                    $scope.taxis.data.push(data);

                    // Clear the form
                    $scope.reset();

                    //Add success message
                    $scope.messages.push('success', 'Taxi added');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };

        // Define a saveTaxi() function, which saves the current contact using the REST service
        // and displays any error messages
        $scope.saveTaxi = function() {
            $scope.messages.clear();
            $scope.taxi.$update(
                //Successful query
                function(data) {
                    //Find the taxi locally by id and update it
                    var idx = _.findIndex($scope.taxis.data, {'id': $scope.taxi.id});
                    $scope.taxis.data[idx] = data;
                    //Add success message
                    $scope.messages.push('success', 'Taxi saved');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            )
        };

        // Define a deleteTaxi() function, which saves the current taxi using the REST service
        // and displays any error messages
        $scope.deleteTaxi = function() {
            $scope.messages.clear();

            //Send the DELETE request
            $scope.taxi.$delete(
                //Successful query
                function() {
                    //TODO: Fix the wonky imitation of a cache by replacing with a proper $cacheFactory cache.
                    //Find the taxi locally by id and remove it
                    var idx = _.findIndex($scope.taxis.data, {'id': $scope.taxi.id});
                    $scope.taxis.data.splice(idx, 1);
                    //Mark success on the editTaxi form
                    $scope.messages.push('success', 'Taxi removed');
                    //Redirect back to /home
                    $location.path('/home');
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