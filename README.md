# E-Commerce REST API

A e-commerce REST API built with **Spring Boot**, **Spring JPA**, **Hibernate**, **Spring Security**, and **MySQL**.  
Supports product management, categories, cart, orders, checkout with Stripe payment Gateway, integration with Amazon S3 for efficient image storage and management. and JWT-based stateless authentication.

---

## Features
- **User Authentication:** Registration & login using JWT with Spring Security filters (stateless authentication)
- **Product Management:**  
  - Public: Search, pagination, sorting, filtering, view product details  
  - Admin: Create, update products 
- **Image Management(Admin):**
  - Admin: upload and delete images to amazon s3 storage ,  attach images , set main image for a product   
- **Category Management (Admin):** CRUD operations  
- **Cart Management (/me):** Add, update, remove items, view cart  
- **Checkout (/me):** Stripe payment integration  
- **Order Management (/me):** View orders, get order details, cancel orders  
- **Security:** JWT-secured endpoints; admin-only access where applicable  

---
## Database schema
![alt text](https://github.com/Youssef-Khogaly/E-Commerce/blob/main/E_Commerce_DBSCHEM_DEMOV2.png?raw=true)


--
API Documentation — Swagger UI

The API is documented using OpenAPI 3 and can be explored  through Swagger UI.
After running the application, visit:
    http://localhost:8080/swagger-ui/index.html

The generated OpenAPI specification is also available at:

http://localhost:8080/v3/api-docs
---
## To Do / Future Improvements
- [X]  Implement product images end points and integrate with amazon s3 cloud storage
- [ ]  Implement caching for product search to improve performance
- [ ]  Use multithreading to improve API performance
- [ ]  implement email verification feature 
- [ ]  Add email notifications for order creation, shipment, and cancellation
- [ ]  integration with shipping service
- [ ]  Expand Admin Dashboard with charts and insights for orders, revenue, and products  
- [ ]  Implement product reviews and ratings  
- [X]  Use docker and docker compose to simplify setup and deployment
## Running with Docker
### Configuration

1.  Clone the repository:
    ```sh
    git clone https://github.com/youssef-khogaly/E-learning-Platform.git
    cd E-learning-Platform
    ```

2.  Create the backend environment file at `source_code/backend.env` with your credentials:
    ```env
    # Database Configuration
    DB_IP=db-sql-service
    DB_PORT=3306
    DB_SchemaName=E_learning
    DB_USER=your_db_user
    DB_PASS=your_db_password

    # Security
    JWT_SECRET=your_super_secret_jwt_key_with_at_least_256_bits_of_entropy

    # External Service API Keys
    StripeApisec=sk_your_stripe_secret_key
    StripeWhsec=whsec_your_stripe_webhook_secret
            
    # AWS
    AWS_SECRET_ACCESS_KEY=
    AWS_ACCESS_KEY_ID=
    AWS_REGION=
    cloud.aws.bucket.name=
    ```

3.  Create the database environment file at `DB-init/db-sql.env`:
    ```env
    MYSQL_ROOT_PASSWORD=your_root_password
    MYSQL_DATABASE=E_learning
    MYSQL_USER=your_db_user
    MYSQL_PASSWORD=your_db_password

    ### Running the Application

4. Build the application's JAR file using Maven:
    ```sh
    cd source_code
    ./mvnw clean package
    ```

5. Start the application and the MySQL database using Docker Compose:
    ```sh
    docker-compose up --build -d
    ```

6. Stripe webhook 
   1. install Striple CLI https://docs.stripe.com/stripe-cli/install
   2. login to stripe account and forward webhook
       ```bash
           stripe login
           stripe listen --events checkout.session.completed,checkout.session.expired --forward-to localhost:8080/api/webhooks/stripe

       ```
  3. do not forget to set StripeWhsec enviroment variable to the webhook secret
## Tech Stack
- **Backend:** JAVA 17 ,Spring Boot, Spring JPA, Hibernate, Spring MVC , Spring Security, JWT
- **Database:** MySQL  
- **Payment Gateway:** Stripe
- - **Cloud storage:** Amazon s3
- **Build Tool:** Maven  Docker Docker-compose
