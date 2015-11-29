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
package org.jboss.quickstarts.wfk.flightBooking;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;

/**
 * <p>This is a the Domain object. The Contact class represents how contact resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a contacts are retrieved from the database (with @NamedQueries), and acceptable values
 * for Contact fields (with @NotNull, @Pattern etc...)<p/>
 * 
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = FlightBooking.FIND_ALL, query = "SELECT c FROM FlightBooking c ORDER BY c.id ASC"),
    @NamedQuery(name = FlightBooking.FIND_BY_DATE, query = "SELECT c FROM FlightBooking c WHERE c.bookingDate = :bookingDate"),
    @NamedQuery(name = FlightBooking.FIND_BY_FLIGHT, query = "SELECT c FROM FlightBooking c WHERE c.flight.id = :flight_id"),
    @NamedQuery(name = FlightBooking.FIND_BY_CONTACT, query = "SELECT c FROM FlightBooking c WHERE c.contact.id = :contact_id")
})
@XmlRootElement
@Table(name = "FlightBooking", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class FlightBooking implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "FlightBooking.findAll";
    public static final String FIND_BY_DATE = "FlightBooking.findByDate";
    public static final String FIND_BY_FLIGHT = "FlightBooking.findByFlight";
    public static final String FIND_BY_CONTACT = "FlightBooking.findByContact";
    

    /*
     * The  error messages match the ones in the UI so that the user isn't confused by two similar error messages for
     * the same error after hitting submit. This is if the form submits while having validation errors. The only
     * difference is that there are no periods(.) at the end of these message sentences, this gives us a way to verify
     * where the message came from.
     * 
     * Each variable name exactly matches the ones used on the HTML form name attribute so that when an error for that
     * variable occurs it can be sent to the correct input field on the form.  
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name="contact_id")
    private Contact contact;
    
    @ManyToOne
    @NotNull
    @JoinColumn(name="flight_id")
    private Flight flight;
    
    @NotNull
    @Future(message = "Booking date can not be in the past. Please choose one from the future")
    @Column(name = "bookingDate")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;
    
    /**
    @NotNull
    @Size(min = 5, max = 5)
    @Pattern(regexp = "^[A-Za-z0-9]{5}$", message = "Please enter a flight number with only numbers and alphabets")
    @Column(name = "flight_number")
    private Contact contact;

    @NotNull
    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[A-Z]{3}$", message = "Please use a upper case departure point without numbers or specials")
    @Column(name = "departure_point")
    private String departurePoint;
    
    @NotNull
    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[A-Z]{3}$", message = "Please use a upper case destination without numbers or specials")
    @Column(name = "flight_destination")
    private String flightDestination;
    */

    
    @Column(name = "state")
    private String state;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
    public Contact getContact(){
    	return contact;
    }
    
    public void setContact(Contact contact){
    	this.contact = contact;
    	
    }
    
    public Flight getFlight(){
    	return flight;
    }
    
    public void setFlight(Flight flight){   	
    	this.flight = flight;
    	
    }

    public void setBookingDate(Date bookingDate){
    	this.bookingDate = bookingDate;
    }
    
    public Date getBookingDate(){
    	return bookingDate;
    }
    /**public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getFlightDestination() {
        return flightDestination;
    }

    public void setFlightDestination(String flightDestination) {
        this.flightDestination = flightDestination;
    }
    */
    
    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

}
