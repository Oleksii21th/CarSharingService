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
| Method | Endpoint                  | Description |
|--------|---------------------------|------------|
| GET    | `/api/cars`               | List available cars |
| POST   | `/api/auth/register`      | Register new user |
| POST   | `/api/auth/login`         | Authenticate user & get JWT |

### Available for registered users
| Method | Endpoint                          | Description |
|--------|-----------------------------------|------------|
| GET    | `/api/rentals/{id}`               | Get rental by ID |
| GET    | `/api/users/me`                   | Get personal info |
| GET    | `/api/payments/success`           | Stripe success redirect |
| GET    | `/api/payments/cancel`            | Stripe cancel redirect |
| GET    | `/api/rentals`                    | Get rentals (with filters) |
| POST   | `/api/rentals`                    | Rent a car |
| POST   | `/api/rentals/{id}/return`        | Return a car |
| PATCH  | `/api/users/me`                   | Update profile info |
| POST   | `/api/payments`                   | Make payment |

### Available for admin users
| Method | Endpoint                                      | Description |
|--------|-----------------------------------------------|------------|
| GET    | `/api/cars/{id}`                              | Get car by ID |
| GET    | `/api/rentals?userId=...&isActive=...`         | Get rentals by user ID & status |
| GET    | `/api/payments?user_id=...`                    | Get payments by user ID |
| POST   | `/api/cars`                                   | Add new car |
| PUT    | `/api/users/{id}/role`                        | Update user role |
| PATCH  | `/api/cars/{id}`                              | Update car info |
| DELETE | `/api/cars/{id}`                              | Delete car |

### Health Check
| Method | Endpoint                    | Description |
|--------|-----------------------------|------------|
| GET    | `/api/memory-health` | Get memory usage and health status |

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
