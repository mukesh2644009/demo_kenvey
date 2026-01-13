# ShopEase E-Commerce Platform

A full-featured e-commerce platform built with Java Spring Boot, featuring a modern customer storefront and comprehensive admin panel.

## 🚀 Features

### Customer Features
- **Product Browsing**: Browse products by category, brand, color, and price range
- **Search**: Full-text search across products
- **Shopping Cart**: Add, update, and remove items
- **Checkout**: Complete order placement with discount codes
- **Order Tracking**: Track order status and shipping
- **Warranty Management**: View product warranties

### Admin Panel
- **Dashboard**: Real-time overview of orders, sales, and inventory
- **Product Management**: Add, edit, and manage products (single and bulk)
- **Order Management**: View, update status, and track orders
- **Customer Analytics**: Top buyers, purchase frequency
- **Warranty Tracking**: Monitor warranties and expiring alerts
- **Discount Management**: Create and manage promotional codes

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2
- **Database**: H2 (development) / PostgreSQL (production)
- **Security**: Spring Security with JWT Authentication
- **Frontend**: Thymeleaf, Bootstrap 5
- **API Documentation**: OpenAPI/Swagger

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## 🏃 Running the Application

### Development Mode

1. Clone the repository:
```bash
cd ecommerce-platform
```

2. Run with Maven:
```bash
mvn spring-boot:run
```

3. Access the application:
   - **Store**: http://localhost:8080
   - **Admin Panel**: http://localhost:8080/admin
   - **API Docs**: http://localhost:8080/swagger-ui.html
   - **H2 Console**: http://localhost:8080/h2-console

### Default Credentials

**Admin Account:**
- Email: admin@shopeasy.com
- Password: admin123

**Customer Account:**
- Email: john@example.com
- Password: password123

## 📁 Project Structure

```
src/main/java/com/ecommerce/
├── config/          # Configuration classes
├── controller/      # REST & MVC Controllers
│   └── admin/       # Admin controllers
├── dto/             # Data Transfer Objects
│   └── auth/        # Authentication DTOs
├── entity/          # JPA Entities
├── exception/       # Custom exceptions
├── repository/      # Spring Data repositories
├── security/        # JWT & Security config
└── service/         # Business logic

src/main/resources/
├── static/
│   ├── css/         # Stylesheets
│   └── js/          # JavaScript files
├── templates/
│   ├── admin/       # Admin templates
│   └── fragments/   # Reusable fragments
└── application.yml  # Configuration
```

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Products
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product details
- `GET /api/products/search?q=` - Search products
- `GET /api/products/filter` - Filter products

### Cart
- `GET /api/cart` - Get cart items
- `POST /api/cart/add` - Add to cart
- `PUT /api/cart/update` - Update cart item
- `DELETE /api/cart/remove/{productId}` - Remove from cart

### Orders
- `POST /api/orders/checkout` - Place order
- `GET /api/orders` - Get user orders
- `GET /api/orders/track/{orderNumber}` - Track order

### Admin Endpoints (Requires ADMIN role)
- `/api/admin/dashboard` - Dashboard data
- `/api/admin/products` - Product management
- `/api/admin/orders` - Order management
- `/api/admin/customers` - Customer management
- `/api/admin/warranties` - Warranty management
- `/api/admin/discounts` - Discount management

## 💾 Database Schema

### Core Tables
- **users** - Customer and admin accounts
- **products** - Product catalog
- **categories** - Product categories
- **orders** - Customer orders
- **order_items** - Order line items
- **cart_items** - Shopping cart
- **warranties** - Product warranties
- **discounts** - Promotional codes

## 🔐 Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control (CUSTOMER, ADMIN)
- CORS configuration for API access

## 📦 Building for Production

```bash
mvn clean package -DskipTests
java -jar target/ecommerce-platform-1.0.0-SNAPSHOT.jar
```

### Production Configuration

For production, update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
```

## 🎨 Customization

### Styling
The application uses CSS custom properties for easy theming:

```css
:root {
    --primary-color: #1a1a2e;
    --accent-color: #e94560;
    /* ... */
}
```

### Adding New Features
1. Create entity in `entity/` package
2. Create repository in `repository/` package
3. Add service logic in `service/` package
4. Create controllers in `controller/` package

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

---

Built with ❤️ using Spring Boot

