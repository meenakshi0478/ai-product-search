# AI Product Search API

A Spring Boot application that provides a RESTful API for product search and management with AI-powered search capabilities.

## Features

- **User Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (ADMIN/USER)
  - Secure password handling
  - User registration and login

- **Product Management**
  - Create, update, and delete products (Admin only)
  - Search products by name, category, or brand
  - Filter products by price range
  - Get latest products
  - Get products by category
  - Remove duplicate products

- **Error Handling**
  - Global exception handling
  - Consistent error response format
  - Detailed error messages
  - Proper HTTP status codes

- **Security**
  - Role-based access control
  - JWT token authentication
  - Secure endpoints
  - Input validation

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"  // Optional, defaults to USER
  }
  ```

- `POST /api/auth/login` - Login and get JWT token
  ```json
  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```

### Products (Admin Only)

- `POST /api/products/admin/add` - Create a new product
  ```json
  {
    "name": "Product Name",
    "description": "Product Description",
    "price": 100.00,
    "category": "Category",
    "brand": "Brand"
  }
  ```

- `PUT /api/products/admin/{id}` - Update a product
  ```json
  {
    "name": "Updated Name",
    "description": "Updated Description",
    "price": 150.00,
    "category": "Updated Category",
    "brand": "Updated Brand"
  }
  ```

- `DELETE /api/products/admin/{id}` - Delete a product

- `DELETE /api/products/admin/cleanup-duplicates` - Remove duplicate products

### Products (Public)

- `GET /api/products/search?query=search_term` - Search products
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/products/price-range?minPrice=10&maxPrice=100` - Get products by price range
- `GET /api/products/latest` - Get latest products
- `GET /api/products/latest/{category}` - Get latest products by category

## Response Format

### Success Response
```json
{
    "status": "success",
    "message": "Operation successful message",
    "data": {
        // Response data
    }
}
```

### Error Response
```json
{
    "status": "error",
    "message": "Error message describing what went wrong"
}
```

### Info Response (Empty Results)
```json
{
    "status": "info",
    "message": "Information message about empty results"
}
```

## Error Handling

The API provides detailed error messages for various scenarios:

1. **Authentication Errors** (401 Unauthorized)
   - Invalid credentials
   - Expired token
   - Missing authentication

2. **Authorization Errors** (403 Forbidden)
   - Insufficient permissions
   - Access denied

3. **Validation Errors** (400 Bad Request)
   - Invalid input data
   - Missing required fields
   - Invalid format

4. **Resource Not Found** (404 Not Found)
   - Product not found
   - User not found

5. **Server Errors** (500 Internal Server Error)
   - Unexpected errors
   - Database errors

## Security

- All admin endpoints are protected with `@PreAuthorize("hasRole('ADMIN')")`
- JWT tokens are required for authenticated endpoints
- Passwords are securely hashed using BCrypt
- Input validation is performed on all requests
- SQL injection prevention through parameterized queries
- XSS protection through proper data sanitization

## Getting Started

1. Clone the repository
2. Configure the database in `application.properties`
3. Build the project: `mvn clean install`
4. Run the application: `mvn spring-boot:run`

## Dependencies

- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- Lombok
- MySQL
- Maven

## Configuration

Update `application.properties` with your database and JWT settings:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

jwt.secret=your_jwt_secret
jwt.expiration=86400000
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 