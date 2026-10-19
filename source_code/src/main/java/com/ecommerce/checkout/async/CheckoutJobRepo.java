package com.ecommerce.checkout.async;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckoutJobRepo extends ListCrudRepository<CheckoutJob, UUID> {



    public Optional<CheckoutJob> findByIdAndUserId(UUID id, Long userId);
}
