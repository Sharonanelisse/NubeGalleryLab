package com.darwinruiz.uspglocalgallerylab.repositories;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class S3Repository {

    private final String bucketName = System.getenv("BUCKET");
    private final Region region = Region.of(System.getenv("AWS_REGION"));

    private final S3Client s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

    private final S3Presigner presigner = S3Presigner.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

    public List<String> listByPrefix(String prefix) {
        List<String> urls = new ArrayList<>();

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix) // 👈 imágenes/
                .build();

        for (S3Object obj : s3.listObjectsV2(request).contents()) {
            // Filtrar solo imágenes
            if (obj.key().matches(".*\\.(png|jpg|jpeg|gif|webp)$")) {
                GetObjectRequest getReq = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(obj.key())
                        .build();

                GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10)) // 10 min
                        .getObjectRequest(getReq)
                        .build();

                URL presignedUrl = presigner.presignGetObject(presignReq).url();
                urls.add(presignedUrl.toString());
            }
        }
        return urls;
    }
}
