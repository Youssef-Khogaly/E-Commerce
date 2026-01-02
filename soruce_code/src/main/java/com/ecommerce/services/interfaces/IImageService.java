package com.ecommerce.services.interfaces;

import com.ecommerce.util.ImageWrapper;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IImageService {
    public String getImage(Long imgId);
    public Set<Long> getExistingIds(List<Long> imgIds);
    public Map<Long , String> saveImages(List<ImageWrapper> imageWrapperList);
    public void deleteImage(Long dbId);
}
