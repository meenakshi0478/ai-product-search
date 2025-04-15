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

- **AI-Powered Smart Search**
  - Semantic search using OpenAI embeddings
  - Fuzzy search for handling typos
  - Synonym matching
  - Category and price range filtering
  - Sorting by relevance and other criteria
  - Returns top 5 most relevant results

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

## Prerequisites

- Java 17 or higher
- PostgreSQL database
- OpenAI API key
- Pinecone account and API key

## Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/meenakshi0478/ai-product-search.git
   cd ai-product-search
   ```

2. **Configure environment variables**
   Create a `.env` file in the project root:
   ```bash
   # Database Configuration
   DATABASE_URL=jdbc:postgresql://localhost:5432/your_database
   DATABASE_USERNAME=your_username
   DATABASE_PASSWORD=your_password

   # JWT Configuration
   JWT_SECRET=your_jwt_secret
   JWT_EXPIRATION=86400000

   # OpenAI Configuration
   OPENAI_API_KEY=your_openai_api_key

   # Pinecone Configuration
   PINECONE_API_KEY=your_pinecone_api_key
   PINECONE_ENVIRONMENT=your_environment
   PINECONE_INDEX=your_index
   ```

3. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

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

### AI-Powered Search

- `POST /api/ai/search` - Smart search with AI capabilities

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

## AI Search Features

### Semantic Search
The AI search uses OpenAI's text-embedding-ada-002 model to convert search queries into vector embeddings. This enables semantic understanding of search terms, allowing for more relevant results.

### Fuzzy Search
The system handles typos and minor variations in search terms through:
- Levenshtein distance-based matching
- Phonetic matching
- Common typo patterns

### Synonym Matching
The search recognizes related terms through:
- Pre-defined synonym mappings
- Contextual understanding from embeddings
- Domain-specific terminology

### Filters & Sorting
Search results can be filtered and sorted by:
- Category
- Price range
- Relevance score
- Popularity
- Date added

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
   - AI service errors

## Security

- All admin endpoints are protected with `@PreAuthorize("hasRole('ADMIN')")`
- JWT tokens are required for authenticated endpoints
- Passwords are securely hashed using BCrypt
- Input validation is performed on all requests
- SQL injection prevention through parameterized queries
- XSS protection through proper data sanitization

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 