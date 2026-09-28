# Omni-Stock Requirements Specification

## 1. Problem Statement

Omni-Stock Retail requires a multi-warehouse inventory control
platform to monitor physical stock levels and streamline order
fulfillment across regional distribution hubs.

The system must maintain accurate inventory quantities despite
frequent stock updates and order transactions.

## 2. Business Requirements

### BR-01 Authentication

The system must authenticate users using JWT tokens.

### BR-02 Product Management

Warehouse managers must be able to:

- Add products
- Update products
- View products
- Manage product information

### BR-03 Inventory Management

The system must:

- Track stock quantities
- Track inventory by warehouse
- Update stock quantities
- Detect low-stock conditions
- Support inventory reconciliation

### BR-04 Order Management

The system must:

- Accept customer orders
- Validate requested products
- Check inventory availability
- Reduce available stock after successful order placement
- Maintain order status

### BR-05 Service Communication

The Order Service must communicate with the Inventory Service.

The Inventory Service must communicate with the Product Service
when product information needs to be validated or retrieved.

Required communication flow:

Order Service
    |
    v
Inventory Service
    |
    v
Product Service

### BR-06 Service Discovery

All microservices must register with Eureka Server.

### BR-07 API Gateway

External client requests must enter through the API Gateway.

The gateway must route requests to appropriate microservices.

### BR-08 Load Balancing

The system must support load-balanced communication between
service instances.

### BR-09 Data Consistency

Inventory quantities must remain consistent when multiple
inventory updates and orders occur.

### BR-10 Testing

The system must contain:

- Unit tests
- Integration tests
- API tests

### BR-11 Deployment

All services must be deployable independently.

## 3. Primary Actors

### Warehouse Manager

Responsibilities:

- Authenticate
- Manage product catalog
- Monitor inventory
- Review low-stock conditions
- Update product information

### Customer/User

Responsibilities:

- Authenticate
- Place orders
- View order information

### System

Responsibilities:

- Validate authentication
- Route requests
- Discover services
- Check stock
- Update stock
- Maintain consistency
- Generate low-stock alerts

## 4. Microservices

### Auth Service

Responsibilities:

- User authentication
- Credential validation
- JWT generation
- JWT validation support

### Product Service

Responsibilities:

- Product CRUD
- Product information
- Product availability information

### Inventory Service

Responsibilities:

- Warehouse stock
- Stock quantity updates
- Stock availability
- Low-stock detection
- Stock reconciliation

### Order Service

Responsibilities:

- Order creation
- Order validation
- Inventory verification
- Order status

### API Gateway

Responsibilities:

- Single external entry point
- Request routing
- Authentication filtering
- Service discovery integration
- Load-balanced routing

### Eureka Server

Responsibilities:

- Service registration
- Service discovery
- Service health visibility

## 5. Non-Functional Requirements

### Security

- JWT authentication
- Password hashing
- Protected endpoints
- Stateless authentication

### Reliability

- Consistent inventory updates
- Proper error handling
- Service health checks

### Scalability

- Independently deployable services
- Service discovery
- Load balancing
- Multiple service instances

### Maintainability

- Separation of responsibilities
- Independent microservices
- Clear API contracts
- Unit and integration tests
## 6. System Architecture

The system follows a microservice architecture consisting of six
independently deployable applications:

- Eureka Server
- API Gateway
- Auth Service
- Product Service
- Inventory Service
- Order Service

## 7. Service Ports

| Service | Port |
|---|---:|
| Eureka Server | 8761 |
| API Gateway | 8080 |
| Auth Service | 8081 |
| Product Service | 8082 |
| Inventory Service | 8083 |
| Order Service | 8084 |

## 8. Database Architecture

The system uses PostgreSQL with a database-per-service approach.

Databases:

- omni_auth_db
- omni_product_db
- omni_inventory_db
- omni_order_db

Services must not directly access another service's database.

Inter-service communication must occur through service APIs.

## 9. Service Communication

The primary order workflow is:

Client
→ API Gateway
→ Order Service
→ Inventory Service
→ Product Service

The Order Service is responsible for order creation.

The Inventory Service is responsible for checking and updating stock.

The Product Service is responsible for product information.

## 10. Authentication Flow

1. User authenticates through Auth Service.
2. Auth Service validates credentials.
3. Auth Service generates a JWT.
4. Client includes JWT in subsequent requests.
5. API Gateway validates/filter requests.
6. Protected microservices process authenticated requests.

## 11. Authorization

Roles:

- MANAGER
- CUSTOMER

Managers can manage products and monitor inventory.

Customers can view products and place orders.

## 12. Inventory Consistency

When an order is placed:

1. Order Service receives the order.
2. Inventory availability is checked.
3. Product information is validated when required.
4. Inventory is reserved/reduced only when sufficient stock exists.
5. Order creation succeeds only after successful inventory processing.
6. Insufficient inventory causes the order to fail without reducing stock.

## 13. Low-Stock Detection

A product is considered low-stock when:

quantity <= reorderLevel

The Inventory Service provides functionality for managers to
identify low-stock products.

## 14. Service Discovery

All application services register with Eureka Server.

Services communicate using logical service names rather than
hardcoded host addresses wherever service discovery is applicable.

## 15. API Gateway

The API Gateway provides:

- Centralized request routing
- JWT request filtering
- Service discovery integration
- Load-balanced routing
- Centralized external entry point

External clients should communicate with the microservices through
the API Gateway.

## 16. Scalability

The architecture supports multiple instances of individual
microservices.

Eureka provides service instance discovery and load-balancing
components distribute requests among available instances.

## 17. Testing Strategy

Each business service will contain:

### Unit Tests

Tests for:

- Service logic
- Validation
- Business rules
- Error handling

### Integration Tests

Tests for:

- REST endpoints
- Database interactions
- Service communication
- Authentication

### API Tests

Postman will be used to test:

- Authentication
- Product APIs
- Inventory APIs
- Order APIs
- Gateway routing