# 💳 Digital Wallet System (Microservices)

A microservices-based backend system that enables users to create wallets,
transfer money securely, and maintain transaction consistency using idempotency
and locking mechanisms.

---

## 🔗 Live APIs

🔹 User Service (Swagger UI)  
https://user-service-g5hz.onrender.com/swagger-ui/index.html  

🔹 Wallet Service (Swagger UI)  
https://wallet-service-qyy9.onrender.com/swagger-ui/index.html  

---

## ⚙️ Tech Stack

- Java 17  
- Spring Boot  
- Spring Security (JWT)  
- PostgreSQL  
- Docker  
- Render (Cloud Deployment)  
- Swagger (API Documentation)  

---

## 🧠 Architecture

User Service → Handles user registration, login, JWT authentication  
Wallet Service → Handles wallet operations, transactions, and business logic  

---

## 🔥 Key Features

- User Authentication using JWT  
- Wallet creation and balance management  
- Secure money transfer between users  
- Idempotency support (prevents duplicate transactions)  
- Transaction status tracking (SUCCESS / FAILED)  
- Optimistic locking for concurrency control  
- Global exception handling  
- RESTful APIs documented using Swagger  
- Deployed on cloud (Render)  

---

## 🔄 API Testing Flow

1. Register a new user  
2. Login to get JWT token  
3. Click "Authorize" in Swagger and paste token  
4. Create wallet  
5. Add balance  
6. Transfer money  
7. Check transaction history  

---

## ⚡ Important Concepts Implemented

### 🔁 Idempotency
Ensures that duplicate requests (same idempotency key) do not result in multiple transactions.
The system stores request keys and returns the same response if retried.

### 🔒 Concurrency Handling
Implemented using optimistic locking (version field) to prevent race conditions
during simultaneous transactions.

### 💸 Transaction Management
Each transfer creates:
- DEBIT entry for sender  
- CREDIT entry for receiver  

Also tracks transaction status:
- SUCCESS  
- FAILED  

---

## 📂 Project Structure

wallet-system/
│
├── user-service/
│   ├── controller
│   ├── service
│   ├── repository
│   ├── security (JWT)
│   └── entity
│
├── wallet-service/
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── exception
│   └── idempotency

---

## 🚀 Deployment

Both services are deployed on Render.

User Service → Handles authentication  
Wallet Service → Handles transactions  

---

## 📌 Sample APIs

POST /api/users/register  
POST /api/users/login  

GET  /wallet/my-wallet  
PATCH /wallet/add-balance  
POST /wallet/transfer  
GET  /wallet/history  

---

## 🧪 Example Transaction

```json
{
  "fromUserId": 2,
  "toUserId": 1,
  "amount": 500,
  "status": "SUCCESS"
}