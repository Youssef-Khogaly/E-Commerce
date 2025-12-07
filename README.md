# E-Commerce REST API

A demo e-commerce REST API built with **Spring Boot**, **Spring JPA**, **Hibernate**, **Spring Security**, and **MySQL**.  
Supports product management, categories, cart, orders, checkout with Stripe payment Gateway, and JWT-based stateless authentication.

---

## Features
- **User Authentication:** Registration & login using JWT with Spring Security filters (stateless authentication)
- **Product Management:**  
  - Public: Search, pagination, sorting, filtering, view product details  
  - Admin: Create, update products  
- **Category Management (Admin):** CRUD operations  
- **Cart Management (/me):** Add, update, remove items, view cart  
- **Checkout (/me):** Stripe payment integration  
- **Order Management (/me):** View orders, get order details, cancel orders  
- **Security:** JWT-secured endpoints; admin-only access where applicable  

---

## API Endpoints

### Public
- `POST /api/auth/register` – Register user  
- `POST /api/auth/login` – Login & receive JWT  
- `GET /api/products` – Search products (pagination, filter, sort)  
- `GET /api/products/{id}` – Get product details  
- `POST /api/webhook/stripe` – handle Stripe webhook events  

### Admin
- `POST /api/products` – Create product  
- `PUT /api/products/{id}` – Update product  
- `POST /api/categories?name=` – Create category  
- `DELETE /api/categories/{id}` – Delete category  

### Authenticated User (/me)
apply to current authenticated user
- **Cart**
  - `POST /api/me/cart/items` – Add item  
  - `PUT /api/me/cart/items` – Update quantity  
  - `DELETE /api/me/cart/items` – Remove item  
  - `GET /api/me/cart` – View cart  
- **Checkout**
  - `POST /api/me/checkout` – Checkout via Stripe  
- **Orders**
  - `GET /api/me/orders` – List orders  
  - `GET /api/me/orders/{id}` – Get order by ID  
  - `POST /api/me/orders/{id}/cancel` – Cancel order  

---
## To Do / Future Improvements
- [ ]  Implement product image upload using presigned urls
- [ ]  Implement caching for product search to improve performance
- [ ]   Use multithreading to improve API performance
- [ ]   implement email verification feature 
- [ ]   Add email notifications for order creation, shipment, and cancellation
- [ ]   integration with shipping service
- [ ]   Expand Admin Dashboard with charts and insights for orders, revenue, and products  
- [ ]   Implement product reviews and ratings  


## Tech Stack
- **Backend:** Spring Boot, Spring JPA, Hibernate, Spring MVC  
- **Security:** Spring Security, JWT  
- **Database:** MySQL  
- **Payment Gateway:** Stripe  
- **Build Tool:** Maven  
