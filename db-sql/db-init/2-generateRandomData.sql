
DELIMITER $$

DROP PROCEDURE IF EXISTS populate_products$$

CREATE PROCEDURE populate_products()
BEGIN
    DECLARE i INT DEFAULT 1;


    WHILE i <= 10000 DO
        INSERT INTO product (title, description, price)
        VALUES (
            CONCAT(
                ELT(FLOOR(1 + RAND() * 6), 'Laptop','Phone','Tablet','Monitor','Keyboard','Mouse'),
                ' Model ', i
            ),
            CONCAT('This is a description for product ', i),
            ROUND(RAND() * 1000000000 + 500, 2)  -- price between 500 and 1000000000
        );
        SET i = i + 1;
END WHILE;
END$$

DELIMITER ;

-- Call the procedure to populate products
CALL populate_products();

-- =====================================
-- 2. Product stock table
-- =====================================
DELIMITER $$

DROP PROCEDURE IF EXISTS populate_product_stock$$

CREATE PROCEDURE populate_product_stock()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE stock_tmp int;
    DECLARE reservedStock_tmp int;
    WHILE i <= 10000 DO
        set stock_tmp = FLOOR(RAND() * 100 + 1);   -- stock between 1 and 100;
        set reservedStock_tmp = FLOOR(RAND() * stock_tmp);  -- reservedStock between 0 and stock_tmp
        INSERT INTO product_stock (product_id, stock, reservedStock)
        VALUES (
            i,
            stock_tmp,
            reservedStock_tmp
        );
        SET i = i + 1;
END WHILE;
END$$

DELIMITER ;

-- Call the procedure to populate product_stock
CALL populate_product_stock();

-- add electronics category
insert into category(cate_id,name) values(1,'electronics');


DROP PROCEDURE IF EXISTS addCategoryToAllProducts$$
DELIMITER $$

CREATE PROCEDURE addCategoryToAllProducts()
BEGIN
    -- Insert category_id = 1 for all products that don't already have it
    INSERT INTO product_category (product_id, category_id)
    SELECT p.product_id, 1
    FROM product p
    WHERE NOT EXISTS (
        SELECT 1
        FROM product_category pc
        WHERE pc.product_id = p.product_id
          AND pc.category_id = 1
    );
END $$

DELIMITER ;

call addCategoryToAllProducts();

