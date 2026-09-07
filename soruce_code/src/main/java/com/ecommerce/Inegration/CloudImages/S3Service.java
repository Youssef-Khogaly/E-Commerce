package com.ecommerce.Inegration.CloudImages;

import com.ecommerce.Images.StorageProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Service
public class S3Service implements CloudStorageService{
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String publicUrlFormat = "http://s3.%s.localhost.localstack.cloud:4566/%s/%s"; // region , bucketname , key
    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }
    @Override
    public boolean uploadFile(String bucketName,String key, byte[] imageBtyes , String contentType) {
        PutObjectResponse response = s3Client.putObject(
                PutObjectRequest.builder().bucket(bucketName).key(key).
                        contentType(contentType).build(), RequestBody.fromBytes(imageBtyes));

        return response.sdkHttpResponse().isSuccessful();
    }

    @Override
    public URL generatePreSignedGetUrl(String bucketName , String key , Duration duration) {
        if(duration.compareTo(Duration.ofDays(7)) > 0){
            throw new IllegalArgumentException("max presigned url duration for s3 object is 7 days");
        }
            GetObjectRequest objectRequest =  GetObjectRequest.builder().bucket(bucketName).key(key).build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .getObjectRequest(objectRequest)
                    .build();
            // generate url
            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedGetObjectRequest.url();
    }

    @Override
    public URL getPublicURL(String bucketName,String key) throws MalformedURLException {
        String strUrl = publicUrlFormat.formatted(s3Client.serviceClientConfiguration().region().id(), bucketName,key);
        URL url = new URL(strUrl);
        return url;
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }

    @Override
    public StorageProvider getProvider() {
        return StorageProvider.AMAZON_S3;
    }

    @Override
    public String getRegion() {
        return s3Client.serviceClientConfiguration().region().toString();
    }


}
