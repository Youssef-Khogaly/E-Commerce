
## **1. Users**

**Table:** `user`

|Column|Type|Constraints / Notes|
|---|---|---|
|`usr_id`|BIGINT|Primary Key, Auto Increment|
|`usr_name`|VARCHAR(16)|Unique, Not Null, regex validation (A-Z, a-z, 0-9, `_`, 6–16 chars, starts with letter)|
|`usr_email`|VARCHAR(127)|Unique, Not Null|
|`usr_pass`|VARCHAR(128)|Not Null, password must include lowercase, uppercase, digits, min length 8|
|`role`|ENUM('customer','admin')|Default: 'customer', Not Null|
|`isDeleted`|BOOLEAN|Default: FALSE (used for soft deletion)|
**Notes:**

- `usr_name` and `usr_pass` have CHECK constraints for basic validation.
    
- `role` distinguishes between customers and admins.

-----
## **2. Customers and Admins**

**Table:** `customer`

|Column|Type|Constraints / Notes|
|---|---|---|
|`cust_id`|BIGINT|PK, FK → `user(usr_id)`|
**Table:** `admin`

|Column|Type|Constraints / Notes|
|---|---|---|
|`admin_id`|BIGINT|PK, FK → `user(usr_id)`|
|`lastLogin`|TIMESTAMP|Nullable, stores last login|
|`created_at`|TIMESTAMP|Not Null, default CURRENT_TIMESTAMP|
|`updated_at`|TIMESTAMP|Updated automatically when user info changes|
**Notes:**

- One-to-one relationship with `user`.
    
- Triggers automatically create corresponding customer/admin records when a new user is added.

----
## **3. Customer Addresses**

**Table:** `customer_address`

| Column     | Type        | Notes                    |
| ---------- | ----------- | ------------------------ |
| `cust_id`  | BIGINT      | FK → `customer(cust_id)` |
| `country`  | VARCHAR(63) | Part of composite PK     |
| `city`     | VARCHAR(63) | Part of composite PK     |
| `street`   | VARCHAR(63) | Part of composite PK     |
| `building` | VARCHAR(63) | Part of composite PK     |
**Notes:**

- Composite PK ensures uniqueness of each address for a customer.
    
- `ON DELETE RESTRICT` prevents deletion of customer if address exists.
    

----
## **4. Products and Images**

**Table:** `product`

| Column        | Type          | Notes                         |
| ------------- | ------------- | ----------------------------- |
| `product_id`  | BIGINT        | PK, Auto Increment            |
| `stock`       | INT           | Default 0, cannot be negative |
| `title`       | VARCHAR(63)   | Not Null                      |
| `description` | MEDIUMTEXT    | Optional                      |
| `price`       | DECIMAL(12,2) | Default 0.00, must be >=0     |
| `isDeleted`   | BOOLEAN       | Default FALSE (soft deletion) |

**Table:** `product_image`

|Column|Type|Notes|
|---|---|---|
|`prod_id`|BIGINT|FK → `product(product_id)`|
|`image_url`|VARCHAR(512)|PK with `prod_id`|
|`isMain`|BOOLEAN|Indicates main image|

**Notes:**

- Each product can have multiple images.
    
- Only one main image per product should be enforced in application logic.

------
## **5. Categories**

**Table:** `category`

|Column|Type|Notes|
|---|---|---|
|`cate_id`|INT|PK, Auto Increment|
|`name`|VARCHAR(16)|Unique, Not Null|
|`parent_id`|INT|FK → `category(cate_id)`, Nullable (top-level categories have NULL parent)|

**Table:** `product_category`

| Column        | Type                      | Notes                      |
| ------------- | ------------------------- | -------------------------- |
| `product_id`  | BIGINT                    | FK → `product(product_id)` |
| `category_id` | INT                       | FK → `category(cate_id)`   |
| PK            | (product_id, category_id) | Many-to-many relationship  |

-----
## **6. Cart and Cart Items**

**Table:** `cart`

| Column             | Type          | Notes                                            |
| ------------------ | ------------- | ------------------------------------------------ |
| `cust_id`          | BIGINT        | PK, FK → `customer(cust_id)`                     |
| `cart_total_price` | DECIMAL(12,2) | Optional (calculated in application or triggers) |

**Table:** `cart_item`

