# BlitzBuy - High-Performance Flash Sale System

A robust, scalable flash sale system built with Spring Boot, featuring advanced security measures, distributed architecture, and high-concurrency optimization.

## 🚀 Features

### Core Functionality
- **Flash Sale System**: High-performance flash sale with inventory management
- **User Authentication**: Secure login with phone number validation (US/CN formats)
- **Order Management**: Complete order processing and tracking
- **Real-time Updates**: Live countdown and status updates

### Security & Performance
- **Distributed Locking**: Redis-based distributed locks for data consistency
- **Rate Limiting**: Configurable access control and anti-brute force protection
- **Captcha Protection**: Kaptcha integration to prevent bot attacks
- **Path Security**: Unique path-based endpoint obfuscation
- **Session Management**: Redis-based distributed session storage

### Architecture
- **Microservices Ready**: Designed for distributed deployment
- **High Concurrency**: Optimized for high-traffic scenarios
- **Fault Tolerant**: Comprehensive error handling and recovery
- **Scalable**: Horizontal scaling support with Redis clustering

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: MySQL with MyBatis-Plus
- **Cache**: Redis for session storage and distributed locking
- **Message Queue**: RabbitMQ for asynchronous processing
- **Frontend**: Thymeleaf, Bootstrap, jQuery
- **Security**: Kaptcha, MD5 encryption, rate limiting
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+
- Maven 3.6+

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/CyberHeather/BlitzBuy.git
cd BlitzBuy
```

### 2. Database Setup
```sql
-- Create database
CREATE DATABASE blitzbuy;

-- Import schema
mysql -u root -p blitzbuy < src/main/resources/sql/create_tables.sql
```

### 3. Configuration
Update `src/main/resources/application.yml`:


### 4. Build and Run
```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

### 5. Access the Application
- **Application**: http://localhost:8080
- **Login**: ID : Use phone number (US: +1-555-123-4567, CN: 13812345678)
- **Login**: Password： 123456 (default)

## 🏗️ Architecture Overview

### System Components
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Data Layer    │
│   (Thymeleaf)   │◄──►│   (Spring Boot) │◄──►│   (MySQL/Redis) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Message Queue │
                       │   (RabbitMQ)    │
                       └─────────────────┘
```

### Key Features
- **Distributed Locking**: Redis-based locks ensure data consistency
- **Asynchronous Processing**: RabbitMQ handles flash sale requests
- **Rate Limiting**: Configurable access control per user/IP
- **Captcha Protection**: Prevents automated attacks
- **Session Management**: Distributed session storage



## 📊 Performance Features

### High Concurrency Optimization
- **Redis Pre-stock Reduction**: Atomic inventory management
- **Local JVM Memory Cache**: Fast stock availability checks
- **Asynchronous Order Processing**: Non-blocking order creation
- **Connection Pooling**: Optimized database connections

### Security Measures
- **Multi-layer Rate Limiting**: User, IP, and global limits
- **Captcha Verification**: Bot protection
- **Path Obfuscation**: Hidden endpoint security
- **Session Validation**: Secure user authentication

## 🚀 Deployment

### Single Instance
```bash
mvn spring-boot:run
```

### Multi-Instance (Docker)
```bash
# Build Docker image
docker build -t blitzbuy .

# Run multiple instances
docker run -d -p 8080:8080 blitzbuy
docker run -d -p 8081:8080 blitzbuy
```

### Production Considerations
- **Redis Cluster**: For high availability
- **Load Balancer**: Nginx or HAProxy
- **Database**: MySQL master-slave setup
- **Monitoring**: Application metrics and health checks

## 📈 Testing & Performance

### Load Testing with JMeter
The project has been thoroughly tested using **JMeter** with the following configuration:
```bash
# JMeter Test Configuration
Thread Count: 1000 concurrent users
Loop Count: 10 iterations per thread
Total Requests: 10,000 requests
Simulated Users: 2,000 unique users
User Data: CSV file with credentials
```

### Test Results
- **High Concurrency**: Successfully handles 1000+ concurrent users
- **Performance**: Optimized for flash sale scenarios
- **Stability**: Consistent response times under load
- **Scalability**: Ready for distributed deployment

### Current Testing Approach
- **Functional Testing**: Manual verification of all features
- **Integration Testing**: End-to-end workflow validation
- **Security Testing**: Captcha, rate limiting, and authentication verification
- **Performance Testing**: High concurrency scenarios with JMeter

### Future Testing Plans
- **Unit Tests**: Automated unit test coverage
- **Integration Tests**: Automated API testing
- **Performance Tests**: Continuous load testing
- **Security Tests**: Automated security scanning
- **Monitoring**: Application health checks and metrics

*Note: Automated testing framework is planned for future development*

## 🔒 Security

### Authentication
- **Phone Number Validation**: US and Chinese formats
- **Session Management**: Secure session handling
- **Password Encryption**: MD5 with salt

### Protection Mechanisms
- **Rate Limiting**: Configurable access control
- **Captcha**: Bot attack prevention
- **Input Validation**: XSS and injection protection
- **CSRF Protection**: Cross-site request forgery prevention

## 📝 API Documentation

### LoginController (`/login`)
- `GET /login/toLogin` - Display login page
- `POST /login/doLogin` - User authentication (phone number + password)

### GoodsController (`/goods`)
- `GET /goods/toList` - Display product list page (with Redis caching)
- `GET /goods/toDetail/{goodsId}` - Display product detail page (with Redis caching)

### FlashSaleController (`/flashSale`)
- `GET /flashSale/getCaptcha` - Generate captcha image for security
- `GET /flashSale/getFlashSalePath` - Get secure flash sale path (requires captcha)
- `POST /flashSale/doFlashSale/{path}` - Execute flash sale with secure path
- `GET /flashSale/getFlashSaleResult` - Get flash sale result status

### UserController (`/user`)
- `GET /user/info` - Get current user information
- `POST /user/updatePassword` - Update user password

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Heather** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- Redis team for robust caching solutions
- RabbitMQ for reliable message queuing
- Bootstrap for responsive UI components


**BlitzBuy** - Built for high-performance flash sales with enterprise-grade security and scalability.