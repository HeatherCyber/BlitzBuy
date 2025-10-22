# BlitzBuy - Technical Information Report

## 1. Repository Structure and Environment

### 1.1 Project Tree (Up to 3 Levels)

```
BlitzBuy/
├── frontend/
│   └── blitzbuy-frontend/
│       ├── build/                        # Production build output
│       ├── node_modules/                 # Frontend dependencies (npm)
│       ├── public/                       # Static assets
│       ├── src/                          # React TypeScript source code
│       │   ├── components/               # Reusable React components
│       │   ├── pages/                    # Page components (Login, Goods, Order)
│       │   ├── services/                 # API service layer
│       │   ├── store/                    # State management
│       │   ├── types/                    # TypeScript type definitions
│       │   ├── utils/                    # Utility functions
│       │   ├── App.tsx                   # Main application component
│       │   └── index.tsx                 # Application entry point
│       ├── package.json                  # Frontend dependencies & scripts
│       └── tsconfig.json                 # TypeScript configuration
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/blitzbuy/
│   │   │       ├── config/               # Configuration classes (Redis, RabbitMQ, Web)
│   │   │       ├── controller/           # REST Controllers & API endpoints
│   │   │       ├── exception/            # Global exception handlers
│   │   │       ├── mapper/               # MyBatis data access layer
│   │   │       ├── pojo/                 # Plain Old Java Objects (entities)
│   │   │       ├── rabbitmq/             # RabbitMQ message producers/consumers
│   │   │       ├── service/              # Business logic layer
│   │   │       │   └── impl/             # Service implementations
│   │   │       ├── util/                 # Utility classes
│   │   │       ├── validator/            # Custom validators
│   │   │       ├── vo/                   # Value Objects (DTOs)
│   │   │       └── BlitzbuyApplication.java  # Spring Boot main class
│   │   │
│   │   └── resources/
│   │       ├── mapper/                   # MyBatis XML mappers
│   │       ├── scripts/                  # Lua scripts (Redis)
│   │       ├── sql/                      # Database schema definitions
│   │       ├── static/                   # Legacy frontend static resources
│   │       ├── templates/                # Thymeleaf templates (legacy)
│   │       └── application.yml           # Spring Boot configuration
│   │
│   └── test/
│       └── java/                         # Unit tests
│
├── target/                               # Maven build output
├── API.md                                # API documentation
├── LICENSE                               # MIT License
├── pom.xml                               # Maven configuration & dependencies
└── README.md                             # Project overview
```

---

## 2. Backend Dependency Tree (Maven)

### 2.1 Core Framework
```
Spring Boot Parent: 3.5.3
└── Java Version: 17
```

### 2.2 Web & Template Engine
```
spring-boot-starter-web
├── Spring MVC (REST Controllers)
├── Embedded Tomcat Server
└── Jackson (JSON processing)

spring-boot-starter-thymeleaf
└── Thymeleaf Template Engine (Legacy frontend)
```

### 2.3 Database & ORM
```
mysql-connector-j (runtime)
└── MySQL 8.0 JDBC Driver

mybatis-plus-spring-boot3-starter: 3.5.12
├── MyBatis Core
├── MyBatis-Plus Extensions
└── Code Generator Support
```

### 2.4 Caching & Session Management
```
spring-boot-starter-data-redis
├── Lettuce Client (default)
├── RedisTemplate
└── Distributed Session Support

commons-pool2: 2.12.1
└── Redis Connection Pooling
```

### 2.5 Message Queue
```
spring-boot-starter-amqp
├── RabbitMQ Client
├── Spring AMQP
└── Message Queue Integration
```

### 2.6 Validation & Security
```
spring-boot-starter-validation
├── Hibernate Validator
├── Jakarta Validation API
└── Custom Validators (@IsMobile)

commons-codec: 1.17.0
└── MD5 Password Hashing

commons-lang3: 3.18.0
└── String & Object Utilities
```

### 2.7 Captcha Generation
```
kaptcha: 2.3.2
└── Google Kaptcha Library (Captcha images)
```

### 2.8 Utilities
```
hutool-all: 5.8.16
├── JSON Utilities
├── Crypto Utilities
└── Common Tool Methods

jackson-databind: 2.19.1
└── JSON Serialization/Deserialization

lombok (optional)
└── Code Generation (@Data, @Slf4j, etc.)
```

### 2.9 Testing
```
spring-boot-starter-test (test scope)
├── JUnit 5
├── Mockito
├── Spring Test
└── AssertJ
```

### 2.10 Build Plugin
```
spring-boot-maven-plugin
└── Package executable JAR
```

---

## 3. Frontend Dependencies (Top-Level)

