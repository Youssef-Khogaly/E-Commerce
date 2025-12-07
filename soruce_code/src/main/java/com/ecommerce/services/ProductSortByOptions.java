package com.ecommerce.services;

public  enum ProductSortByOptions{
    DATE("addedAt"),PRICE("price");

    private final String field;

    ProductSortByOptions(String field) {
        this.field = field;
    }

    public String toProductField() {
        return  field;
    }
}
