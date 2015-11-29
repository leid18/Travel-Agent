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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.flight.FlightRepository;
import org.jboss.quickstarts.wfk.flight.FlightRESTService;
import org.jboss.quickstarts.wfk.flight.FlightService;
import org.jboss.quickstarts.wfk.flight.FlightValidator;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Flight creation functionality
 * (see {@link FlightRESTService#createFlight(Flight) createFlight(Flight)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see FlightRESTService
 */
@RunWith(Arquillian.class)
public class FlightRegistrationTest {

    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        //HttpComponents and org.JSON are required by FlightService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Flight.class, 
                        FlightRESTService.class, 
                        FlightRepository.class, 
                        FlightValidator.class, 
                        FlightService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    FlightRESTService flightRESTService;
    
    @Inject
    @Named("logger") Logger log;
    
    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Response response = flightRESTService.createFlight();

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New flight was persisted and returned status " + response.getStatus());
    }
    

/**
    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Flight flight = createFlightInstance("Jack", "jack@mailinator.com", "06464646464");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New flight was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Flight flight = createFlightInstance("", "", "");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("Jane", "jane@mailinator.com", "06464646464");
        flightRESTService.createFlight(flight);

        // Register a different user with the same email
        Flight anotherFlight = createFlightInstance("John", "jane@mailinator.com", "06464646464");
        Response response = flightRESTService.createFlight(anotherFlight);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate flight register attempt failed with return code " + response.getStatus());
    }

    @Test
    @InSequence(4)
    public void testDeleteFlight() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("Jaa", "jaa@mailinator.com", "06464646464");
        flightRESTService.createFlight(flight);

        Response response = flightRESTService.deleteFlight(flight.getId());

        assertEquals("Unexpected response status", 400, response.getStatus());
        log.info("Delete flight register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(5)
    public void testRetrieveFlight() throws Exception {
        Response response = flightRESTService.retrieveAllFlights();

        assertEquals("Unexpected response status", 200, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        log.info("Retrieve flight register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(6)
    public void testNameValidation() throws Exception {
        Flight flight = createFlightInstance("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "hfghi@qq.com", "01111111111");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(7)
    public void testEmailValidation() throws Exception {
        Flight flight = createFlightInstance("ffffff", "hfghi444qq.com", "01111111111");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testPhoneNumberValidation() throws Exception {
        Flight flight = createFlightInstance("ffffff", "hfghigg@qq.com", "xxxxxx");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight register attempt failed with return code " + response.getStatus());
    }
    
/*    @SuppressWarnings("unchecked")
    @Test
    @InSequence(9)
    public void testChangeID() throws Exception {
        Flight flight = createFlightInstance("ffffff", "hfghikk@qq.com", "09999999999");
        flightRESTService.createFlight(flight);
        Flight flight2 = createFlightInstance("fjjffff", "yyghi@qq.com", "07777777777");
        flightRESTService.createFlight(flight2);

        Response response = flightRESTService.updateFlight(4, flight2);
        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight register attempt failed with return code " + response.getStatus());
    }*/
 /**   
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testIDValidation() throws Exception {
        Flight flight = createFlightInstanceWithID((long)9,"ffffff", "hfghig33g@qq.com", "09888888888");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight register attempt failed with return code " + response.getStatus());
    }
    
    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.flight.Flight Flight} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param name The name of the Flight being created
     * @param email     The email address of the Flight being created
     * @param phone     The phone number of the Flight being created
     * @return The Flight object create
     */
    
    private Flight createFlightInstance(String flightNumber, String departurePoint, String flightDestination) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setDeparturePoint(departurePoint);
        flight.setFlightDestination(flightDestination);
        return flight;
    }
    
    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.flight.Flight Flight} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param id The id of the Flight being created
     * @param name The name of the Flight being created
     * @param email     The email address of the Flight being created
     * @param phone     The phone number of the Flight being created
     * @return The Flight object create
     */
    private Flight createFlightInstanceWithID(Long id, String flightNumber, String departurePoint, String flightDestination) {
        Flight flight = new Flight();
        flight.setId(id);
        flight.setFlightNumber(flightNumber);
        flight.setDeparturePoint(departurePoint);
        flight.setFlightDestination(flightDestination);
        return flight;
    }
}
