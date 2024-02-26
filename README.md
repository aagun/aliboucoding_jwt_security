# JWT Security

## Overview

This project showcases the implementation of JWT (JSON Web Token) security in a Spring Boot application using Spring Security. The primary features include user registration and login, with user data stored in a PostgreSQL database. The application provides robust authentication mechanisms, allowing users to create new accounts and securely log in.


## Technologies Used

- Spring Boot 3
- Spring Security 6
- PostgreSQL 12
- Docker
- Docker Compose

## Prerequisites

Before running the project, ensure that you have the following software installed on your machine:

- Docker
- Docker Compose
- Postman
- Java 17

## Setup Instructions

1. **Clone the Repository:**

    ```bash
    git clone https://github.com/aagun/aliboucoding_jwt_security.git
    ```

2. **Navigate to the Project Directory:**

    ```bash
    cd aliboucoding_jwt_security
    ```

3. **Run Docker Compose:**

   Execute the following command to start PostgreSQL using Docker Compose:

    ```bash
    docker-compose up -d
    ```

4. **Run the Spring Boot Application:**

   Open the project in your favorite Java IDE or use the following command to run the application:

    ```bash
    ./mvnw spring-boot:run
    ```

   5. **Testing Endpoints:**

      Use Postman to test the following endpoints:

       - **Register User:**
           - **URL:** `http://localhost:8080/api/auth/register`
           - **Method:** `POST`
             - **Request Body:**
                 ```json
                 {
                    "firstName": "your-firstname",
                    "lastName": "your-lastname",
                    "email": "your-email",
                    "password": "your-password"
                 }
                 ```

       - **Login:**
           - **URL:** `http://localhost:8080/api/auth/login`
           - **Method:** `POST`
           - **Request Body:**
               ```json
               {
                   "email": "your-email",
                   "password": "your-password"
               }
               ```

6. **Stopping the Project:**

   When you're done, stop the Spring Boot application and the Docker containers using the following commands:

    - **Stop the Spring Boot application:**
        ```bash
        # Press Ctrl + C in the terminal where the application is running
        ```

    - **Stop the Docker containers:**
        ```bash
        docker-compose down -d --remove-orphans
        ```

## Notes

- Make sure to replace placeholders like `your-email`, `your-password` and etc.. with your desired values.
- Ensure Docker and Docker Compose are running before starting the application.
- You may customize the database configurations in the `application.yml` file if needed.

## Acknowledgments

Special thanks to Alibou Coding's YouTube channel for providing valuable tutorials on JWT security with Spring Boot.

## License

This project is licensed under the [MIT License](LICENSE).
