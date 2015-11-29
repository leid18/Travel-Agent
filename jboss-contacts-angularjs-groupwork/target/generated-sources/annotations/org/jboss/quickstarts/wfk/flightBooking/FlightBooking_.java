package org.jboss.quickstarts.wfk.flightBooking;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FlightBooking.class)
public abstract class FlightBooking_ {

	public static volatile SingularAttribute<FlightBooking, Flight> flight;
	public static volatile SingularAttribute<FlightBooking, Contact> contact;
	public static volatile SingularAttribute<FlightBooking, Date> bookingDate;
	public static volatile SingularAttribute<FlightBooking, Long> id;
	public static volatile SingularAttribute<FlightBooking, String> state;

}

