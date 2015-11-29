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

import java.net.URI;
import java.text.SimpleDateFormat;
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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <p>This class exposes the functionality of {@link BookingService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the Booking resource is rest/bookings .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Joshua Wilson
 * @see BookingService
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
@Path("/taxiBookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class TaxiBookingRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private TaxiBookingService service;
    
    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    /**
     * <p>Search for and return all the Bookings.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of Bookings
     */
/*    @GET
    public Response retrieveAllBookings() {
        List<TaxiBooking> booking = service.findAllOrderedByID();
        return Response.ok(booking).build();
    }*/
    
    @GET
    public Response retrieveAllBookings() throws ConstraintViolationException, ValidationException, Exception{
    	ArrayList<TaxiBooking> taxiBookings = new ArrayList<TaxiBooking>();
        //Perform a rest call to get the state of the contact from the allareacodes.com API
        URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("taxi-000276537.rhcloud.com/")
        .setPath("/rest/bookings/")
        .build();
		HttpGet req = new HttpGet(uri);
		CloseableHttpResponse response = httpClient.execute(req);
		String responseBody = EntityUtils.toString(response.getEntity());
		JSONArray taxiArray = new JSONArray(responseBody);
		
		for(int i=0;i<taxiArray.length();i++){
	    JSONObject responseJson = taxiArray.getJSONObject(i);
		JSONObject responseJson2 = responseJson.getJSONObject("customer");
		JSONObject responseJson3 = responseJson.getJSONObject("taxi");
		
		Contact c = new Contact();
		c.setName(responseJson2.getString("firstName"));
		c.setEmail(responseJson2.getString("email"));
		c.setPhoneNumber(responseJson2.getString("phoneNumber"));
		
		Taxi t = new Taxi();
		t.setRegistration(responseJson3.getString("registration"));
		t.setSeats(Integer.parseInt(responseJson3.getString("seats")));
		
		TaxiBooking taxiBooking = new TaxiBooking();
		taxiBooking.setCustomer(c);
		taxiBooking.setTaxi(t);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		taxiBooking.setDate(sdf.parse(responseJson.getString("date")));
		
		taxiBookings.add(taxiBooking);
		}
//		createBooking(taxiBooking);
		
		HttpClientUtils.closeQuietly(response);
		// Write the contact to the database.
		return Response.ok(taxiBookings).build();
    }
    
    /**
     * <p>Search for and return a Booking identified by id.</p>
     * 
     * @param id The long parameter value provided as a Booking's id
     * @return A Response containing a single Booking
     */
/*    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveBookingById(@PathParam("id") long id) {
    	TaxiBooking booking = service.findById(id);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Booking = " + booking.getCustomer() + " " + booking.getTaxi() + " "+booking.getDate() + " " + booking.getId());        
        return Response.ok(booking).build();
    }*/
    
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveBookingByID(@PathParam("id") long id) throws ConstraintViolationException, ValidationException, Exception{
    	TaxiBooking taxiBooking = new TaxiBooking();
        //Perform a rest call to get the state of the contact from the allareacodes.com API
        URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("taxi-000276537.rhcloud.com/")
        .setPath("/rest/bookings/"+id)
        .build();
		HttpGet req = new HttpGet(uri);
		CloseableHttpResponse response = httpClient.execute(req);
		String responseBody = EntityUtils.toString(response.getEntity());
		JSONObject responseJson = new JSONObject(responseBody);
		JSONObject responseJson2 = responseJson.getJSONObject("customer");
		JSONObject responseJson3 = responseJson.getJSONObject("taxi");
		
		Contact c = new Contact();
//		c.setId(Long.parseLong(responseJson2.getString("id")));
		c.setName(responseJson2.getString("firstName"));
		c.setEmail(responseJson2.getString("email"));
		c.setPhoneNumber(responseJson2.getString("phoneNumber"));
		
		Taxi t = new Taxi();
//		t.setId(Long.parseLong(responseJson3.getString("id")));
		t.setRegistration(responseJson3.getString("registration"));
		t.setSeats(Integer.parseInt(responseJson3.getString("seats")));
		
		taxiBooking.setCustomer(c);
		taxiBooking.setTaxi(t);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		taxiBooking.setDate(sdf.parse(responseJson.getString("date")));
		
//		createBooking(taxiBooking);
		
		HttpClientUtils.closeQuietly(response);
		// Write the contact to the database.
		return Response.ok(taxiBooking).build();
    }

    /**
     * <p>Search for and return a Booking identified by customerID.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between customerIDs and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate customerID.</p>
     *
     *
     * @param customerID The string parameter value provided as a Booking's customerID
     * @return A Response containing a single Booking
     */
    @GET
    @Path("/customer/{customerID:[0-9]+}")
    public Response retrieveBookingsByCustomer(@PathParam("customerID") Long customerID) {
        List<TaxiBooking> booking = service.findByCustomerID(customerID);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(booking).build();
    }
    
    /**
     * <p>Search for and return a Booking identified by taxiID.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between taxiIDs and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate taxiID.</p>
     *
     *
     * @param taxiID The string parameter value provided as a Booking's taxiID
     * @return A Response containing a single Booking
     */
    @GET
    @Path("/taxi/{taxiID:[0-9]+}")
    public Response retrieveBookingsByTaxi(@PathParam("taxiID") Long taxiID) {
        List<TaxiBooking> booking = service.findByTaxiID(taxiID);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }        
        return Response.ok(booking).build();
    }
    


    /**
     * <p>Creates a new booking from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>created</i> via {@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createBooking(TaxiBooking booking) {
        log.info("createBooking started. Booking = " + booking.getCustomer() + " " + booking.getTaxi() + " "+booking.getDate() + " " + booking.getId());
        if (booking == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new Booking.
            service.create(booking);

            // Create a "Resource Created" 201 Response and pass the contact back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);
            
            log.info("createBooking completed. Booking = " + booking.getCustomer() + " " + booking.getTaxi() + " "+booking.getDate() + " " + booking.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            if(e.toString().contains("taxi&date"))
                responseObj.put("taxi&date", "That taxi and date are already used, please use a unique taxi&date");
               if(e.toString().contains("TaxiNotExist"))
                   responseObj.put("TaxiNotExist", "That taxi is not exist, please use an available taxi id");
               if(e.toString().contains("CustomerNotExist"))
                   responseObj.put("CustomerNotExist", "That customer is not exist, please use an available customer id");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Updates a booking with the ID provided in the Booking. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>updated</i> via {@link BookingService#update(Booking)}
     * @param id The long parameter value provided as the id of the Booking to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateBooking(@PathParam("id") long id, TaxiBooking booking) {
        if (booking == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateBooking started. Booking = " + booking.getCustomer() + " " + booking.getTaxi() + " "+booking.getDate() + " " + booking.getId());

        if (booking.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The booking ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(booking.getId()) == null) {
            // Verify if the booking exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the Booking.
            service.update(booking);

            // Create an OK Response and pass the contact back in case it is needed.
            builder = Response.ok(booking);

            log.info("updateBooking completed. Booking = " + booking.getCustomer() + " " + booking.getTaxi() + " "+booking.getDate() + " " + booking.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            if(e.toString().contains("taxi&date"))
            responseObj.put("taxi&date", "That taxi&date is already used, please use a unique taxi&date");
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/ContactRESTService.java line 242.");
            if(e.toString().contains("TaxiNotExist"))
                responseObj.put("TaxiNotExist", "That taxi is not exist, please use an available taxi id");
            if(e.toString().contains("CustomerNotExist"))
                responseObj.put("CustomerNotExist", "That customer is not exist, please use an available customer id");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Deletes a booking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the Booking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteBooking(@PathParam("id") Long id) {
        log.info("deleteTaxi started. Taxi ID = " + id);
        Response.ResponseBuilder builder = null;

        try {
        	TaxiBooking booking = service.findById(id);
            if (booking != null) {
                service.delete(booking);
            } else {
                log.info("TaxiRESTService - deleteTaxi - No taxi with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteBooking completed. Booking = " + booking.getCustomer() + " " + booking.getTaxi() + " "+booking.getDate() + " " + booking.getId());
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
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
