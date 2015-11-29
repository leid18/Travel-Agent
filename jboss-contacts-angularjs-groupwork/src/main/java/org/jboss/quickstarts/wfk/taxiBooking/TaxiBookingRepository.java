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

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.ContactRepository;
import org.jboss.quickstarts.wfk.taxi.TaxiRepository;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and Booking the Service/Control layer (see {@link ContactService} with the
 * Domain/Entity Object (see {@link Contact}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Joshua Wilson
 * @see Booking
 * @see javax.persistence.EntityManager
 */
public class TaxiBookingRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by id.</p>
     * 
     * @return List of Booking objects
     */
    List<TaxiBooking> findAllOrderedByID() {
        TypedQuery<TaxiBooking> query = em.createNamedQuery(TaxiBooking.FIND_ALL, TaxiBooking.class); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    TaxiBooking findById(Long id) {
        return em.find(TaxiBooking.class, id);
    }

    /**
     * <p>Returns a List of Booking objects, specified by a Long customerID.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param customerID The customerID field of the Booking to be returned
     * @return The first Booking with the specified customerID
     */
    List<TaxiBooking> findByCustomerID(Long customerID) {
        TypedQuery<TaxiBooking> query = em.createNamedQuery(TaxiBooking.FIND_BY_CUSTOMER, TaxiBooking.class).setParameter("customerID", customerID); 
        return query.getResultList();
    }
    
    /**
     * <p>Returns a List of Booking objects, specified by a Long taxiID.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param taxiID The taxiID field of the Booking to be returned
     * @return The first Booking with the specified taxiID
     */
    List<TaxiBooking> findByTaxiID(Long taxiID) {
        TypedQuery<TaxiBooking> query = em.createNamedQuery(TaxiBooking.FIND_BY_TAXI, TaxiBooking.class).setParameter("taxiID", taxiID); 
        return query.getResultList();
    }
    
    /**
     * <p>Returns a single Booking object, specified by a Date date.<p/>
     *
     * @param date The date field of the Booking to be returned
     * @return The Booking with the specified date
     */
    TaxiBooking findByDate(Date date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TaxiBooking> criteria = cb.createQuery(TaxiBooking.class);
        Root<TaxiBooking> booking = criteria.from(TaxiBooking.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(contact).where(cb.equal(contact.get(Contact_.firstName), firstName));
        criteria.select(booking).where(cb.equal(booking.get("date"), date));
        return em.createQuery(criteria).getSingleResult();
    }
    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    TaxiBooking create(TaxiBooking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.create() - Creating " + booking.getTaxi() + " " + booking.getDate());
        
        // Write the contact to the database.
        em.persist(booking);
        
        return booking;
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param booking The Booking object to be merged with an existing Booking
     * @return The Booking that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    TaxiBooking update(TaxiBooking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.update() - Updating " + booking.getTaxi() + " " + booking.getDate());
        
        // Either update the contact or add it if it can't be found.
        em.merge(booking);
        
        return booking;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    TaxiBooking delete(TaxiBooking booking) throws Exception {
        log.info("BookingRepository.delete() - Deleting " + booking.getTaxi() + " " + booking.getDate());
        
        if (booking.getId() != null) {
            /*
             * The Hibernate session (aka EntityManager's persistent context) is closed and invalidated after the commit(), 
             * because it is bound to a transaction. The object goes into a detached status. If you open a new persistent 
             * context, the object isn't known as in a persistent state in this new context, so you have to merge it. 
             * 
             * Merge sees that the object has a primary key (id), so it knows it is not new and must hit the database 
             * to reattach it. 
             * 
             * Note, there is NO remove method which would just take a primary key (id) and a entity class as argument. 
             * You first need an object in a persistent state to be able to delete it.
             * 
             * Therefore we merge first and then we can remove it.
             */
            em.remove(em.merge(booking));
            
        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }
        
        return booking;
    }

}
