# IST Signatures

## Features
* User registration and login with JWT authentication
* Password encryption using BCrypt
* Role-based authorization with Spring Security
* Customized access denied handling
* Email verification
* Manage company (CRUD)
* User Phone update
* Logout mechanism
* Refresh token

## Technologies
* Spring Boot 3.0
* Spring Security
* JSON Web Tokens (JWT)
* BCrypt
* Maven
* PostgreSQL
* Docker
* JUnit
 
## Getting Started
To get started with this project, you will need to have the following installed on your local machine:

* JDK 17+
* Maven 3+


To build and run the project, follow these steps:

* Clone the repository: `git clone https://github.com/RedJanvier/ist-signature-be.git`
* Navigate to the project directory: 
```bash 
cd ist-signature-be
```
* Add database "ist_signatures" to postgres or simply run the command
```bash
docker compose up 
``` 
* Build the project: `mvn clean install`
* Run the project: `mvn spring-boot:run`

-> The application will be available at http://localhost:8080/api/v1/swagger.
