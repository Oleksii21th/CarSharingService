# Car Sharing API

## Table of Contents
- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Endpoints](#endpoints)
- [Postman Examples](#postman-examples)
- [API Usage Steps](#api-usage-steps)
- [Contact](#contact)

---

## Project Overview

**Car Sharing API** is a Spring Boot REST application with JWT-based authentication.  
It allows **customers** to register, rent and return cars, and make payments, while **admins** can manage cars, users, and track payments.

The system includes:
- Automatic daily checks for overdue rentals
- Telegram notifications for new rentals, payments, and overdue rentals
- Stripe integration for payment processing

---

## Technologies Used

- **Java 17**
- **Spring Boot** (Web, Security, Data JPA)
- **Spring Security**
- **MySQL**
- **Liquibase**
- **MapStruct**
- **Docker**
- **JWT (JSON Web Token)**
- **Stripe API**
- **Telegram API**

---

## Project Structure

```
src/
├── main/
│ ├── java/carsharing/carsharingservice/
│ │ ├── config/ → Application configuration
│ │ ├── controller/ → REST API endpoints
│ │ ├── dto/ → Data Transfer Objects
│ │ ├── exception/ → Custom exceptions
│ │ ├── healthcheck/ → Health check implementation
│ │ ├── mapper/ → MapStruct mappers
│ │ ├── model/ → Entity classes
│ │ ├── repository/ → JPA repositories
│ │ ├── scheduler/ → Scheduled tasks (overdue rentals)
│ │ ├── security/ → JWT authentication and authorization
│ │ ├── service/ → Business logic
│ │ └── validation/ → Custom validators
│ └── CarSharingServiceApplication.java
├── resources/
│ ├── db/changelog/ → Liquibase changelogs
│ └── application.properties
```

---

## Endpoints

### Available for all users
| Method | Endpoint       | Description |
|--------|----------------|------------|
| GET    | `/cars`        | List available cars |
| POST   | `/register`    | Register new user |
| POST   | `/login`       | Authenticate user & get JWT |

### Available for registered users
| Method | Endpoint             | Description |
|--------|--------------------|------------|
| GET    | `/rentals/{rentalId}` | Get rental by ID |
| GET    | `/users/me`         | Get personal info |
| GET    | `/payments/success` | Stripe success redirect |
| GET    | `/payments/cancel`  | Stripe cancel redirect |
| POST   | `/rentals`          | Rent a car |
| POST   | `/rentals/return`   | Return a car |
| POST   | `/users/me`         | Update profile info |
| POST   | `/payments`         | Make payment |

### Available for admin users
| Method | Endpoint                                  | Description |
|--------|-------------------------------------------|------------|
| GET    | `/cars/{id}`                              | Get car by ID |
| GET    | `/rentals?user_id=...&is_active=...`      | Get rentals by user ID & status |
| GET    | `/rentals/status?isActive=...`           | Get rentals by status |
| GET    | `/payments/{userId}`                      | Get payments by user ID |
| POST   | `/cars`                                   | Add new car |
| PUT    | `/users/{userId}/role`                    | Update user role |
| PATCH  | `/cars/{id}`                              | Update car info |
| DELETE | `/cars/{id}`                              | Delete car |

> **Note:** All `POST`, `PUT`, and `PATCH` endpoints require JSON body.

---

## Postman Examples

- **Register:** Provide email, password, name, surname.
- **Add a New Car (Admin Only):** Provide model, brand, type, inventory, daily fee.
- **Get Rentals by Status (Admin Only):** Parameter `isActive=true` for active, `false` for returned.
- **Pay for Rental:** Provide rental ID and payment type; response includes Stripe payment link.
- **Get Payments by User ID (Admin Only):** Provide `userId` parameter.

---

## API Usage Steps

1. Deploy the API on your server using Docker.
2. Add cars using the admin user (see example).
    - Default admin created via Liquibase:
        - Login: `admin@user.com`
        - Password: `admin`
3. Add the following environment variables in `.env`:
    - `TELEGRAM_BOT_TOKEN`
    - `TELEGRAM_CHAT_ID`
    - `STRIPE_SECRET_KEY`
4. Done! New users can now register and rent cars.

## Video Representation
https://youtu.be/p1TUqOvCCKE

## Contact

- **Developer**: Oleksii Babych
- **Email**:   obabych1@stu.vistula.edu.pl
- **GitHub**: https://github.com/Oleksii21th/CarSharingService
