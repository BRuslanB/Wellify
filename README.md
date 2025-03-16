# Wellify Application
A comprehensive health tracking application that can become a handy tool for disease prevention and monitoring of users' health. This is a Java-based application supported by a REST API.

Note: This application is created for educational and training purposes only. It is not intended for commercial or production use.
Wellify — from “wellness” and “amplify,” which emphasizes the goal of the project — strengthening and improving health.

## Features
Several functional blocks (services) can be distinguished:

* Administration — managing application settings and viewing user data, providing convenient service management.
* Health monitoring — collecting data from wearable devices, analyzing indicators and displaying metrics.
* Scheduling and reminders — reminders for taking medications, visiting doctors and other events.
* Storing medical documents and data — the ability to download and store reports, prescriptions and medical history.
* Notifications and alerts — warnings and notifications about important health indicators and the user's health.
* Analytics and reports – health data analysis, trends, graphs and insights.

## Technologies Used
The Wellify application was built using a microservices architecture, which uses the following services:
* User registration and authentication.
* User account management (blocking, unblocking, deleting), only for application administrators.
* Changes to user profile data and password.
* ...

The application is built using the following technologies:

* Java 21
* Spring Data JPA
* Hibernate
* PostgreSQL
* MongoDB (not used yet)
* Redis
* Keycloak
* MinIO
* Grafana (not used yet)
* Prometheus (not used yet)

# Prerequisites
Before running the application, you will need to have the following software installed on your machine:

* Java Development Kit (JDK) 21 or higher
* Docker

# Getting Started
To run the application locally, follow these steps:

1. Clone the repository to your local machine:
```bash
git clone https://github.com/BRuslanB/Wellify.git
```
2. Launch:
```bash
docker-compose up
```

## Contributing
If you would like to contribute to developing this application, please submit a pull request or open an issue on the GitHub repository.

## License
This application is licensed under the MIT License. See the LICENSE file for more details.
