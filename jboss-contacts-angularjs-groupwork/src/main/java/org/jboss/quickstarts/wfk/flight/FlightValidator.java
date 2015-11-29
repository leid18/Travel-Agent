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
package org.jboss.quickstarts.wfk.flight;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

/**
 * <p>This class provides methods to check flight objects against arbitrary requirements.</p>
 * 
 * @author Joshua Wilson
 * @see Flight
 * @see FlightRepository
 * @see javax.validation.Validator
 */
public class FlightValidator {
    @Inject
    private Validator validator;

    @Inject
    private FlightRepository crud;

    /**
     * <p>Validates the given flight object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing flight with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param flight The flight object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If flight with the same email already exists
     */
    void validateFlight(Flight flight) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Flight>> violations = validator.validate(flight);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        if (numberAlreadyExists(flight.getFlightNumber(), flight.getId())) {
            throw new ValidationException("Unique Flight Number Violation");
        }
        if (sameDepartureDestination(flight)) {
            throw new ValidationException("Same Departure Destination Violation");
        }
    }

    /**
     * <p>Checks if a flight with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the flight class.</p>
     * 
     * <p>Since Update will being using an email that is already in the database we need to make sure that it is the email
     * from the record being updated.</p>
     * 
     * @param email The email to check is unique
     * @param id The user id to check the email against if it was found
     * @return boolean which represents whether the email was found, and if so if it belongs to the user with id
     */
    boolean numberAlreadyExists(String flightNumber, Long id) {
        Flight flight = null;
        Flight flightWithID = null;
        try {
            flight = crud.findByNumber(flightNumber);
        } catch (NoResultException e) {
            // ignore
        }

        if (flight != null && id != null) {
            try {
                flightWithID = crud.findById(id);
                if (flightWithID != null && flightWithID.getFlightNumber().equals(id)) {
                    flight = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return flight != null;
    }
    
    boolean sameDepartureDestination(Flight flight) {
    	Flight flightWithID = null;
        if (flight != null ) {
            try {
                //flightWithID = crud.findById(id);
                if (flight.getDeparturePoint().equals(flight.getFlightDestination())) {
                    flightWithID = flight;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return flightWithID != null;
    }
}
