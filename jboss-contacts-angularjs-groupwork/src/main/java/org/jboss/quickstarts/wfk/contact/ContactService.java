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
package org.jboss.quickstarts.wfk.contact;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
 * @see ContactValidator
 * @see ContactRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class ContactService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private ContactRepository crud;
    
    @Inject
    private ContactValidator validator;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;

    List<Contact> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }
    /**
     * <p>Returns a single Contact object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Contact to be returned
     * @return The Contact with the specified id
     */
    Contact findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Contact object, specified by a String email.</p>
     *
     * <p>If there is more than one Contact with the specified email, only the first encountered will be returned.<p/>
     * 
     * @param email The email field of the Contact to be returned
     * @return The first Contact with the specified email
     */
    Contact findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * <p>Returns a single Contact object, specified by a String firstName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param firstName The firstName field of the Contact to be returned
     * @return The first Contact with the specified firstName
     */
    Contact findByName(String name) {
        return crud.findByName(name);
    }

    /**
     * <p>Writes the provided Contact object to the application database.<p/>
     *
     * <p>Validates the data in the provided Contact object using a {@link ContactValidator} object.<p/>
     * 
     * @param contact The Contact object to be written to the database using a {@link ContactRepository} object
     * @return The Contact object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    @SuppressWarnings("null")
	ArrayList<Contact> create() throws ConstraintViolationException, ValidationException, Exception {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
    	log.info("ContactService.create() - Creating ");


        //Perform a rest call to get the state of the contact from the allareacodes.com API
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("group4flight-000297171.rhcloud.com/")
                .setPath("/rest/contacts/")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray contactArray = new JSONArray(responseBody);
        for(int i=0;i<contactArray.length();i++){
        	JSONObject responseJson = contactArray.getJSONObject(i);
        	Contact contact = new Contact();
            // Check to make sure the data fits with the parameters in the Contact model and passes validation.
            contact.setName(responseJson.getString("name"));
            contact.setEmail(responseJson.getString("email"));
            contact.setPhoneNumber(responseJson.getString("phoneNumber"));
			validator.validateContact(contact);
            
            contacts.add(crud.create(contact));
        }
        HttpClientUtils.closeQuietly(response);

        // Write the contact to the database.
        return contacts;
    }



}