### 3.1 Core Framework
```
react: 19.1.1
└── UI library

react-dom: 19.1.1
└── React DOM rendering

react-scripts: 5.0.1
└── Create React App build tools

typescript: 4.9.5
└── TypeScript support
```

### 3.2 UI Component Library
```
antd: 5.27.4
├── Ant Design component library
└── Modern enterprise UI components

@ant-design/icons: 6.0.2
└── Ant Design icon library
```

### 3.3 State Management
```
@reduxjs/toolkit: 2.9.0
└── Redux state management

react-redux: 9.2.0
└── React-Redux bindings

zustand: 5.0.8
└── Lightweight state management alternative
```

### 3.4 Routing
```
react-router-dom: 7.9.3
└── Client-side routing
```

### 3.5 HTTP Client
```
axios: 1.12.2
└── HTTP request library
```

### 3.6 Utilities
```
crypto-js: 4.2.0
└── MD5 password hashing (client-side)

dayjs: 1.11.18
└── Date/time manipulation

web-vitals: 2.1.4
└── Performance metrics
```

### 3.7 TypeScript Type Definitions
```
@types/react: 19.1.15
@types/react-dom: 19.1.9
@types/react-redux: 7.1.34
@types/react-router-dom: 5.3.3
@types/crypto-js: 4.2.2
@types/jest: 27.5.2
@types/node: 16.18.126
```

### 3.8 Testing
```
@testing-library/react: 16.3.0
@testing-library/jest-dom: 6.8.0
@testing-library/user-event: 13.5.0
@testing-library/dom: 10.4.1
```

---

## 4. Version Snapshot

### 4.1 Runtime Environment
| Component | Version | Release Date | Usage |
|-----------|---------|--------------|-------|
| **JDK** | 17.0.8 LTS | July 2023 | ✅ Production runtime |
| **Node.js** | 22.14.0 | December 2024 | 🔧 Development only (React toolchain) |
| **npm** | 10.9.2 | December 2024 | 🔧 Development only (package manager) |

**Note**: Node.js is **only used during React frontend development** for build tools (webpack, babel) and dev server. Production deployment uses static files (HTML/CSS/JS) that don't require Node.js runtime.

### 4.2 Backend Framework & Tools
| Component | Version | Purpose |
|-----------|---------|---------|
| **Spring Boot** | 3.5.3 | Core framework |
| **MyBatis-Plus** | 3.5.12 | ORM & data access |
| **MySQL Connector** | Latest (runtime) | Database driver |
| **Redis** | 6.0+ (deployed) | Cache & session store |
| **RabbitMQ** | 3.8+ (deployed) | Message queue |

### 4.3 Frontend Framework & Tools
| Component | Version | Purpose |
|-----------|---------|---------|
| **React** | 19.1.1 | UI framework |
| **TypeScript** | 4.9.5 | Type system |
| **Ant Design** | 5.27.4 | UI component library |
| **React Router** | 7.9.3 | Client-side routing |
| **Axios** | 1.12.2 | HTTP client |

### 4.4 Infrastructure Services (Deployed on CentOS 7)
| Service | Version | Deployment |
|---------|---------|------------|
| **MySQL** | 8.0 | Local/VM (port 3306) |
| **Redis** | 6.0+ | VM (192.168.33.10:6379) |
| **RabbitMQ** | 3.8+ | VM (192.168.33.10:5672) |

### 4.5 Development Tools
| Tool | Purpose |
|------|---------|
| **Maven** | Backend build & dependency management |
| **npm** | Frontend package management |
| **Apache JMeter** | Load testing (2000 concurrent users) |
| **Docker** | Multi-instance deployment |

---

## 5. Configuration Summary

### 5.1 Backend Configuration
- **Application Port**: 9090
- **Database Connection Pool**: HikariCP
  - Min Idle: 5
  - Max Pool Size: 10
  - Connection Timeout: 30s
- **Redis Pool**:
  - Max Active: 8
  - Max Idle: 200
  - Min Idle: 5
- **RabbitMQ Listener**:
  - Concurrency: 5-10
  - Prefetch: 1
  - Max Retry: 3

### 5.2 Frontend Configuration

**Development Environment** (Node.js required):
- **Development Port**: 3000 (React dev server)
- **API Base URL**: http://localhost:9090/api/v1
- **CORS**: Enabled (credentials supported)
- **Hot Reload**: Enabled (webpack-dev-server)

**Production Build**:
- **Build Tool**: webpack (via Create React App)
- **Build Target**: ES5 (browser compatibility)
- **Output**: Static files (HTML/CSS/JS)
- **Runtime**: Browser only (no Node.js server)

---

## 6. Project Statistics

### 6.1 Backend Code Structure
- **Controllers**: 9 classes (REST API + Thymeleaf)
- **Services**: 6 interfaces + 6 implementations
- **Mappers**: 5 MyBatis interfaces
- **POJOs/Entities**: 6 domain models
- **Configuration Classes**: 8 classes
- **Utilities**: 6 utility classes

