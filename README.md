# upgrade

API docs via Swagger
http://localhost:8080/swagger-ui.html

Highlights:
1.  The main Spring Boot Application class is CampsiteApplication.java.
2.  The main and only RestController class is CampsiteController.java.
3.  There are two tables.
    a.  Reservations table 
    b.  Schedules table (One date per row.   Many-to-one relation to Reservations table via BookingId)
4.  Pessimistic locking on the persistence layer is used to maintain data integrity during concurrent requests.
    The com.upgrade.campsite.services.CampsiteServiceTest.testAsynchronous test is used to test if
    pessimistic locking is working.
5.  Sample REST calls in upgrade.postman_collection.json can be imported to PostMan
6.  The response objects will have a non-empty errors json array if there are any errors.

Sample REST calls Quick Start:
1.  Making a reservation

URL: 
http://localhost:8080/reserve
POST Body: 
{
    "firstName": "Michael",
    "lastName": "Jordan",
    "email": "michael@jordan.com",
    "checkInDate": "2020-07-01",
    "checkOutDate": "2020-07-03"
}

2.  Cancelling a reservation
URL: http://localhost:8080/cancel
POST Body: 
{
   "bookingId": "DOOVO"
}

3.  Modifying a reservation
POST URL: http://localhost:8080/modify
Body:
{
	"firstName": "Michael",
	"lastName": "Jordan",
	"email": "michael@jordan.com",
	"checkInDate": "2020-07-06",
	"checkOutDate": "2020-07-09",
	"bookingId": "BSFJH"
}

4.  Requesting for available dates
POST URL: http://localhost:8080/available
Body:
{
   "startDate": "2020-07-01",
   "endDate": "2020-09-31"
}

5.  Get info about given bookingId
GET URL: http://localhost:8080/reservation?bookingId=BSFJH

6.  Get all reservations
GET URL: http://localhost:8080/reservations

7.  Get all schedules
GET URL: http://localhost:8080/schedules

      
      

 


