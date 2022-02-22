# Cloudbeds Microservice API
This a POC Microservice implemented based on the given requirements.

## How to run

### Integration tests
Simply run a **mvn clean install** in the parent project folder.

This will build and run a container with the app together with its dependencies(mysql).
Once everything is done, the integration tests will be run against those containers.
When finished, containers are torn down.

### Application
Simply run a **docker-compose up --build** in the parent project folder.
This will build up the application and start a container the app and another container with MySQL.
**The tests are being skipped at this stage**.

###Swagger
The swagger endpoints are available at:
http://localhost:8080/swagger-ui/index.html

## Technologies and features

### Java:  11 
Java Runtime version.
When the app is dockerized, the application uses the **openjdk11 Alpine** distribution, more specifically:
**adoptopenjdk/openjdk11:jre-11.0.3_7-alpine**

### Maven: 3.6.0
Build tool.
Also dockerized as a build step in a multi-stage dockerfile.

The docker imaged used for this is: **maven:3.6.0-jdk-11-slim**

### Docker Engine: 20.10.8
The docker engine tested against and required to run the **docker-compose** file.

### MySQL: 8.0
The SQL persistence provider. It is pulled and ran as a docker image from the docker hub.

### H2 In memory DB
The in memory DB used for running the unit/service/controller tests.

### REST APIs
Communication mechanism for the main APIs(create user/address and search users by country).

###Swagger
Swagger UI is used in order to facilitate working with the REST endpoints.

### GRPC
Communication mechanism for the secondary APIs(retrieve users by Id).

### FailSafe plugin
This is used for integration tests.
During the mvn **verify step**, the integration tests are run against the **actual containers** which are started as a pre-requisite.

This setup is as close as possible to the one in production.

### Flyway
**Database versioning system**.
This is used to ensure consistency in the database structure(DDL) on a server.

###JSON Path
This is used to assert json values on response paths during integration tests.

###MapStruct
This is used as a mapper for the DTO between entities and vice-versa.

###Docker compose
Used to bring up all required services(mysql, app).

###Password encryption
The passwords are stored encrypted in the DB.

###Validations
DTOs are validated against messages from a resource bundle.

###Paginated Search
The search endpoint is paginated.

