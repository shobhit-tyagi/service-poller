# Service Poller
Spring boot app which polls configured service and keeps a track of their health. 
Written in a layered architecture.
Uses flyway for database migrations.
The service comes with an extremely basic UI to present the status of each service. UI can be accessed at http://localhost:8080

### Getting started

```
mvn clean install
docker-compose up --build --force-recreate
```
This will start the application on port 8080 and a postgres docker container to save the service data.

### Available API

API endpoint | Description | Handled Response codes
-|-|-|
GET http://localhost:8080/v1/services | Get all configured services | 200
GET http://localhost:8080/v1/services/{serviceId} | Get a service by id | 200, 404, 400
POST http://localhost:8080/v1/services <br>```{"name": "service-1", "url": "http://test-service/ping"}```| Create a new service | 200, 400
PATCH http://localhost:8080/v1/services/{serviceId} <br>```{"name": "service-1", "url": "http://test-service/ping"}``` | Patch the service by Id | 200, 400, 404
DELETE http://localhost:8080/v1/services/{serviceId} | Delete the service by Id | 200, 404

### Testing
Service code has been tested with Junit 5. And API tests have been written in groovy using spock.
