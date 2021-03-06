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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.NoSuchEntityException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

/**
 * <p>This class exposes the functionality of {@link FlightService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the Contact resource is rest/flight .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Joshua Wilson
 * @see FlightService
 * @see javax.ws.rs.core.Response
 */
/*
 * The Path annotation defines this as a REST Web Service using JAX-RS.
 * 
 * By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they 
 * can be overridden by adding the Consumes or Produces annotations to the individual method.
 * 
 * It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow 
 * transaction demarcation when accessing the database." - Antonio Goncalves
 * 
 */
@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class FlightRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private FlightService service;
    
    /**
     * <p>Search for and return all the flight.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of flight
     */
    @GET
    public Response retrieveAllFlight() {
    	try {
			service.create();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List<Flight> flights = service.findAllOrderedByName();
        return Response.ok(flights).build();
    }

    /**
     * <p>Search for and return a Contact identified by email address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between email addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate email addresses.</p>
     *
     *
     * @param email The string parameter value provided as a Contact's email
     * @return A Response containing a single Contact
     */
    @GET
    @Path("/{flightNumber:[a-zA-Z]{3}\\d{2}}")
    public Response retrieveFlightByNumber(@PathParam("flightNumber") String flightNumber) {
        Flight flight;
        try {
            flight = service.findByNumber(flightNumber);
        } catch (NoResultException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(flight).build();
    }
    
    /**
     * <p>Search for and return a Contact identified by id.</p>
     * 
     * @param id The long parameter value provided as a Contact's id
     * @return A Response containing a single Contact
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveFlightById(@PathParam("id") long id) {
        Flight flight = service.findById(id);
        if (flight == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Flight = " + flight.getFlightNumber() + " " + flight.getDeparturePoint() + " " + flight.getFlightDestination() + " " + flight.getId());
        
        return Response.ok(flight).build();
    }

    /**
     * <p>Creates a new contact from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param contact The Contact object, constructed automatically from JSON input, to be <i>created</i> via {@link FlightService#create(Flight)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createFlight() {
        
        Response.ResponseBuilder builder = null;
        ArrayList<Flight> getFlights = new ArrayList<Flight>();
        try {
			getFlights = service.create();
			for (Flight flight: getFlights){
		        try {
		
		            // Create a "Resource Created" 201 Response and pass the contact back in case it is needed.
		            builder = Response.status(Response.Status.CREATED).entity(flight);
		            
		            log.info("createFlight completed. Flight = " + flight.getFlightNumber() + " " + flight.getDeparturePoint() + " " + flight.getFlightDestination()
		            		+ " " + flight.getId());
		        } catch (ConstraintViolationException ce) {
		            log.info("ConstraintViolationException - " + ce.toString());
		            // Handle bean validation issues
		            builder = createViolationResponse(ce.getConstraintViolations());
		        } catch (ValidationException e) {
		            log.info("ValidationException - " + e.toString());
		            // Handle the unique constrain violation
		            Map<String, String> responseObj = new HashMap<String, String>();
		            responseObj.put("flightnumber", "That flight number is already used, please use a unique flight number");
		            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		        } catch (PersistenceException e){
		        	log.info("PersistenceException - " + e.toString());
		            // Handle the unique constrain violation
		            Map<String, String> responseObj = new HashMap<String, String>();
		            responseObj.put("flightdestination", "Destination should be different from departure");
		            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		        }catch (Exception e) {
		            log.info("Exception - " + e.toString());
		            // Handle generic exceptions
		            Map<String, String> responseObj = new HashMap<String, String>();
		            responseObj.put("error", e.getMessage());
		            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		        } 
	        }
		} catch (ConstraintViolationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ValidationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        

        return builder.build();
    }

    
    /**
     * <p>Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can be used
     * by calling client applications to display violations to users.<p/>
     * 
     * @param violations A Set of violations that need to be reported in the Response body
     * @return A Bad Request (400) Response containing all violation messages
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }


}
