Messaging & Payment Microservice
1. Project Overview

This microservice handles payments and messaging via RabbitMQ.

Payments are received via REST API, stored in PostgreSQL, and published to RabbitMQ.

RabbitMQ delivers messages asynchronously to consumers.

Java version 17 .
2. File / Directory Structure
messaging/ 
│
├─ src/main/java/com/example/messaging/
│   ├─ controller/ 
│   ├─ service/
│   ├─ dto/  
│   ├─ model/ 
│   ├─ repository/ 
│   └─ config/
│
├─ src/main/resources/
│   ├─ application.properties
│
└─ pom.xml

payment/ 
│
├─ src/main/java/com/example/payment/
│   ├─ controller/ 
│   ├─ service/
│   ├─ dto/  
│   ├─ model/ 
│   ├─ repository/ 
│   └─ config/
│
├─ src/main/resources/
│   ├─ application.properties
│
└─ pom.xml


rabbitmq/ 
│
├─ .env
│
└─ docker-compose.yml



RUn RabbitMQ docker docker-compose up -d

Access UI: http://localhost:15672

Login: guest / guest

4. Running the Microservice

create your database for both message and payment 

Run the microservices

mvn clean install
mvn spring-boot:run

MESSAGE BASE URL: http://localhost:8081
PAYMENT BASE URL: http://localhost:8082



POSTMAN DIRECTORY

5. Example Postman Collection


Request Type	URL	Body Example
POST	/api/payments	{ "amount": 100, "currency": "USD", "email": "user@test.com" }
GET	/api/payments/{id}	N/A

Postman Invite Link:
https://app.getpostman.com/join-team?invite_code=7aade01f3ed484fb8db9a20aae558dfe3da1a4bdabff4a100017f035ffeb6199&target_code=11ef339cb756adfa6c4b87777b3e9570

6. Payment Flow

Client sends payment request via POST /api/v1/payment/create.

Microservice stores the payment in PostgreSQL.

Message is sent to RabbitMQ queue (payment.queue).

Consumer listens on the queue and processes the payment asynchronously.