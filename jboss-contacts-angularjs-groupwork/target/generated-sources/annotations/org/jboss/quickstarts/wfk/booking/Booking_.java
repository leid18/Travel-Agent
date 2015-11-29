package org.jboss.quickstarts.wfk.booking;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.hotel.Hotel;
import org.jboss.quickstarts.wfk.taxi.Taxi;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Booking.class)
public abstract class Booking_ {

	public static volatile SingularAttribute<Booking, Date> date;
	public static volatile SingularAttribute<Booking, Flight> flight;
	public static volatile SingularAttribute<Booking, Taxi> taxi;
	public static volatile SingularAttribute<Booking, Hotel> hotel;
	public static volatile SingularAttribute<Booking, Long> id;
	public static volatile SingularAttribute<Booking, String> state;
	public static volatile SingularAttribute<Booking, Contact> customer;

}

