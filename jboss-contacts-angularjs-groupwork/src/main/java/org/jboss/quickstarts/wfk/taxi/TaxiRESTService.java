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
package org.jboss.quickstarts.wfk.taxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;

/**
 * <p>This class exposes the functionality of {@link TaxiService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the Taxi resource is rest/taxis .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Joshua Wilson
 * @see TaxiService
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
@Path("/taxis")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class TaxiRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private TaxiService service;
    
    /**
     * <p>Search for and return all the Taxis.  They are sorted alphabetically by registration.</p>
     * 
     * @return A Response containing a list of Taxis
     */
    @GET
    public Response retrieveAllTaxis() {
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
    	List<Taxi> taxis = service.findAllOrderedByRegistration();
        return Response.ok(taxis).build();
    }

    /**
     * <p>Search for and return a Taxi identified by registration.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between registration and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate registration.</p>
     *
     *
     * @param email The string parameter value provided as a Taxi's registration
     * @return A Response containing a single Taxi
     */
    @GET
    @Path("/{registration:^[A-Za-z0-9]{7}$}")
    public Response retrieveTaxisByRegistration(@PathParam("registration") String registration) {
    	Taxi taxi;
        try {
            taxi = service.findByRegistration(registration);
        } catch (NoResultException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(taxi).build();
    }
    
    /**
     * <p>Search for and return a Taxi identified by id.</p>
     * 
     * @param id The long parameter value provided as a Taxi's id
     * @return A Response containing a single Taxi
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveTaxiById(@PathParam("id") long id) {
        Taxi taxi = service.findById(id);
        if (taxi == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Taxi = " + taxi.getRegistration() + " " + taxi.getSeats() + " "+taxi.getId());
        
        return Response.ok(taxi).build();
    }

    /**
     * <p>Creates a new Taxi from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param taxi The Taxi object, constructed automatically from JSON input, to be <i>created</i> via {@link TaxiService#create(Taxi)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createTaxi() {
        
        Response.ResponseBuilder builder = null;
        List<Taxi> taxis = new ArrayList<Taxi>();
        try {
			taxis = service.create();
			for (Taxi taxi: taxis){
		        try {
		
		            // Create a "Resource Created" 201 Response and pass the contact back in case it is needed.
		            builder = Response.status(Response.Status.CREATED).entity(taxi);
		            
		            log.info("createTaxi completed. Taxi = " + taxi.getRegistration() + " " + taxi.getSeats());
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
