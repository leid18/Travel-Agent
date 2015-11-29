contacts-angularjs: JAX-RS Services Documentation 
=======================================================
Author: Hugo Firth

This example supports various RESTFul end points which also includes JSONP support for cross domain requests.

By default the base URL for services is `/group4work-000297171.rhcloud.com/rest/`.

BookingService End Points
------------------------
##CREATE
### Create a new Booking

#### /rest/bookings

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{"id":25,"customer":{"id":21,"name":"April","email":"april.liu@locker.com","phoneNumber":"07745261789","state":null},"flight":{"id":13,"flightNumber":"TST01","departurePoint":"DHM","flightDestination":"NCL","state":null},"taxi":{"id":1,"registration":"AUYST66","seats":7,"state":"NY"},"hotel":{"id":18,"hotel":"GTI","hotelNumber":"04637482649","hotelPostCode":"DK12HD","state":"NY"},"date":"2015-08-09","state":"NY","taxiID":1,"customerID":21}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"taxi&date":"That taxi and date are already used, please use a unique taxi&date"}
{"flight&date":"That flight and date are already used, please use a unique flight&date"}
{"hotel&date":"That hotel and date are already used, please use a unique hotel&date"}
```


##READ
### List all bookings
#### /rest/bookings

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{"id":25,"customer":{"id":21,"name":"April","email":"april.liu@locker.com","phoneNumber":"07745261789","state":null},"flight":{"id":13,"flightNumber":"TST01","departurePoint":"DHM","flightDestination":"NCL","state":null},"taxi":{"id":1,"registration":"AUYST66","seats":7,"state":"NY"},"hotel":{"id":18,"hotel":"GTI","hotelNumber":"04637482649","hotelPostCode":"DK12HD","state":"NY"},"date":"2015-08-09","state":"NY","taxiID":1,"customerID":21}]
```

### Find a booking by it's ID.
#### /rest/bookings/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{"id":25,"customer":{"id":21,"name":"April","email":"april.liu@locker.com","phoneNumber":"07745261789","state":null},"flight":{"id":13,"flightNumber":"TST01","departurePoint":"DHM","flightDestination":"NCL","state":null},"taxi":{"id":1,"registration":"AUYST66","seats":7,"state":"NY"},"hotel":{"id":18,"hotel":"GTI","hotelNumber":"04637482649","hotelPostCode":"DK12HD","state":"NY"},"date":"2015-08-09","state":"NY","taxiID":1,"customerID":21}
```


##UPDATE
### Edit one booking
#### /rest/bookings

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{"id":25,"customer":{"id":21,"name":"April","email":"april.liu@locker.com","phoneNumber":"07745261789","state":null},"flight":{"id":13,"flightNumber":"TST01","departurePoint":"DHM","flightDestination":"NCL","state":null},"taxi":{"id":1,"registration":"AUYST66","seats":7,"state":"NY"},"hotel":{"id":18,"hotel":"GTI","hotelNumber":"04637482649","hotelPostCode":"DK12HD","state":"NY"},"date":"2015-08-09","state":"NY","taxiID":1,"customerID":21}
```


##DELETE
### Delete one booking
#### /rest/bookings

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{"id":25,"customer":{"id":21,"name":"April","email":"april.liu@locker.com","phoneNumber":"07745261789","state":null},"flight":{"id":13,"flightNumber":"TST01","departurePoint":"DHM","flightDestination":"NCL","state":null},"taxi":{"id":1,"registration":"AUYST66","seats":7,"state":"NY"},"hotel":{"id":18,"hotel":"GTI","hotelNumber":"04637482649","hotelPostCode":"DK12HD","state":"NY"},"date":"2015-08-09","state":"NY","taxiID":1,"customerID":21}
```


