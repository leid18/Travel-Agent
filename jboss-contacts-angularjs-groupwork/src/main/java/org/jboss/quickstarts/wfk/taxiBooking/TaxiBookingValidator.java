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
package org.jboss.quickstarts.wfk.taxiBooking;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.ContactRepository;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxi.TaxiRepository;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 * 
 * @author Joshua Wilson
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
public class TaxiBookingValidator {
    @Inject
    private Validator validator;

    @Inject
    private TaxiBookingRepository crud;

    @Inject
    private ContactRepository crud3;
    
    @Inject
    private TaxiRepository crud2;
    
    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing booking with the same taxi in the same day registered, or the customer and taxi 
     * of the booking are not exist, it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If contact with the same email already exists
     */
    void validateBooking(TaxiBooking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<TaxiBooking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the combination of taxi and date
        if (taxiDateAlreadyExists(booking.getTaxiID(), booking.getDate(), booking.getId())) {
            throw new ValidationException("Unique taxi&date Violation");
        }
        
        // Check whether the taxi is exist
        if (taxiNotExist(booking.getTaxiID())) {
            throw new ValidationException("TaxiNotExist");
        }
        
        // Check whether the customer is exist
        if (customerNotExist(booking.getCustomerID())) {
            throw new ValidationException("CustomerNotExist");
        }

    }

    /**
     * <p>Checks if a booking with the same combination of taxi and date is already registered.
     * 
     * <p>Since Update will being using an booking that is already in the database we need to make sure that it is the combination of taxi and date
     * from the record being updated.</p>
     * 
     * @param taxiID The taxiID to check is unique
     * @param date The date to check is unique
     * @param id The user id to check
     * @return boolean which represents whether the combination of taxi and date was found, and if so if it belongs to the user with id
     */    
    boolean taxiDateAlreadyExists(Long taxiID, Date date, Long id) {
        List<TaxiBooking> booking1 = null;
        TaxiBooking bookingWithID = null;
        try {
        	booking1 = crud.findByTaxiID(taxiID);
        } catch (NoResultException e) {
            // ignore    
        }

            try {
            	for(TaxiBooking bb: booking1){
                if (bb.getDate().equals(date) && bb.getId()!=id) {
                	bookingWithID = bb;
                }
            	}
            } catch (NoResultException e) {
                // ignore
            }
        
        return bookingWithID != null;
    }
    
    /**
     * Check whether the taxi is exist with using the taxiID
     * @param taxiID the id of the taxi
     * @return boolean whether the taxi is found
     */
    boolean taxiNotExist(Long taxiID){
    	Taxi taxi = null;
    	try {
    		taxi = crud2.findById(taxiID);
    	}catch (NoResultException e) {
            // ignore
        }
    	return !(taxi != null);
    }
    
    /**
     * Check whether the customer is exist with using the customerID
     * @param customerID the id of the customer
     * @return boolean whether the customer is found
     */
    boolean customerNotExist(Long customerID){
    	Contact customer = null;
    	try {
    		customer = crud3.findById(customerID);
    	}catch (NoResultException e) {
            // ignore
        }
    	return !(customer != null);
    }
}
