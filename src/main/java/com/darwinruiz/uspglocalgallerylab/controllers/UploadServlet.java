package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.storage.S3Storage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

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

    private S3Storage s3;

    @Override
    public void init() throws ServletException {
        Path base = Path.of(System.getProperty("java.io.tmpdir"), "uspgtests");
        s3 = new S3Storage();
    }

    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        Part part = req.getPart("file");
        if (part == null || part.getSize() == 0) {
            resp.sendError(400, "Archivo vacío");
            return;
        }

        String target = req.getParameter("target"); // "local" | "s3"
        if (target == null) target = "local";

        // filename limpio (evita rutas tipo C:\fakepath\foto.png)
        String submitted = part.getSubmittedFileName();
        String fileName = java.nio.file.Paths.get(submitted).getFileName().toString();
        String contentType = part.getContentType();

        String prefix = req.getParameter("prefix"); // ej: "imagenes" o "pdfs"
        if (prefix == null) prefix = "";

        if ("s3".equalsIgnoreCase(target)) {
            String s3Prefix = prefix.isBlank() ? "" : (prefix.endsWith("/") ? prefix : prefix + "/");
            String key = s3Prefix + java.util.UUID.randomUUID() + "_" + fileName;
            try (InputStream in = part.getInputStream()) {
                s3.put(key, in, part.getSize(), contentType); // en S3 no hay que crear carpetas
            }
            resp.sendRedirect(req.getContextPath() + "/list?uploaded=1&rejected=0");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/list?type=images&uploaded=1&rejected=0");
    }
}
