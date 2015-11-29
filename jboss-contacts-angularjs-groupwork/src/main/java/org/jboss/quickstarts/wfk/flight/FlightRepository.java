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

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link FlightService} with the
 * Domain/Entity Object (see {@link Flight}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Joshua Wilson
 * @see Flight
 * @see javax.persistence.EntityManager
 */
public class FlightRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link Flight} objects, sorted alphabetically by last name.</p>
     * 
     * @return List of Flight objects
     */
    List<Flight> findAllOrderedByName() {
        TypedQuery<Flight> query = em.createNamedQuery(Flight.FIND_ALL, Flight.class); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single Flight object, specified by a Long id.<p/>
     *
     * @param id The  field of the Flight to be returned
     * @return The Flight with the specified id
     */
    public Flight findById(Long id) {
        return em.find(Flight.class, id);
    }

    /**
     * <p>Returns a single Flight object, specified by a String email.</p>
     *
     * <p>If there is more than one Flight with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the Flight to be returned
     * @return The first Flight with the specified email
     */
    Flight findByNumber(String flightNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Flight> criteria = cb.createQuery(Flight.class);
        Root<Flight> flight = criteria.from(Flight.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(flight).where(cb.equal(flight.get(Flight_.firstName), firstName));
        criteria.select(flight).where(cb.equal(flight.get("flightNumber"), flightNumber));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Returns a single Flight object, specified by a String firstName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param firstName The firstName field of the Flight to be returned
     * @return The first Flight with the specified firstName
     */
    Flight findByDeparture(String departurePoint) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Flight> criteria = cb.createQuery(Flight.class);
        Root<Flight> flight = criteria.from(Flight.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(flight).where(cb.equal(flight.get(Flight_.firstName), firstName));
        criteria.select(flight).where(cb.equal(flight.get("departurePoint"), departurePoint));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Returns a single Flight object, specified by a String lastName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param lastName The lastName field of the Flight to be returned
     * @return The first Flight with the specified lastName
     */
    Flight findByDestination(String flightDestination) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Flight> criteria = cb.createQuery(Flight.class);
        Root<Flight> flight = criteria.from(Flight.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(flight).where(cb.equal(flight.get(Flight_.lastName), lastName));
        criteria.select(flight).where(cb.equal(flight.get("flightDestination"), flightDestination));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Persists the provided Flight object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param flight The Flight object to be persisted
     * @return The Flight object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Flight create(Flight flight) throws ConstraintViolationException, ValidationException,PersistenceException, Exception {
        log.info("FlightRepository.create() - Creating " + flight.getFlightNumber() + " " + flight.getDeparturePoint() + " " + flight.getFlightDestination());
        
        // Write the flight to the database.
        em.persist(flight);
        
        return flight;
    }

    /**
     * <p>Updates an existing Flight object in the application database with the provided Flight object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param flight The Flight object to be merged with an existing Flight
     * @return The Flight that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Flight update(Flight flight) throws ConstraintViolationException, ValidationException,PersistenceException, Exception {
        log.info("FlightRepository.update() - Updating " + flight.getFlightNumber() + " " + flight.getDeparturePoint() + " " + flight.getFlightDestination());
        
        // Either update the flight or add it if it can't be found.
        em.merge(flight);
        
        return flight;
    }

    /**
     * <p>Deletes the provided Flight object from the application database if found there</p>
     *
     * @param flight The Flight object to be removed from the application database
     * @return The Flight object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Flight delete(Flight flight) throws Exception {
        log.info("FlightRepository.delete() - Deleting " + flight.getFlightNumber() + " " + flight.getDeparturePoint() + " " + flight.getFlightDestination());
        
        if (flight.getId() != null) {
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
            em.remove(em.merge(flight));
            
        } else {
            log.info("FlightRepository.delete() - No ID was found so can't Delete.");
        }
        
        return flight;
    }

}
