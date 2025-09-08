package com.darwinruiz.uspglocalgallerylab.config;

public class AwsConfig {
    public static final String ACCESS_KEY = "AKIAS2ZLVM3EM2GIC25D";
    public static final String SECRET_KEY = "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";
    public static final String REGION = "us-east-2";
    public static final String BUCKET = "uspgtests";

    // Duración de URLs prefirmadas (ms)
    public static final long PRESIGNED_MS = 5 * 60 * 1000; // 5 min
}
