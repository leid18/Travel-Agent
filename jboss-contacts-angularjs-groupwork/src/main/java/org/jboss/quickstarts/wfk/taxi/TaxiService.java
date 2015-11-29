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


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 * @author Joshua Wilson
 * @see TaxiValidator
 * @see TaxiRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class TaxiService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private TaxiValidator validator;

    @Inject
    private TaxiRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sorted alphabetically by registration.<p/>
     * 
     * @return List of Taxi objects
     */
    List<Taxi> findAllOrderedByRegistration() {
        return crud.findAllOrderedByRegistration();
    }

    /**
     * <p>Returns a single Taxi object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Taxi to be returned
     * @return The Taxi with the specified id
     */
    Taxi findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Taxi object, specified by a String registration.</p>
     *
     * <p>If there is more than one Taxi with the specified registration, only the first encountered will be returned.<p/>
     * 
     * @param registration The registration field of the Taxi to be returned
     * @return The first Taxi with the specified registration
     */
    Taxi findByRegistration(String registration) {
        return crud.findByRegistration(registration);
    }

    /**
     * <p>Writes the provided Taxi object to the application database.<p/>
     *
     * <p>Validates the data in the provided Taxi object using a {@link TaxiValidator} object.<p/>
     * 
     * @param taxi The Taxi object to be written to the database using a {@link TaxiRepository} object
     * @return The Taxi object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    ArrayList<Taxi> create() throws ConstraintViolationException, ValidationException, Exception {
    	ArrayList<Taxi> taxis = new ArrayList<Taxi>();
        log.info("FlightService.create() - Creating ");
        
        //Perform a rest call to get the state of the contact from the allareacodes.com API
        URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("taxi-000276537.rhcloud.com/")
        .setPath("/rest/taxis/")
        .build();
		HttpGet req = new HttpGet(uri);
		CloseableHttpResponse response = httpClient.execute(req);
		String responseBody = EntityUtils.toString(response.getEntity());
		JSONArray taxiArray = new JSONArray(responseBody);
		for(int i=0;i<taxiArray.length();i++){
			Taxi taxi = new Taxi();
			JSONObject responseJson = taxiArray.getJSONObject(i);
		    taxi.setRegistration(responseJson.getString("registration"));
		    taxi.setSeats(Integer.parseInt(responseJson.getString("seats")));
		    taxi.setState(responseJson.getString("state"));
		    validator.validateTaxi(taxi);
		    Taxi temp = crud.create(taxi);
		    taxis.add(temp);
		}
		HttpClientUtils.closeQuietly(response);
		
		return taxis;
    }

}
