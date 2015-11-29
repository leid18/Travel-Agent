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
package org.jboss.quickstarts.wfk.hotel;

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
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.ContactRepository;
import org.jboss.quickstarts.wfk.contact.ContactRESTService;
import org.jboss.quickstarts.wfk.contact.ContactService;
import org.jboss.quickstarts.wfk.contact.ContactValidator;
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
 * Contact creation functionality
 * (see {@link ContactRESTService#createContact(Contact) createContact(Contact)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see ContactRESTService
 */
@RunWith(Arquillian.class)
public class HotelRegistrationTest {

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
        //HttpComponents and org.JSON are required by ContactService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Hotel.class, 
                        HotelRESTService.class, 
                        HotelRepository.class, 
                        HotelValidator.class, 
                        HotelService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    HotelRESTService hotelRESTService;
    
    @Inject
    @Named("logger") Logger log;
    
    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Response response = hotelRESTService.createHotel();

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New contact was persisted and returned status " + response.getStatus());
    }
    

/**
    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Contact contact = createContactInstance("Jack", "jack@mailinator.com", "06464646464");
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New contact was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Contact contact = createContactInstance("", "", "");
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Contact contact = createContactInstance("Jane", "jane@mailinator.com", "06464646464");
        contactRESTService.createContact(contact);

        // Register a different user with the same email
        Contact anotherContact = createContactInstance("John", "jane@mailinator.com", "06464646464");
        Response response = contactRESTService.createContact(anotherContact);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate contact register attempt failed with return code " + response.getStatus());
    }

    @Test
    @InSequence(4)
    public void testDeleteContact() throws Exception {
        // Register an initial user
        Contact contact = createContactInstance("Jaa", "jaa@mailinator.com", "06464646464");
        contactRESTService.createContact(contact);

        Response response = contactRESTService.deleteContact(contact.getId());

        assertEquals("Unexpected response status", 400, response.getStatus());
        log.info("Delete contact register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(5)
    public void testRetrieveContact() throws Exception {
        Response response = contactRESTService.retrieveAllContacts();

        assertEquals("Unexpected response status", 200, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        log.info("Retrieve contact register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(6)
    public void testNameValidation() throws Exception {
        Contact contact = createContactInstance("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "hfghi@qq.com", "01111111111");
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(7)
    public void testEmailValidation() throws Exception {
        Contact contact = createContactInstance("ffffff", "hfghi444qq.com", "01111111111");
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testPhoneNumberValidation() throws Exception {
        Contact contact = createContactInstance("ffffff", "hfghigg@qq.com", "xxxxxx");
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }
    
/*    @SuppressWarnings("unchecked")
    @Test
    @InSequence(9)
    public void testChangeID() throws Exception {
        Contact contact = createContactInstance("ffffff", "hfghikk@qq.com", "09999999999");
        contactRESTService.createContact(contact);
        Contact contact2 = createContactInstance("fjjffff", "yyghi@qq.com", "07777777777");
        contactRESTService.createContact(contact2);

        Response response = contactRESTService.updateContact(4, contact2);
        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }*/
 /**   
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testIDValidation() throws Exception {
        Contact contact = createContactInstanceWithID((long)9,"ffffff", "hfghig33g@qq.com", "09888888888");
        Response response = contactRESTService.createContact(contact);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }
    
    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.contact.Contact Contact} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param name The name of the Contact being created
     * @param email     The email address of the Contact being created
     * @param phone     The phone number of the Contact being created
     * @return The Contact object create
     */
    
    private Contact createContactInstance(String name, String email, String phone) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setPhoneNumber(phone);
        return contact;
    }
    
    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.contact.Contact Contact} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param id The id of the Contact being created
     * @param name The name of the Contact being created
     * @param email     The email address of the Contact being created
     * @param phone     The phone number of the Contact being created
     * @return The Contact object create
     */
    private Contact createContactInstanceWithID(Long id, String name, String email, String phone) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        contact.setEmail(email);
        contact.setPhoneNumber(phone);
        return contact;
    }
}
