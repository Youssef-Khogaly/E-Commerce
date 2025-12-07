package com.ecommerce.repository.Reviews;

import com.ecommerce.entities.review.Review;
import com.ecommerce.entities.review.ReviewID;
import org.springframework.data.repository.CrudRepository;

public interface ReviewCrudRepo extends CrudRepository<Review, ReviewID> {
}