### 6.2 Frontend Code Structure
- **Pages**: 4 main page components
- **Services**: API layer with modular endpoints
- **Type Definitions**: Complete TypeScript interfaces
- **Utilities**: Session, MD5, Image helpers

### 6.3 Database Schema

**Core Tables**: 5 tables with optimized indexes and constraints

| Table | Purpose | Key Features |
|-------|---------|-------------|
| user | User authentication | Phone as primary key, MD5 password |
| goods | Product catalog | Basic product information |
| flash_sale_goods | Flash sale events | Price, stock, time constraints |
| order | Order records | Status tracking, timestamps |
| flash_sale_order | Flash sale tracking | Unique constraint prevents duplicate purchases |

**Detailed Schema**: See [Section 8 - Database Schema Design](#8-database-schema-design)

---

## 7. Build & Deployment

### 7.1 Backend Build
```bash
# Maven build
mvn clean package

# Run Spring Boot application
java -jar target/Blitzbuy-0.0.1-SNAPSHOT.jar
```

### 7.2 Frontend Build

**Development Mode** (requires Node.js):
```bash
cd frontend/blitzbuy-frontend

# Install dependencies (one-time setup)
npm install

# Run development server on port 3000
npm start
```

**Production Build** (generates static files):
```bash
cd frontend/blitzbuy-frontend

# Build optimized static files
npm run build

# Output: frontend/blitzbuy-frontend/build/
# Contains: index.html, CSS, JS bundles
# Can be served by Nginx, Apache, or Spring Boot
# ✅ No Node.js needed at runtime
```

### 7.3 Production Deployment Architecture

```
┌─────────────────────────────────────────────────────┐
│            Production Environment                   │
├─────────────────────────────────────────────────────┤
│                                                     │
│  Frontend (Static Files)                            │
│  ├── Nginx / Apache / CDN                           │
│  ├── HTML, CSS, JS bundles                          │
│  └── ❌ No Node.js runtime required                 │
│                                                     │
│  Backend (Spring Boot)                              │
│  ├── Java 17 JVM                                    │
│  ├── Port 9090                                      │
│  └── ✅ Only Java runtime required                  │
│                                                     │
│  Infrastructure                                     │
│  ├── MySQL 8.0 (localhost:3306)                     │
│  ├── Redis 6.0+ (192.168.33.10:6379)               │
│  └── RabbitMQ 3.8+ (192.168.33.10:5672)            │
│                                                     │
└─────────────────────────────────────────────────────┘
```

**Deployment Components**:
- **Frontend**: Static files served by web server (no Node.js)
- **Backend**: Spring Boot JAR (Java 17 runtime)
- **Cache**: Redis instance on VM
- **MQ**: RabbitMQ instance on VM
- **Database**: MySQL with connection pooling

---

## 8. Database Schema Design

### 8.1 Entity-Relationship Overview

```
┌─────────┐       ┌──────────────────┐       ┌─────────┐
│  User   │       │ Flash Sale Order │       │  Order  │
│         │◄──────│                  │──────►│         │
│  (PK)   │  1:N  │    (UK: user,    │  N:1  │  (PK)   │
│   id    │       │     goods)       │       │   id    │
└─────────┘       └──────────────────┘       └─────────┘
     │                     │                       │
     │                     │                       │
     │            ┌────────▼────────┐             │
     │            │     Goods       │◄────────────┘
     │            │                 │
     │            │      (PK)       │
     └───────────►│       id        │
            N:N   └─────────────────┘
                           │
                           │ 1:1
                  ┌────────▼─────────┐
                  │ Flash Sale Goods │
                  │                  │
                  │   (FK: goods)    │
                  └──────────────────┘
```

### 8.2 Table Structures

#### 8.2.1 User Table
```sql
CREATE TABLE `user` (
    id BIGINT(20) NOT NULL COMMENT 'User ID (mobile phone number)',
    nickname VARCHAR(255) NOT NULL DEFAULT '',
    password VARCHAR(32) NOT NULL DEFAULT '' 
        COMMENT 'MD5(MD5(plaintext+fixed_salt)+user_salt)',
    salt VARCHAR(10) NOT NULL DEFAULT '',
    head VARCHAR(128) NOT NULL DEFAULT '' COMMENT 'Avatar URL',
    register_date DATETIME DEFAULT NULL,
    last_login_date DATETIME DEFAULT NULL,
    login_count INT(11) DEFAULT '0',
    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Key Design Decisions**:
- 📱 **Phone as Primary Key**: Mobile number serves as unique identifier
- 🔐 **Two-Layer MD5**: Client hashes once, server hashes again with user salt
- 📊 **Login Tracking**: Tracks last login time and count for analytics

---

#### 8.2.2 Goods Table
```sql
CREATE TABLE `goods` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    image_url VARCHAR(255) COMMENT 'Product image (OSS/CDN)',
    price DECIMAL(10,2) NOT NULL COMMENT 'Original price',
    description TEXT,
    stock INT NOT NULL COMMENT 'Total inventory',
    status TINYINT DEFAULT 1 COMMENT '1:active, 0:inactive',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Key Design Decisions**:
- 💰 **DECIMAL for Prices**: Precise monetary calculations
- 🖼️ **External Image Storage**: URL references to CDN/OSS
- 📅 **Auto Timestamps**: Automatic tracking of creation and updates

---

#### 8.2.3 Flash Sale Goods Table
```sql
CREATE TABLE `flash_sale_goods` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    goods_id BIGINT NOT NULL COMMENT 'Reference to goods table',
    flash_sale_price DECIMAL(10,2) NOT NULL,
    flash_sale_stock INT NOT NULL COMMENT 'Flash sale inventory',
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    is_active TINYINT DEFAULT 1 COMMENT 'Flash sale active status',
    PRIMARY KEY (id),
    INDEX idx_goods_id (goods_id),
    INDEX idx_time_range (start_time, end_time),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Key Design Decisions**:
- ⏰ **Time-Based Activation**: Start/end times control flash sale windows
- 📦 **Separate Stock**: Independent inventory for flash sales
- 🔍 **Compound Index**: Optimized for time range queries

---

#### 8.2.4 Order Table
```sql
CREATE TABLE `order` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    goods_name VARCHAR(200) NOT NULL,
    goods_count INT NOT NULL DEFAULT 1,
    goods_price DECIMAL(10,2) NOT NULL,
    order_channel TINYINT NOT NULL DEFAULT 1 
        COMMENT '1:flash_sale, 2:normal',
    order_status TINYINT NOT NULL DEFAULT 0 
        COMMENT '0:unpaid, 1:paid, 2:shipped, 3:received, 4:refunded, 5:completed',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pay_time TIMESTAMP NULL,
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_goods_id (goods_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Key Design Decisions**:
- 📋 **Order Status FSM**: State machine with 6 states
- 📊 **Denormalized Data**: Stores goods name/price for historical record
- 🔍 **Query Optimization**: Indexes on user_id, goods_id, create_time

**Order Status Flow**:
```
0 (Unpaid) → 1 (Paid) → 2 (Shipped) → 3 (Received) → 5 (Completed)
                ↓
            4 (Refunded)
```

---

#### 8.2.5 Flash Sale Order Table
```sql
CREATE TABLE `flash_sale_order` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_goods (user_id, goods_id) 
        COMMENT 'Prevent duplicate flash sale purchases',
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_goods_id (goods_id),
    FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Key Design Decisions**:
- 🚫 **Unique Constraint**: `(user_id, goods_id)` prevents duplicate purchases
- 🔗 **Foreign Key**: Cascade delete ensures referential integrity
- ⚡ **Lightweight Design**: Minimal fields for fast inserts during flash sales

**Critical Constraint**:
```sql
UNIQUE KEY uk_user_goods (user_id, goods_id)
```
This is the **database-level safeguard** against users purchasing the same flash sale item multiple times, complementing Redis-based checks.

---

### 8.3 Index Strategy

| Table | Index Name | Columns | Purpose | Type |
|-------|-----------|---------|---------|------|
| order | idx_user_id | user_id | User order lookup | B-Tree |
| order | idx_goods_id | goods_id | Product order stats | B-Tree |
| order | idx_create_time | create_time | Time-based queries | B-Tree |
| flash_sale_order | uk_user_goods | user_id, goods_id | **Prevent duplicates** | Unique |
| flash_sale_order | idx_order_id | order_id | Join optimization | B-Tree |
| flash_sale_goods | idx_time_range | start_time, end_time | Active flash sales | Composite |

**Query Optimization Examples**:

```sql
-- Optimized by idx_user_id
SELECT * FROM `order` WHERE user_id = 13500001001;

-- Optimized by uk_user_goods (prevents duplicates)
INSERT INTO flash_sale_order (user_id, order_id, goods_id) 
VALUES (13500001001, 12345, 1);

-- Optimized by idx_time_range
SELECT * FROM flash_sale_goods 
WHERE NOW() BETWEEN start_time AND end_time 
AND is_active = 1;
```

---

## 9. Redis Key Design

### 9.1 Key Naming Convention

**Pattern**: `{domain}:{entity}:{identifier}[:{sub-identifier}]`

Example: `flashSaleOrder:13500001001:1` (user 13500001001's order for goods 1)

### 9.2 Redis Key Catalog

| Key Pattern | Data Type | TTL | Purpose | Example Value |
|-------------|-----------|-----|---------|---------------|
| **Session Management** |
| `userTicket:{uuid}` | String (JSON) | Default | User session storage | `{"id":13500001001,"nickname":"Alice",...}` |
| **Flash Sale Stock** |
| `flashSaleStock:{goodsId}` | String (Integer) | Persistent | Pre-stock reduction | `100` → `99` → `98` ... |
| **Distributed Lock** |
| `lock:{goodsId}` | String (UUID) | 5 seconds | Flash sale distributed lock | `uuid-abc-123-def` |
| **Order Tracking** |
| `flashSaleOrder:{userId}:{goodsId}` | String (JSON) | Persistent | Prevent duplicate purchases | `{"id":1,"userId":135...}` |
| `flashSaleFail:{userId}:{goodsId}` | String | 60 seconds | Flash sale failure marker | `"0"` |
| **Security** |
| `flashSalePath:{userId}:{goodsId}` | String | 60 seconds | Secure flash sale path | `md5-hashed-path-string` |
| `captcha:{userId}:{goodsId}` | String | 100 seconds | Captcha verification | `"A3X9K"` |
| **Rate Limiting** |
| `{requestURI}:{userId}` | String (Integer) | Dynamic | Access frequency control | `1` → `2` → `3` ... |
| **Page Caching (Legacy)** |
| `goodsList` | String (HTML) | 60 seconds | Goods list page cache | `<html>...</html>` |
| `goodsDetail:{goodsId}` | String (HTML) | 60 seconds | Goods detail page cache | `<html>...</html>` |

---

### 9.3 Detailed Key Specifications

#### 9.3.1 User Session
```
Key:   userTicket:{uuid}
Type:  String (Serialized User object)
TTL:   Default (Redis default expiration)
Value: {"id":13500001001,"nickname":"Alice","mobile":"13500001001",...}
```

**Code Reference**:
```java
// UserServiceImpl.java - Set
redisTemplate.opsForValue().set("userTicket:" + ticket, user);

// Get
User user = (User) redisTemplate.opsForValue().get("userTicket:" + userTicket);
```

**Purpose**: Distributed session management across multiple application instances.

---

#### 9.3.2 Flash Sale Stock (Pre-Reduction)
```
Key:   flashSaleStock:{goodsId}
Type:  String (Integer)
TTL:   Persistent (initialized on app startup)
Value: Integer stock count (e.g., 100, 99, 98...)
```

**Code Reference**:
```java
// FlashSaleController.java - Initialize
redisTemplate.opsForValue().set("flashSaleStock:" + goodsVo.getId(), 
    goodsVo.getFlashSaleStock());

// Atomic decrement
Long stock = redisTemplate.opsForValue().decrement("flashSaleStock:" + goodsId);

// Rollback on failure
redisTemplate.opsForValue().increment("flashSaleStock:" + goodsId);
```

**Critical Operations**:
1. **Initialization**: On app startup, load stock from database
2. **Decrement**: Atomic pre-reduction before database transaction
3. **Rollback**: Increment if database update fails
4. **Local Memory Check**: JVM map marks stock as depleted when Redis hits 0

**Flow**:
```
Database: 100 items
    ↓
Redis Init: flashSaleStock:1 = 100
    ↓
Request 1: DECR → 99 (check passed, create order)
Request 2: DECR → 98 (check passed, create order)
...
Request 100: DECR → 0 (check passed, create order)
Request 101: DECR → -1 (❌ rejected, stock exhausted)
```

---

#### 9.3.3 Distributed Lock
```
Key:   lock:{goodsId}
Type:  String (UUID identifier)
TTL:   5 seconds (with retry mechanism)
Value: UUID (e.g., "uuid-abc-123-def-456")
```

**Code Reference**:
```java
// FlashSaleController.java - Acquire lock
String lockKey = "lock:" + goodsId;
String uuid = UUIDUtil.uuid();
Boolean lock = redisTemplate.opsForValue()
    .setIfAbsent(lockKey, uuid, 5, TimeUnit.SECONDS);

// Release lock (Lua script for atomicity)
redisTemplate.execute(redisScript, Arrays.asList(lockKey), uuid);
```

**Lua Script** (`releaseLock.lua`):
```lua
-- Atomic check-and-delete to prevent releasing others' locks
if redis.call('get', KEYS[1]) == ARGV[1] then 
    return redis.call('del', KEYS[1]) 
else 
    return 0 
end
```

**Lock Lifecycle**:
```
1. Request arrives → Generate UUID
2. SET lock:{goodsId} {uuid} NX EX 5
   ├─ Success → Proceed with stock decrement
   └─ Failure → Wait 10ms, retry once
3. Business logic completes
4. Lua script: DELETE lock:{goodsId} IF value == {uuid}
```

**Why Distributed Lock?**
- **Problem**: `DECR` alone ensures atomicity for single operation
- **Solution**: Lock protects multi-step operations (check stock, validate user, create order)
- **Scale**: Necessary for distributed deployment with multiple app instances

---

#### 9.3.4 Flash Sale Order Cache
```
Key:   flashSaleOrder:{userId}:{goodsId}
Type:  String (Serialized FlashSaleOrder object)
TTL:   Persistent
Value: {"id":1,"userId":13500001001,"orderId":12345,"goodsId":1}
```

**Code Reference**:
```java
// OrderServiceImpl.java - Set after successful order
redisTemplate.opsForValue()
    .set("flashSaleOrder:" + user.getId() + ":" + goodsId, flashSaleOrder);

// Check before flash sale
FlashSaleOrder order = (FlashSaleOrder) redisTemplate.opsForValue()
    .get("flashSaleOrder:" + user.getId() + ":" + goodsId);
if (order != null) {
    return RespBean.error(RespBeanEnum.REPEAT_PURCHASE);
}
```

**Purpose**: **Fast duplicate purchase check** without querying database.

**Defense Layers**:
```
Layer 1: Redis Cache Check (flashSaleOrder:userId:goodsId exists?)
    ↓ Not found
Layer 2: Database Unique Constraint (UNIQUE KEY uk_user_goods)
    ↓ Insert attempt
Layer 3: Transaction Rollback (if constraint violated)
```

---

#### 9.3.5 Security Path (Anti-Crawler)
```
Key:   flashSalePath:{userId}:{goodsId}
Type:  String (MD5 hash)
TTL:   60 seconds
Value: MD5-hashed random UUID (e.g., "5d41402abc4b2a76b9719d911017c592")
```

**Code Reference**:
```java
// OrderServiceImpl.java - Generate
String path = MD5Util.md5(UUIDUtil.uuid());
redisTemplate.opsForValue()
    .set("flashSalePath:" + userId + ":" + goodsId, path, 60, TimeUnit.SECONDS);

// Validate
String redisPath = (String) redisTemplate.opsForValue()
    .get("flashSalePath:" + userId + ":" + goodsId);
return path.equals(redisPath);
```

**Security Flow**:
```
1. User requests flash sale → Show captcha
2. User inputs captcha → Generate random path, store in Redis (60s TTL)
3. Frontend receives path → POST /flashSale/doFlashSale/{path}
4. Backend validates path from Redis
5. Path expires after 60s or one-time use
```

**Why?**
- **Prevent Direct Access**: Can't access `/doFlashSale` without valid path
- **Time-Limited**: Path expires in 60 seconds
- **One-Time Use**: Optional deletion after use
- **Anti-Bot**: Combined with captcha verification

---

#### 9.3.6 Captcha Verification
```
Key:   captcha:{userId}:{goodsId}
Type:  String (5-character code)
TTL:   100 seconds
Value: "A3X9K" (generated by Kaptcha library)
```

**Code Reference**:
```java
// FlashSaleController.java - Generate
String capText = captchaProducer.createText();
redisTemplate.opsForValue()
    .set("captcha:" + userId + ":" + goodsId, capText, 100, TimeUnit.SECONDS);

// Verify
String redisCaptcha = (String) redisTemplate.opsForValue()
    .get("captcha:" + userId + ":" + goodsId);
return captcha.equals(redisCaptcha);
```

---

#### 9.3.7 Rate Limiting
```
Key:   {requestURI}:{userId}
Type:  String (Integer counter)
TTL:   Configured per endpoint (e.g., 5 seconds)
Value: Request count (e.g., 1, 2, 3...)
```

**Code Reference** (`AccessLimitInterceptor.java`):
```java
@AccessLimit(seconds = 5, maxCount = 5) // Max 5 requests per 5 seconds
public RespBean getFlashSalePath(...) { ... }

// Implementation
String key = requestURI + ":" + userId;
Integer count = (Integer) redisTemplate.opsForValue().get(key);
if (count == null) {
    redisTemplate.opsForValue().set(key, 1, seconds, TimeUnit.SECONDS);
} else if (count >= maxCount) {
    return RespBean.error(RespBeanEnum.REQUEST_TOO_FREQUENT);
} else {
    redisTemplate.opsForValue().increment(key);
}
```

**Example**:
```
Endpoint: /flashSale/getFlashSalePath
User: 13500001001
Limit: 5 requests / 5 seconds

Key: /flashSale/getFlashSalePath:13500001001
Lifecycle:
  t=0s: Request 1 → SET key 1 EX 5 → ✅ Allow
  t=1s: Request 2 → INCR key → 2 → ✅ Allow
  t=2s: Request 3 → INCR key → 3 → ✅ Allow
  t=3s: Request 4 → INCR key → 4 → ✅ Allow
  t=4s: Request 5 → INCR key → 5 → ✅ Allow
  t=4.5s: Request 6 → GET key → 5 ≥ 5 → ❌ Reject (429 Too Many Requests)
  t=5s: Key expires → Reset counter
  t=6s: Request 7 → SET key 1 EX 5 → ✅ Allow
```

---

### 9.4 Redis Data Persistence Strategy

| Key Type | Persistence | Reason |
|----------|-------------|--------|
| Session tokens | ✅ Important | User login state |
| Flash sale stock | ✅ Critical | Must survive restart |
| Flash sale orders | ✅ Critical | Duplicate check |
| Distributed locks | ❌ Ephemeral | Self-expiring (5s TTL) |
| Captcha codes | ❌ Ephemeral | Self-expiring (100s TTL) |
| Flash sale paths | ❌ Ephemeral | Self-expiring (60s TTL) |
| Rate limit counters | ❌ Ephemeral | Self-expiring (5-60s TTL) |
| Page cache | ❌ Ephemeral | Regenerable (60s TTL) |

**Redis Configuration Recommendation**:
```conf
# Persistence for critical data
save 900 1      # Save if 1 key changed in 15 min
save 300 10     # Save if 10 keys changed in 5 min
save 60 10000   # Save if 10k keys changed in 1 min

# AOF for maximum durability
appendonly yes
appendfsync everysec
```

---

## 10. RabbitMQ Topology

### 10.1 Message Queue Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                     Flash Sale Flow                          │
└──────────────────────────────────────────────────────────────┘

Producer (FlashSale Controller)
    │
    │ 1. Pre-check (stock, user, path)
    │ 2. Redis pre-decrement
    │ 3. Send message to queue
    │
    ├──────> MQSender.sendFlashSaleMessage(message)
    │
    ↓
┌───────────────────────────────────────────┐
│      Topic Exchange                       │
│   "flash.sale.exchange"                   │
│                                           │
│   Routing Key: "flash.sale.#"             │
└───────────────┬───────────────────────────┘
                │
                │ Binding
                ↓
┌───────────────────────────────────────────┐
│         Queue                             │
│    "flash.sale.queue"                     │
│                                           │
│  - Durable: true                          │
│  - Auto-delete: false                     │
│  - Concurrency: 5-10 consumers            │
└───────────────┬───────────────────────────┘
                │
                │ Consume
                ↓
Consumer (MQReceiver)
    │
    │ 1. Parse message
    │ 2. Validate user & goods
    │ 3. Create order (DB transaction)
    │ 4. Update Redis cache
    │
    └──────> Order created or failure marked
```

---

### 10.2 RabbitMQ Configuration

**Configuration Class** (`RabbitMQConfig.java`):

```java
@Configuration
public class RabbitMQConfig {
    // Queue name
    public static final String FLASH_SALE_QUEUE = "flash.sale.queue";
    
    // Exchange name
    public static final String FLASH_SALE_EXCHANGE = "flash.sale.exchange";
    
    // Routing key pattern
    public static final String FLASH_SALE_ROUTING_KEY = "flash.sale.#";
    
    @Bean
    public Queue flashSaleQueue() {
        return new Queue(FLASH_SALE_QUEUE);
    }
    
    @Bean
    public TopicExchange flashSaleExchange() {
        return new TopicExchange(FLASH_SALE_EXCHANGE);
    }
    
    @Bean
    public Binding flashSaleBinding() {
        return BindingBuilder.bind(flashSaleQueue())
            .to(flashSaleExchange())
            .with(FLASH_SALE_ROUTING_KEY);
    }
}
```

---

### 10.3 Message Format

**FlashSaleMessage POJO**:
```json
{
  "user": {
    "id": 13500001001,
    "nickname": "Alice",
    "mobile": "13500001001"
  },
  "goodsId": 1
}
```

**Serialization**: JSON (via Hutool JSONUtil)

---

### 10.4 Producer Implementation

**MQSender.java**:
```java
@Service
public class MQSender {
    @Resource
    private RabbitTemplate rabbitTemplate;
    
    public void sendFlashSaleMessage(String message) {
        log.info("Sending message: {}", message);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FLASH_SALE_EXCHANGE,
            RabbitMQConfig.FLASH_SALE_ROUTING_KEY,
            message
        );
    }
}
```

**When to Send**:
```java
// FlashSaleController.java (commented out - direct sync now)
// After Redis pre-check passes
FlashSaleMessage message = new FlashSaleMessage(user, goodsId);
mqSender.sendFlashSaleMessage(JSONUtil.toJsonStr(message));
```

**Note**: Current implementation uses **synchronous processing** instead of message queue for simplicity. The MQ infrastructure is in place for future scaling.

---

### 10.5 Consumer Implementation

**MQReceiver.java**:
```java
@Service
public class MQReceiver {
    @Resource
    private GoodsService goodsService;
    
    @Resource
    private OrderService orderService;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @RabbitListener(queues = RabbitMQConfig.FLASH_SALE_QUEUE)
    public void receiveFlashSaleMessage(String message) {
        try {
            // 1. Parse message
            FlashSaleMessage msg = JSONUtil.toBean(message, FlashSaleMessage.class);
            
            // 2. Get goods info
            GoodsVo goods = goodsService.getGoodsVoByGoodsId(msg.getGoodsId());
            
            // 3. Create order (with DB transaction)
            Order order = orderService.creatFlashSaleOrder(msg.getUser(), goods);
            
            if (order == null) {
                // Mark as failed
                redisTemplate.opsForValue().set(
                    "flashSaleFail:" + msg.getUser().getId() + ":" + msg.getGoodsId(),
                    "0",
                    60,
                    TimeUnit.SECONDS
                );
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
}
```

---

### 10.6 Consumer Configuration

**application.yml**:
```yaml
spring:
  rabbitmq:
    host: 192.168.33.10
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    
    listener:
      simple:
        concurrency: 5          # Minimum concurrent consumers
        max-concurrency: 10     # Maximum concurrent consumers
        prefetch: 1             # Prefetch 1 message at a time
        auto-startup: true
        default-requeue-rejected: true
    
    template:
      retry:
        enabled: true
        initial-interval: 1000ms
        max-attempts: 3
        max-interval: 10000ms
        multiplier: 1.0
```

---

### 10.7 Message Flow Lifecycle

```
Step 1: Flash Sale Request
  └─> Controller validates request
      └─> Redis pre-check (stock, duplicate)
          └─> Pass? → Continue

Step 2: Asynchronous Processing (Optional)
  └─> Send message to RabbitMQ
      └─> Controller returns immediately
          └─> Frontend polls for result

Step 3: Background Consumer
  └─> Receive message from queue
      └─> Fetch goods from database
          └─> Create order (with transaction)
              ├─> Success: Save to Redis cache
              └─> Failure: Mark failure in Redis

Step 4: Result Polling
  └─> Frontend polls /getFlashSaleResult
      └─> Check Redis for order or failure marker
          ├─> Order exists → Success (redirect to order page)
          ├─> Failure marker → Show error
          └─> Neither → Still processing
```

---

### 10.8 Why RabbitMQ?

| Benefit | Description |
|---------|-------------|
| **Peak Traffic Handling** | Queue absorbs burst traffic, processes at steady rate |
| **Failure Isolation** | Order creation failures don't crash frontend requests |
| **Retry Mechanism** | Auto-retry failed messages (max 3 attempts) |
| **Horizontal Scaling** | Add more consumers to process orders faster |
| **Async Response** | Controller returns immediately, user polls for result |

**Performance Comparison**:

| Scenario | Synchronous | With RabbitMQ |
|----------|-------------|---------------|
| Request handling | Blocking (2-5s) | Non-blocking (<100ms) |
| Peak traffic | Limited by DB connections | Limited by queue capacity |
| Failure impact | User sees error immediately | Graceful degradation |
| Scalability | Vertical (more DB connections) | Horizontal (more consumers) |

---

### 10.9 Message Durability & Reliability

**Queue Durability**:
```java
@Bean
public Queue flashSaleQueue() {
    return new Queue(FLASH_SALE_QUEUE, true, false, false);
    //                                  ↑     ↑      ↑
    //                              durable  exclusive  auto-delete
}
```

**Message Persistence**:
```java
// Messages are persisted to disk by default with Spring AMQP
rabbitTemplate.convertAndSend(exchange, routingKey, message);
```

**Acknowledgement Mode**:
```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: auto  # Auto-ack after successful processing
```

**Failure Handling**:
1. **Parse Error**: Log error, discard message (invalid format)
2. **Goods Not Found**: Mark failure in Redis, don't retry
3. **Order Creation Failed**: Auto-retry (max 3 attempts)
4. **Max Retries Exceeded**: Move to dead-letter queue (optional)

---

### 10.10 Monitoring & Metrics

**Key Metrics to Track**:
- Queue depth (number of pending messages)
- Consumer throughput (orders/second)
- Message processing time (average, p95, p99)
- Failure rate (% of failed order creations)
- Retry count (how often retries occur)

**RabbitMQ Management UI**:
- URL: `http://192.168.33.10:15672`
- Credentials: `guest` / `guest`
- Monitor queue depth, consumer count, message rates

---

**Report Generated**: October 13, 2025  
**Project**: BlitzBuy - High-Concurrency Flash Sale System  
**Environment**: Development & Testing


