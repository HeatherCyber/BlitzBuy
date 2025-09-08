# ⚡ BlitzBuy – High-Concurrency Flash Sale System

This project was developed as the **Capstone Project** for the Master of Software Engineering program, 
under instructor guidance in a professional training environment.

It simulates a high-concurrency e-commerce flash sale system, focusing on backend development 
and applying distributed system concepts such as Redis-based locking, distributed session management, 
and asynchronous order processing with RabbitMQ.

## 🌟 Overview

This project focuses on learning and practicing high-concurrency solutions in backend systems:

- **Preventing overselling** with distributed locks
- **Managing user sessions** across instances
- **Offloading peak traffic** with message queues
- **Deploying components** across multiple VMs to simulate distributed environments

## 🚀 Key Features

- **Flash Sale & Orders**: Inventory management, order creation, and tracking
- **Authentication**: Secure login with phone number validation (US/CN formats)
- **Distributed Locking**: Redis-based locks for inventory consistency
- **Session Management**: Redis-backed distributed sessions
- **Asynchronous Processing**: RabbitMQ queue for order handling
- **Caching Optimization**: Redis pre-stock reduction and local JVM cache
- **Rate Limiting & Captcha**: Protect against brute force and bot attacks

## 🛠️ Technology Stack

- **Backend**: Spring Boot (Java 17), MyBatis-Plus
- **Database**: MySQL 8.0
- **Cache**: Redis 6.0+ (session + locking)
- **Message Queue**: RabbitMQ 3.8+
- **Frontend (Basic)**: Thymeleaf, Bootstrap, jQuery
- **Deployment**: Docker, CentOS7 VMs (Redis & RabbitMQ)

## ⚙️ Deployment

- **Local run** with Spring Boot
- **Multi-instance setup** with Docker
- **Redis and RabbitMQ** deployed on separate CentOS7 VMs to simulate distributed operation

## 🧪 Testing & Performance

- Conducted load testing with **Apache JMeter** to simulate flash sale concurrency scenarios
- Verified stability and concurrency control with 2,000 concurrent users
- Focused on validating Redis-based locks, distributed sessions, and RabbitMQ asynchronous processing under load

**Note**: JMeter scripts and reports are not included in this repository.

## 👤 My Contributions

- **Implemented backend features**: flash sale, authentication, and order management
- **Integrated Redis** for distributed locks, caching, and session management
- **Applied RabbitMQ** for asynchronous order processing to handle peak load
- **Designed and tested** rate limiting and captcha verification to secure high-concurrency workflows

## 🔮 Future Extensions (Not yet implemented)

The project can be extended with additional strategies for handling high concurrency:

- **Blacklist mechanism** (track malicious IPs/users in Redis)
- **Token-based reservation system** (pre-allocate flash sale tokens)
- **Frontend protections** (prevent duplicate submissions)
- **Multi-level caching** (Redis sharding or two-tier cache design)

These were discussed during the training but are not implemented in the current version.

## 📚 Documentation

- **README.md**: Project overview and setup guide
- **API.md**: Complete API documentation with endpoints, parameters, and examples

---

📌 **Note**: This project is for educational purposes, demonstrating practical strategies to address high-concurrency challenges in e-commerce systems.