# offer-service
REST API handled by Spring Boot

The main SpringBoot application is `OffersApplication` with a controller defined in `OfferController`

To run the application, from command line simply use `mvn spring-boot:run`

:information_source: Running `mvn package` will run integration tests, as well as creating an executable JAR.
Once the JAR has been packaged, running `java -jar target/offer-service-0.0.1-SNAPSHOT.jar` from the command line will also run the application.

## Endpoints
The following endpoints are defined for this service:

Method | Endpoint | Description | Example
------ | -------- | ----------- | -------
GET | /offers/_{code}_ | Gets the offer matching the input code | `/offers/ABC123` returns an offer matching code `ABC123`, or throws a 404 NOT FOUND exception if there is nothing matching
PUT | /offers/_{code}_/cancel | Cancels an existing offer matching the input code | `/offers/ABC123/cancel` deactivates the offer matching code `ABC123`
POST | /offers/ | Creates a new Offer from the request body | `/offers/` with valid JSON in the request body will create a new offer

## Design Decisions
Several design decisions were made while creating this application:
1. The `Offer` class is very simple - an offer _code_, a brief description, and an expiration date/time.
   1. I've added an _active_ flag to the Offer class, figuring that for auditing purposes it would probably not be a good idea to actually
   delete offers, rather just to mark them as inactive.
   1. Cancelling an Offer uses `PUT` rather than `DELETE` for this reason - the entity is being updated, rather than completely deleted.
   1. The date handling is fairly basic, mostly for reasons of simplicity.
   1. Handling of JSON requests and responses is also left as the basic out-of-the-box functionality for reasons of simplicity.
1. Spring Boot was used as a quick and easy way to get a running application.
1. Also to make things run quickly and easily, the Offers are held in memory. This would be a very bad idea in a real system, where a database would be the most obvious approach.
   1. It would probably have been worth creating a _data_ layer for the `OfferHandler` to call out to to further separate out the code,
   however for this small example it didn't seem entirely worthwhile (and I ran out of time :wink:).
1. No validation is done for the input parameters so any code/description are accepted - this is probably unwise in a real application.
1. I've used separate exceptions for each possible _error_ scenario - trying to get or update an offer that is not found, one that is
expired, or one that has been cancelled. In a real world system it would probably be worth handling these errors differently, however to
make things quicker here they all return an empty response body.
   1. I would probably keep most of the responses the same.  For security reasons doing a GET on an expired offer, a cancelled offer,
   or a non-existent one, should all give the same response... otherwise you're potentially giving away information about offers that exist.
   1. I nearly wrote a custom error handler using Spring `@ExceptionHandler`, which can be used to create a custom response body with error
   messages taken from the different types of exception thrown, however to keep it simple (and for the security reasons mentioned above)
   I figured it wasn't really necessary for this example.
