Messaging & Payment Microservice
1. Project Overview

This project consists of two microservices: Messaging and Payment, which communicate asynchronously using RabbitMQ.

Messaging Service handles sending messages, such as emails or SMS, to users.

Payment Service handles processing payments.

Payments are received via REST API, stored in PostgreSQL, and published to RabbitMQ.

RabbitMQ delivers messages asynchronously to consumers for processing.

The project is built using Java 17 and Spring Boot 3.5.11.

2. File / Directory Structure
Messaging Service

The messaging service directory contains source code under src/main/java/com/example/messaging/ with the following structure:

controller/ – contains REST controllers.

service/ – contains business logic and RabbitMQ integration.

dto/ – contains Data Transfer Objects.

model/ – contains JPA entities for the database.

repository/ – contains Spring Data JPA repositories.

config/ – contains configurations such as RabbitMQ and security.

Resources such as application.properties are under src/main/resources/. The project also contains a pom.xml for dependencies.

Payment Service

The payment service directory has a similar structure under src/main/java/com/example/payment/ with:

controller/ – REST controllers.

service/ – business logic and RabbitMQ integration.

dto/ – Data Transfer Objects.

model/ – JPA entities.

repository/ – Spring Data JPA repositories.

config/ – configuration files.

Resources such as application.properties are under src/main/resources/, and there is a pom.xml for dependencies.

RabbitMQ Docker Setup

The RabbitMQ setup contains a .env file and docker-compose.yml.

RabbitMQ must be running to allow communication between the microservices. Its management UI can be accessed via a browser at http://localhost:15672
 with login credentials guest and guest.

3. Running the Microservices

Before starting, databases must be created for both messaging and payment services.

Once the databases are ready, the microservices can be started. The Messaging Service runs on port 8081, and the Payment Service runs on port 8082.

4. Example Postman Collection

The following API requests are available for testing:

POST /api/payments – sends payment data with fields such as amount, currency, and email.

GET /api/payments/{id} – retrieves payment information by ID.

A Postman invite link is available to import the collection:
Join Postman Collection

5. Payment Flow

The client sends a payment request to the payment microservice.

The microservice stores the payment in PostgreSQL.

Payment details are sent to a RabbitMQ queue named payment.queue.

A consumer listens on the queue and processes the payment asynchronously.

The messaging service can consume payment events to notify users by email or other channels.

6. Notes / Tips

RabbitMQ must be running before starting the microservices.

Each microservice has its own database; configuration files must point to the correct database.

Global exception handling is implemented to manage invalid file uploads or invalid data rows.

The messaging service expects Excel uploads containing columns: name, email, and phone.