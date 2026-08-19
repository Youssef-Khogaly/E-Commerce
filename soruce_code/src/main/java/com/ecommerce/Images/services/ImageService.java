package com.ecommerce.Images.services;

import com.ecommerce.Exception.InvalidImageException;
import com.ecommerce.Images.entity.Image;
import com.ecommerce.Images.StorageProvider;
import com.ecommerce.Images.repos.ImagesJpaRepo;
import com.ecommerce.Inegration.CloudImages.CloudStorageService;
import com.ecommerce.util.ImageUtil;
import com.ecommerce.util.ImageWrapper;
import com.ecommerce.util.ImagesFormat;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class ImageService implements IImageService {
    private final CloudStorageService cloudStorageService;
    private final String bucketName;
    private final ImagesJpaRepo imagesJpaRepo;
    public ImageService(CloudStorageService cloudStorageService, @Value("${cloud.aws.bucket.name}") String bucketName, ImagesJpaRepo imagesJpaRepo) {
        this.cloudStorageService = cloudStorageService;
        this.bucketName = bucketName;
        this.imagesJpaRepo = imagesJpaRepo;
    }

    private String generateImageId(ImageWrapper image) {
        UUID id = UUID.randomUUID();
        return id + image.getExtension();
    }
    private ImageWrapper convertToWebp(ImageWrapper imageWrapper){
        if(imageWrapper.getContentType().equalsIgnoreCase(ImagesFormat.WEBP.getMediaType()))
            return  imageWrapper;
        try{
            return ImageUtil.convertToWebp(imageWrapper);
        } catch (IOException e) {
            throw new InvalidImageException(e.getMessage());
        }

    }
    private List<String> uploadImagesToCloud(List<ImageWrapper> images)  {

        List<ImageWrapper>imageWebpList =  images.stream().map(this::convertToWebp).toList();
        List<String>ids = new ArrayList<>(images.size());
        for(ImageWrapper image : imageWebpList){
            String idStr = generateImageId(image);
            cloudStorageService.uploadFile(bucketName, idStr,image.getBytes(),image.getContentType());
            ids.add(idStr);
        }
        return ids;
    }
    private void deleteImageFromCloud(String storageKey){
        cloudStorageService.deleteFile(bucketName,storageKey);
    }
    public void deleteImage(Long dbId){
        Image image = imagesJpaRepo.findById(dbId).orElse(null);
        if(image == null)
            return;
        deleteImageFromCloud(image.getStorage_key());
        imagesJpaRepo.deleteById(image.getId());

    }

    public Map<Long , String> saveImages(List<ImageWrapper> imageWrapperList) {
        String region = cloudStorageService.getRegion();
        StorageProvider provider = cloudStorageService.getProvider();
        List<String>storageKeys = uploadImagesToCloud(imageWrapperList);
        List<Image>imagesEntities = new ArrayList<>(imageWrapperList.size());
        try{

                for(String skey : storageKeys){
                    Image image = new Image();
                    image.setImageUrl(getPublicUrl(skey).toString());
                    image.setRegion(region);
                    image.setStorage_key(skey);
                    image.setStorageProvider(provider);
                    imagesEntities.add(image);
                }


                return imagesJpaRepo.saveAll(imagesEntities).stream()
                        .collect(Collectors.toMap(Image::getId , Image::getImageUrl));

        } catch (Exception e) {
            // delete from cloud in case saving to do failed
            storageKeys.forEach(this::deleteImageFromCloud);
            throw new RuntimeException(e);
        }


    }
    public String getImage(Long imgId){
        return imagesJpaRepo.findById(imgId).orElseThrow(() -> new EntityNotFoundException("image id:" + imgId +" doesn't exists")).getImageUrl();
    }
    public Set<Long> getExistingIds(List<Long> imgIds){
        return imagesJpaRepo.findExistingIds(imgIds);
    }
    private URL getPublicUrl(String id) throws MalformedURLException {
        return cloudStorageService.getPublicURL(bucketName,id);
    }

}
