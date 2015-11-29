package org.jboss.quickstarts.wfk.hotelBooking;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.hotel.Hotel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(HotelBooking.class)
public abstract class HotelBooking_ {

	public static volatile SingularAttribute<HotelBooking, Date> date;
	public static volatile SingularAttribute<HotelBooking, Hotel> hotel;
	public static volatile SingularAttribute<HotelBooking, Long> id;
	public static volatile SingularAttribute<HotelBooking, String> state;
	public static volatile SingularAttribute<HotelBooking, Contact> customer;

}

