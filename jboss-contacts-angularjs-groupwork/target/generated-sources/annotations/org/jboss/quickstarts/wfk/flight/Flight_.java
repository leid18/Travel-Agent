package org.jboss.quickstarts.wfk.flight;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Flight.class)
public abstract class Flight_ {

	public static volatile SingularAttribute<Flight, String> flightDestination;
	public static volatile SingularAttribute<Flight, String> departurePoint;
	public static volatile SingularAttribute<Flight, Long> id;
	public static volatile SingularAttribute<Flight, String> state;
	public static volatile SingularAttribute<Flight, String> flightNumber;

}

