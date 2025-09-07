package com.darwinruiz.uspglocalgallerylab.storage;
import com.darwinruiz.uspglocalgallerylab.controllers.DeleteServlet;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.darwinruiz.uspglocalgallerylab.config.AwsConfig;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class S3Storage {
    private final AmazonS3 s3;

    public S3Storage() {
        BasicAWSCredentials creds = new BasicAWSCredentials(AwsConfig.ACCESS_KEY, AwsConfig.SECRET_KEY);
        this.s3 = AmazonS3ClientBuilder.standard()
                .withRegion(AwsConfig.REGION)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }

    public void delete(String key) {
        s3.deleteObject(AwsConfig.BUCKET, key);
    }

    public String put(String key, InputStream in, long size, String contentType) {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(size);
        if (contentType != null) meta.setContentType(contentType);
        s3.putObject(AwsConfig.BUCKET, key, in, meta);
        return key;
    }

    public S3Object get(String key) {
        return s3.getObject(AwsConfig.BUCKET, key);
    }

    public List<String> listKeysByPrefixAndExt(String prefix, String... exts) {
        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(AwsConfig.BUCKET)
                .withPrefix(prefix == null ? "" : prefix);
        ListObjectsV2Result res;
        List<String> keys = new ArrayList<>();
        do {
            res = s3.listObjectsV2(req);
            for (S3ObjectSummary s : res.getObjectSummaries()) {
                String k = s.getKey().toLowerCase();
                for (String e : exts) {
                    if (k.endsWith(e.toLowerCase())) {
                        keys.add(s.getKey());
                        break;
                    }
                }
            }
            req.setContinuationToken(res.getNextContinuationToken());
        } while (res.isTruncated());
        Collections.sort(keys);
        return keys;
    }

    public String presignedGetUrl(String key, long millis) {
        Date exp = new Date(System.currentTimeMillis() + millis);
        GeneratePresignedUrlRequest r = new GeneratePresignedUrlRequest(AwsConfig.BUCKET, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(exp);
        URL url = s3.generatePresignedUrl(r);
        return url.toString();
    }
}
