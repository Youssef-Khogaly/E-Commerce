package com.ecommerce.Inegration.CloudImages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Bean
    S3Client s3Client(@Value("${AWS_SECRET_ACCESS_KEY}") String secKey
            , @Value("${AWS_ACCESS_KEY_ID}") String accessKey , @Value("${AWS_REGION}") String region){
        var credentials = AwsBasicCredentials.create(accessKey,secKey);
        return S3Client
                .builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
    @Bean
    S3Presigner s3Presigner(@Value("${AWS_SECRET_ACCESS_KEY}") String secKey
            , @Value("${AWS_ACCESS_KEY_ID}") String accessKey , @Value("${AWS_REGION}") String region){
        var credentials = AwsBasicCredentials.create(accessKey,secKey);
        return S3Presigner
                .builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
