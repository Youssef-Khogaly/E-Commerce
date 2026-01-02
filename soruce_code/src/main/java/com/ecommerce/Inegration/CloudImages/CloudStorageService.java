package com.ecommerce.Inegration.CloudImages;

import com.ecommerce.entities.images.StorageProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public interface CloudStorageService {



    boolean uploadFile(String bucketName,String key, byte[] imageBtyes , String contentType);
    public URL generatePreSignedGetUrl(String bucketName, String key , Duration duration);
    public URL getPublicURL(String bucketName,String key)throws MalformedURLException;
    public void deleteFile(String bucketName , String key);
    public StorageProvider getProvider();
    public default String getRegion(){
        return null;
    }
}
