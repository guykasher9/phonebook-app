# Phone Book - Web Server API

## Main Objective

Create a simple phone book (contacts) web server API.

---

## Project Description

This project is a backend API service for managing a phone book. It allows you to store, retrieve, search, add, edit, and delete contacts. The API is designed to be clear and simple, focusing on backend implementation using a backend-oriented programming language (Java, Spring Boot). The project uses Docker for easy deployment and includes features for scalability, error handling, logging, and testing.

---

## Features

### Contact Model

Each contact includes:
- **First name**
- **Last name**
- **Phone number**
- **Address**

### API Endpoints

- **Get contacts**: `GET /api/contacts` — Retrieve contacts with pagination (maximum 10 per page)
- **Search contact**: `GET /api/contacts/search` — Find contacts by first and last name, with pagination (returns a message if more than 10 results are found)
- **Add contact**: `POST /api/contacts` — Add a new contact
- **Edit contact**: `POST /api/contacts/edit` — Update an existing contact. If multiple contacts have the same first and last name, you must specify the `id` to edit the correct one.
- **Delete contact**: `DELETE /api/contacts/delete` — Remove a contact. If multiple contacts have the same first and last name, you must specify the `id` to delete the correct one.

---

## Getting Started

### Prerequisites

- [Java 21+](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/) or [Gradle](https://gradle.org/) (depending on your build tool)
- [Docker](https://www.docker.com/)

---

## Build and Run Instructions (Build with Docker only in the next section)

### 1. Clone the Repository

```sh
git clone <your-repo-url>
cd phonebook
```

### 2. Build the Project

If you use **Maven**:
```sh
./mvnw clean package
```

If you use **Gradle**:
```sh
./gradlew build
```

### 3. Run with Docker Compose

```sh
docker compose up --build
```

This will start the phonebook API server, Redis, Prometheus, and Grafana.

- The API will be available at: [http://localhost:8080](http://localhost:8080)
- Redis: [localhost:6379](http://localhost:6379)
- Prometheus: [http://localhost:9090](http://localhost:9090)
- Grafana: [http://localhost:3000](http://localhost:3000)

---

## Build with Docker Only

If you do not have Java or Maven installed, you can build the project using Docker:

```sh
docker build -f Dockerfile.builder -o . .
```

This will produce the built JAR file in your current directory, inside an `/output` folder.

---

## Testing

To run the tests:

If you use **Maven**:
```sh
./mvnw test
```
---

## Notes

- The project uses Docker Compose for orchestration.
- All configuration is in `src/main/resources/application.properties`.
- For development, you can run the app locally or inside Docker.

## Example Grafana Dashboard

Below is an example of a Grafana dashboard visualizing metrics from the phonebook API:

![image](https://github.com/user-attachments/assets/2d8925d9-bb2a-461e-abfa-bd8e25204cc5)


This dashboard shows request counts by type, requests per URI, and JVM memory usage, helping you monitor your API in real time.

