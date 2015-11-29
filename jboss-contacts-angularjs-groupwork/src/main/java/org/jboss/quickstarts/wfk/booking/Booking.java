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
package org.jboss.quickstarts.wfk.booking;

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
import org.jboss.quickstarts.wfk.flightBooking.FlightBooking;
import org.jboss.quickstarts.wfk.hotel.Hotel;
import org.jboss.quickstarts.wfk.hotelBooking.HotelBooking;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxiBooking.TaxiBooking;

/**
 * <p>This is a the Domain object. The Booking class represents how Booking resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a Booking are retrieved from the database (with @NamedQueries), and acceptable values
 * for Booking fields (with @NotNull, @Pattern etc...)<p/>
 * 
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Booking.FIND_ALL, query = "SELECT c FROM Booking c ORDER BY c.id ASC"),
    @NamedQuery(name = Booking.FIND_BY_CUSTOMER, query = "SELECT c FROM Booking c WHERE c.customer.id = :customerID"),
    @NamedQuery(name = Booking.FIND_BY_TAXI, query = "SELECT c FROM Booking c WHERE c.taxi.id = :taxiID"),
    @NamedQuery(name = Booking.FIND_BY_FLIGHT, query = "SELECT c FROM Booking c WHERE c.flight.id = :flightID"),
    @NamedQuery(name = Booking.FIND_BY_HOTEL, query = "SELECT c FROM Booking c WHERE c.hotel.id = :hotelID"),
})
@XmlRootElement
@Table(name = "Booking", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Booking implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomer";
    public static final String FIND_BY_TAXI = "Booking.findByTaxi";
    public static final String FIND_BY_FLIGHT = "Booking.findByFlight";
    public static final String FIND_BY_HOTEL = "Booking.findByHotel";
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
    @JoinColumn(name = "customerID")
    private Contact customer;

    @ManyToOne
    @JoinColumn(name = "flightID")
    private Flight flight;
    
    @ManyToOne
    @JoinColumn(name = "taxiID")
    private Taxi taxi;
    
    @ManyToOne
    @JoinColumn(name = "hotelID")
    private Hotel hotel;
    
    @NotNull
    @Future(message = "Booking day can not be in the past. Please choose one from the future.")
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    
    @Column(name = "state")
    private String state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getCustomer() {
        return customer;
    }

    public void setCustomer(Contact customer) {
        this.customer = customer;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
    
    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    
    public Long getCustomerID() {
        return customer.getId();
    }

    public void setCustomerID(Long customerID) {
        this.customer.setId(customerID);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
    
    public Long getTaxiID() {
        return taxi.getId();
    }

    public void setTaxiID(Long taxiID) {
        this.taxi.setId(taxiID);
    }
    
    public void main(){}
}
