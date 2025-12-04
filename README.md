# RideMate

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [System Integrations](#system-integrations)
- [Getting Started](#getting-started)
- [License](#license)

---

## Overview

RideMate is a comprehensive ride-sharing application that connects drivers offering rides with passengers looking for transportation. The platform features role-based access control, real-time ride management, booking system, and comprehensive audit logging through a microservices architecture.

---

## 🛠️ Tech Stack

### **Backend (Main Application)**

| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.4.0 | Core framework |
| **Java** | 17 | Programming language |
| **Spring Data JPA** | 3.4.0 | Data persistence |
| **PostgreSQL** | Latest | Primary database |
| **Spring Security** | 3.4.0 | Authentication & authorization |
| **JWT (JJWT)** | 0.11.5 | Token-based authentication |
| **Spring Cloud OpenFeign** | 2024.0.2 | Microservice communication |
| **Spring Cache** | 3.4.0 | Application-level caching |
| **Spring AOP** | 3.4.0 | Aspect-oriented programming |
| **Lombok** | Latest | Boilerplate reduction |
| **Bean Validation** | 3.4.0 | Input validation |
| **iText PDF** | 8.0.4 | PDF generation |
| **Google Maps Services** | 2.2.0 | Route calculations |
| **Spring Actuator** | 3.4.0 | Application monitoring |
| **JaCoCo** | 0.8.12 | Code coverage |
| **Maven** | Latest | Build tool |

### **Backend (Audit Service)**

| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.4.0 | Core framework |
| **Java** | 17 | Programming language |
| **Spring Data JPA** | 3.4.0 | Data persistence |
| **PostgreSQL** | Latest | Audit database |
| **H2 Database** | Latest | Testing database |
| **Spring Cache** | 3.4.0 | Audit log caching |
| **Spring Validation** | 3.4.0 | Input validation |
| **JaCoCo** | 0.8.12 | Code coverage |

### **Frontend**

| Technology | Version | Purpose |
|------------|---------|---------|
| **React** | 19.2.0 | UI framework |
| **TypeScript** | 5.9.3 | Type safety |
| **Vite** | 7.2.2 | Build tool & dev server |
| **React Router DOM** | 7.9.6 | Client-side routing |
| **Axios** | 1.13.2 | HTTP client |
| **Tailwind CSS** | 4.1.17 | Styling framework |
| **ESLint** | 9.39.1 | Code linting |

---

### **Main Application (`main-app`)**
- Handles core business logic
- User authentication and authorization
- Ride management (CRUD operations)
- Booking system
- Role-based access control
- PDF receipt generation

### **Audit Service (`audit-service`)**
- Independent microservice for audit logging
- Tracks all significant user actions
- Activity history management
- Scheduled cleanup of old logs
- RESTful API for audit queries

### **Frontend**
- Responsive design
- Role-specific UI/UX
- Real-time data updates

---

## 🔗 System Integrations

### **1. Audit Service Integration**
- **Type**: Microservice communication via OpenFeign
- **Purpose**: Track user actions and maintain audit logs
- **Communication**: RESTful API calls
- **Data Flow**: Main app → Audit service (async logging)
- **Features**:
  - User registration/login tracking
  - Ride creation/update/deletion logging
  - Booking action tracking
  - Activity history retrieval

### **2. OpenStreetMap (OSM) Autocomplete**
- **Type**: Frontend integration
- **Purpose**: Location autocomplete for origin/destination
- **Usage**: Enhanced UX for address input
- **Integration**: Nominatim API

### **3. PostgreSQL Database**
- **Type**: Relational database
- **Purpose**: Primary data storage
- **Databases**:
  - `ridemate` - Main application data
  - `ridemate_audit` - Audit service logs

### **4. Spring Actuator**
- **Type**: Monitoring and management
- **Purpose**: Application health checks and metrics
- **Endpoints**: Health, metrics, info

---

## 🚀 Getting Started

### **Prerequisites**
- Java 17 or higher
- Node.js 18+ and npm
- PostgreSQL 14+
- Maven 3.8+

### **Backend Setup**

**1. Clone the repository:**
```bash
git clone https://github.com/georgikazaliev/RideMate.git
cd RideMate
```

**2. Set up PostgreSQL databases:**
```sql
CREATE DATABASE ridemate;
CREATE DATABASE ridemate_audit;
```

**3. Configure application properties:**

Update `main-app/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ridemate
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Update `audit-service/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ridemate_audit
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**4. Start Audit Service:**
```bash
cd audit-service
mvn clean install
mvn spring-boot:run
```

**5. Start Main Application:**
```bash
cd main-app
mvn clean install
mvn spring-boot:run
```

### **Frontend Setup**

**1. Navigate to frontend directory:**
```bash
cd frontend
```

**2. Install dependencies:**
```bash
npm install
```

**3. Start development server:**
```bash
npm run dev
```

**4. Access the application:**
```
http://localhost:5173
```

---

## 🔒 Security Features

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control (RBAC)
- CORS configuration
- Input validation and sanitization
- SQL injection prevention (JPA/Hibernate)
- XSS protection
- Secure HTTP headers

---

## 📄 License

This project is developed as part of Spring Advanced coursework.

---
