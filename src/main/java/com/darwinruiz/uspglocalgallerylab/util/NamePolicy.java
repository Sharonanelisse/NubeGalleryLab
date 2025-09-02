package com.darwinruiz.uspglocalgallerylab.util;
import java.nio.file.Paths;

public class NamePolicy {
    /**
     * TODO-2:
     *  normalizar nombre:
     *   - tomar solo nombre base (sin ruta)
     *   - pasar a minúsculas
     *   - reemplazar espacios por guiones
     *   - remover caracteres no [a-z0-9._-]
     *   - limitar a 80 caracteres
     */

    public static String normalize(String original) {
        if (original == null || original.isBlank()) {
            return "unnamed";
        }

        String baseName = Paths.get(original).getFileName().toString();

        baseName = baseName.toLowerCase();

        baseName = baseName.replace(" ", "-");

        baseName = baseName.replaceAll("[^a-z0-9._-]", "");

        int maxLen = 80;
        if (baseName.length() > maxLen) {
            int dotIndex = baseName.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < baseName.length() - 1) {
                String namePart = baseName.substring(0, dotIndex);
                String ext = baseName.substring(dotIndex);
                int allowedNameLen = maxLen - ext.length();
                if (allowedNameLen < 1) {
                    return baseName.substring(0, maxLen);
                }
                baseName = namePart.substring(0, Math.min(namePart.length(), allowedNameLen)) + ext;
            } else {
                baseName = baseName.substring(0, maxLen);
            }
        }

        return baseName;
    }

    /** subcarpeta por fecha: "imagenes/yyyy/MM/dd" */
    public static String datedSubdir(java.time.LocalDate d) {
        return String.format("imagenes/%04d/%02d/%02d", d.getYear(), d.getMonthValue(), d.getDayOfMonth());
    }
}