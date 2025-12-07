package com.ecommerce.entities.review;

import com.ecommerce.entities.user.Customer;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class ReviewID implements Serializable {

    private BigInteger customer;
    private BigInteger product;

    public BigInteger getCustomer() {
        return customer;
    }

    public void setCustomer(BigInteger customer) {
        this.customer = customer;
    }

    public BigInteger getProduct() {
        return product;
    }

    public void setProduct(BigInteger product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReviewID reviewID = (ReviewID) o;
        return Objects.equals(getCustomer(), reviewID.getCustomer()) && Objects.equals(getProduct(), reviewID.getProduct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomer(), getProduct());
    }
}
