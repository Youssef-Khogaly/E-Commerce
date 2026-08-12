create database if not exists E_Commerce ;

use E_Commerce;
set default_storage_engine = InnoDB;

-- USERS
CREATE TABLE IF NOT EXISTS `user` (
                                      usr_id  bigint PRIMARY KEY AUTO_INCREMENT,
                                      usr_name VARCHAR(16) UNIQUE NOT NULL,
    usr_email VARCHAR(127) UNIQUE NOT NULL,
    usr_pass VARCHAR(128) NOT NULL,
    `role` ENUM('customer','admin') NOT NULL DEFAULT 'customer',
    isDeleted BOOLEAN DEFAULT FALSE,
    isEmailVerified boolean default false,
    constraint usr_email_delete unique (usr_email,isDeleted)
    );

-- CUSTOMERS & ADMINS
CREATE TABLE IF NOT EXISTS customer (
                                        cust_id  bigint PRIMARY KEY,
                                        CONSTRAINT fk_customer_user FOREIGN KEY (cust_id)
    REFERENCES `user`(usr_id)
    ON UPDATE CASCADE
    ON DELETE restrict
    );

CREATE TABLE IF NOT EXISTS `admin` (
                                       admin_id  bigint PRIMARY KEY,
                                       lastLogin TIMESTAMP DEFAULT NULL,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP  DEFAULT NULL on update current_timestamp,
                                       CONSTRAINT fk_admin_user FOREIGN KEY (admin_id)
    REFERENCES `user`(usr_id)
    ON UPDATE CASCADE
    ON DELETE restrict
    );

-- PRODUCTS & IMAGES
CREATE TABLE IF NOT EXISTS product (
    product_id  bigint PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(63) NOT NULL,
    description MEDIUMTEXT,
    price bigint NOT NULL DEFAULT 0.00 check(price >= 0),
    addedAt TIMESTAMP default CURRENT_TIMESTAMP not null
    );
create index product_priceIdx on product(price) using BTREE;
create FULLTEXT INDEX product_title_idx on product(title);
create index  product_addedAt_idx  on product(addedAt) using BTREE;

create table if not exists product_stock(
    product_id bigint primary key ,
    stock INT  not null DEFAULT 0 CHECK (stock >= 0),
    reservedStock INT not null default 0 check ( reservedStock >= 0),
    availableStock int  generated always as (stock-reservedStock)virtual,
    constraint frg_p foreign key (product_id) references product(product_id)
);
create table if not exists image
(
    image_id        bigint auto_increment
        primary key,
    image_url       varchar(256)                           not null,
    storage_key     varchar(256)                           not null,
    storageProvider enum ('AMAZON_S3') default 'AMAZON_S3' not null,
    region          varchar(64)            default null      null
);

create table if NOT EXISTS ProductImages
(
    product_id bigint               not null,
    image_id   bigint               not null,
    isMain     boolean default false not null,
    primary key (product_id, image_id),
    constraint ProductImages_image_image_id_fk
        foreign key (image_id) references image (image_id)
            on update cascade on delete cascade,
    constraint ProductImages_product_product_id_fk
        foreign key (product_id) references product (product_id)
            on update cascade on delete cascade
);

-- ====================================================
-- CATEGORY
-- ====================================================
CREATE TABLE IF NOT EXISTS category (
                                        cate_id bigint PRIMARY KEY AUTO_INCREMENT not null ,
                                        name VARCHAR(64) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS product_category (
   product_id  bigint NOT NULL,
   category_id bigint NOT NULL,
   PRIMARY KEY (product_id,category_id),

    CONSTRAINT fk_pc_product FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id)
    REFERENCES category(cate_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS CustomerOrder (
                                             id  bigint auto_increment primary key,
                                             cust_id  bigint  ,
                                             recipientName VARCHAR(63) not null ,
                                             recipientPhone VARCHAR(63) not null ,
                                             country VARCHAR(64)  not null ,
                                             city VARCHAR(64) not null ,
                                             street VARCHAR(63) not null ,
                                             building VARCHAR(63) not null ,
                                             order_state ENUM('CANCELED','REFUNDED','EXPIRED','PENDING','PROCESSING','SHIPPING','DELIVERED') not null ,
                                             currency_code varchar(4) not null,
                                             subTotal bigint ,

                                            session_id varchar(255) default null unique ,
                                            expireAt bigint not null ,
                                            paymentMethod enum('Stripe'),
                                             CONSTRAINT fk_order_customer FOREIGN KEY (cust_id)
                                                 REFERENCES customer(cust_id)
                                                 ON DELETE RESTRICT
                                                 ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_item (
                                          id bigint auto_increment primary key not null ,
                                          order_id bigint not null ,
                                          product_id bigint  ,
                                          name  varchar(64) ,
                                          description varchar(256),
                                          quantity INT NOT NULL CHECK (quantity > 0),
                                          unitPriceInCents bigint not null ,
                                          discountInCents bigint not null,
                                          subTotalInCents bigint  not null ,
                                          currency_code varchar(4) not null,
                                          CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id)
                                              REFERENCES CustomerOrder(id)
                                              ON UPDATE CASCADE
                                              ON DELETE RESTRICT
);

-- PAYMENTS
CREATE TABLE IF NOT EXISTS Payment (
                                       id bigint auto_increment primary key ,
                                       order_id  bigint not null ,
                                       paymentState ENUM('FAILEd' ,'PAID' ,'REFUNDED') not null,
                                       transaction_id varchar(255) unique not null,
                                       amount bigint not null,
                                       currency varchar(4) not null,
                                       CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
                                           REFERENCES CustomerOrder(id)
                                           ON UPDATE CASCADE
                                           ON DELETE RESTRICT
);
-- ====================================================
-- REVIEWS
-- ====================================================
CREATE TABLE IF NOT EXISTS review (
                                      cust_id  bigint,
                                      product_id  bigint,
                                      comment TEXT DEFAULT NULL,
                                      rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id, cust_id),
    CONSTRAINT fk_review_customer FOREIGN KEY (cust_id)
    REFERENCES customer(cust_id)
                                           ON DELETE RESTRICT
                                           ON UPDATE CASCADE,
    CONSTRAINT fk_review_product FOREIGN KEY (product_id)
    REFERENCES product(product_id)
                                           ON DELETE RESTRICT
                                           ON UPDATE CASCADE
    ) DEFAULT CHARSET=utf8mb4;