|Column|Type|Notes|
|---|---|---|
|`prod_id`|BIGINT|FK → `product(product_id)`|
|`cust_id`|BIGINT|FK → `customer(cust_id)`|
|`unit_price`|DECIMAL(12,2)|Must be > 0|
|`quantity`|INT|Must be > 0|
|`quantityPrice`|DECIMAL(12,2) AS (unit_price * quantity) VIRTUAL|Auto-calculated|
**Notes:**

- Cart items are automatically added to `cart` via trigger.
    
- `quantityPrice` is calculated automatically.


------
## **7. Reviews**

**Table:** `review`

| Column       | Type                  | Notes                               |
| ------------ | --------------------- | ----------------------------------- |
| `cust_id`    | BIGINT                | FK → `customer(cust_id)`            |
| `product_id` | BIGINT                | FK → `product(product_id)`          |
| `comment`    | TEXT                  | Optional                            |
| `rating`     | TINYINT               | 1–5, Not Null                       |
| `created_at` | TIMESTAMP             | Default current time                |
| `updated_at` | TIMESTAMP             | Auto-update on change               |
| PK           | (product_id, cust_id) | One review per product per customer |

-----
## **8. Orders and Order Items**

**Table:** `order`

|Column|Type|Notes|
|---|---|---|
|`order_id`|BIGINT|PK, Auto Increment|
|`recipientName`|VARCHAR(63)|Not Null|
|`recipientPhone`|VARCHAR(63)|Not Null|
|`country, city, street, building`|VARCHAR|Shipping address|
|`order_state`|ENUM('canceled','refunded','pending','shipping','delivered')|Current order state|
|`total_price`|DECIMAL(12,2)|Not Null|
|`cust_id`|BIGINT|FK → `customer(cust_id)`, SET NULL on delete|

**Table:** `order_item`

|Column|Type|Notes|
|---|---|---|
|`order_id`|BIGINT|FK → `order(order_id)`|
|`product_id`|BIGINT|FK → `product(product_id)`|
|`quantity`|INT|Must > 0|
|`unit_price`|DECIMAL(12,2)|Must > 0|
|`quantity_price`|DECIMAL(12,2) AS (quantity * unit_price) VIRTUAL|Auto-calculated|
|PK|(order_id, product_id)|

------
## **9. Payments**

**Table:** `payment`

| Column         | Type                                            | Notes                     |
| -------------- | ----------------------------------------------- | ------------------------- |
| `payment_id`   | BIGINT                                          | PK, Auto Increment        |
| `order_id`     | BIGINT                                          | FK → `order(order_id)`    |
| `paymentState` | ENUM('failed','pending','confirmed','refunded') | Default: pending          |
| `card_type`    | VARCHAR(31)                                     | Not Null                  |
| `card_last_4`  | CHAR(4)                                         | Not Null                  |
| `created_at`   | TIMESTAMP                                       | Default CURRENT_TIMESTAMP |
| `updated_at`   | TIMESTAMP                                       | Auto-update on change     |

-----

## **10. Customer Credit Cards**

**Table:** `customerCreditCards`

|Column|Type|Notes|
|---|---|---|
|`cust_id`|BIGINT|FK → `customer(cust_id)`|
|`card_num`|CHAR(16)|PK with `cust_id`|
|`ccv`|CHAR(3)|Not Null|
|`expMonth`|TINYINT|1–12|
|`expYear`|TINYINT|0–99|
|`expDate`|CHAR(5)|Virtual column 'MM/YY'|

------
## **11. Triggers**

|Trigger Name|Event|Table|Purpose|
|---|---|---|---|
|`addNewUser`|AFTER INSERT|`user`|Automatically create `customer` or `admin`|
|`newCustomerCartCreation`|AFTER INSERT|`customer`|Automatically create cart for new customer|
|`user_deletion`|BEFORE DELETE|`user`|Prevent physical deletion (soft delete)|
|`admin_update`|AFTER UPDATE|`user`|Update `admin.updated_at` when user info changes|
|`product_delete`|BEFORE DELETE|`product`|Prevent physical deletion (soft delete)|
|`Prevent_PaymentHistory_deletion`|BEFORE DELETE|`payment`|Prevent deletion of payment history|
|`prevent_orderDeletion`|BEFORE DELETE|`order`|Prevent deletion of order history|
