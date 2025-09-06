package com.darwinruiz.uspglocalgallerylab.services;

import com.darwinruiz.uspglocalgallerylab.dto.UploadResult;
import com.darwinruiz.uspglocalgallerylab.repositories.IFileRepository;
import com.darwinruiz.uspglocalgallerylab.util.ImageValidator;
import com.darwinruiz.uspglocalgallerylab.util.NamePolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageService {
    private final IFileRepository repo;

    public ImageService(IFileRepository repo) {
        this.repo = repo;
    }
    /**
     * Implementación del upload de imágenes:
     * - Iterar partes "file"
     * - Normalizar nombre (NamePolicy.normalize)
     * - Validar (ImageValidator.isValid)
     * - Crear subcarpeta con fecha (NamePolicy.datedSubdir)
     * - Guardar en el repositorio
     * - Contar subidos/rechazados
     */

    public void delete(String path) throws IOException {
        repo.delete(path);
    }

    public String save(String subdir, String filename, InputStream data) throws IOException {
        return repo.save(subdir, filename, data);
    }

    public UploadResult uploadLocalImages(Collection<Part> parts) throws IOException, ServletException {
        int ok = 0, bad = 0;
        List<String> save = new ArrayList<>();

        for (Part p : parts) {
            if (!"file".equals(p.getName()) || p.getSize() == 0) continue;

            String submitted = p.getSubmittedFileName();
            String fileName = Paths.get(submitted).getFileName().toString();

            fileName = NamePolicy.normalize(fileName);

            try (InputStream in = p.getInputStream()) {
                if (ImageValidator.isValid(p, fileName)) {
                    String subdir = NamePolicy.datedSubdir(LocalDate.now());

                    String storedPath;
                    try (InputStream saveStream = p.getInputStream()) {
                        storedPath = repo.save(subdir, fileName, saveStream);
                    }

                    ok++;
                    save.add(storedPath);
                } else {
                    bad++;
                }
            }
        }
        return new UploadResult(ok, bad, save);
    }
}