package com.darwinruiz.uspglocalgallerylab.util;

import jakarta.servlet.http.Part;

import java.util.Set;

public class ImageValidator {
    public static final long MAX_BYTES = 3L * 1024 * 1024;
    public static final Set<String> ALLOWED_EXT = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp");

    /** TODO-1:
     *  - tamaño <= MAX_BYTES
     *  - contentType empieza con "image/"
     *  - extensión ∈ ALLOWED_EXT (en minúsculas)
     */

    public static boolean isValid(Part part, String fileName) {
        if (part == null || fileName == null) {
            return false;
        }

        // 1. Verificar tamaño
        if (part.getSize() > MAX_BYTES) {
            return false;
        }

        // 2. Verificar contentType
        String contentType = part.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            return false;
        }

        // 3. Verificar extensión
        String lowerName = fileName.toLowerCase();
        boolean hasValidExt = ALLOWED_EXT.stream().anyMatch(lowerName::endsWith);
        if (!hasValidExt) {
            return false;
        }

        return true;
    }
}