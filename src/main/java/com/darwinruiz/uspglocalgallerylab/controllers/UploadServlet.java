package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.dto.UploadResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 2 * 1024 * 1024,
        maxFileSize = 3L * 1024 * 1024, // máximo 3MB
        maxRequestSize = 30L * 1024 * 1024
)
public class UploadServlet extends HttpServlet {

    private S3Client s3;
    private String bucketName;

    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp");

    @Override
    public void init() {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("awsconfig.properties")) {

            if (input == null) {
                throw new RuntimeException("No se encontró awsconfig.properties en resources/");
            }

            Properties props = new Properties();
            props.load(input);

            String accessKey = props.getProperty("AWS_ACCESS_KEY_ID");
            String secretKey = props.getProperty("AWS_SECRET_ACCESS_KEY");
            String region = props.getProperty("AWS_REGION");
            bucketName = props.getProperty("BUCKET");

            s3 = S3Client.builder()
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)
                            )
                    )
                    .region(Region.of(region))
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Error cargando configuración AWS", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        int uploaded = 0;
        int rejected = 0;
        List<String> saved = new ArrayList<>();

        for (Part part : req.getParts()) {
            String fileName = part.getSubmittedFileName();
            long size = part.getSize();
            String contentType = part.getContentType();


            if (fileName == null || size == 0) {
                rejected++;
                continue;
            }

            String ext = getExtension(fileName).toLowerCase();
            if (!ALLOWED_EXT.contains(ext) || !contentType.startsWith("image/") || size > 3L * 1024 * 1024) {
                rejected++;
                continue;
            }


            LocalDate now = LocalDate.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String nombreNormalizado = fileName.replaceAll("\\s+", "_");
            String key = "imagenes/" + datePath + "/" + UUID.randomUUID() + "_" + nombreNormalizado;

            try {
                PutObjectRequest putReq = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .contentLength(size)
                        .build();

                s3.putObject(putReq, RequestBody.fromInputStream(part.getInputStream(), size));

                uploaded++;
                saved.add(key);

            } catch (Exception e) {
                e.printStackTrace();
                rejected++;
            }
        }


        UploadResult result = new UploadResult(uploaded, rejected, saved);

        resp.sendRedirect(req.getContextPath() + "/upload.jsp?uploaded=" + result.uploaded + "&rejected=" + result.rejected);
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) return "";
        return fileName.substring(lastDot + 1);
    }
}
