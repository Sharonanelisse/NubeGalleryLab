package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.dto.UploadResult;
import com.darwinruiz.uspglocalgallerylab.repositories.LocalFileRepository;
import com.darwinruiz.uspglocalgallerylab.services.ImageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 2 * 1024 * 1024, maxFileSize = 5L * 1024 * 1024, maxRequestSize = 30L * 1024 * 1024)
public class UploadServlet extends HttpServlet {
    private ImageService service;

    @Override
    public void init() {
        service = new ImageService(LocalFileRepository.createDefault());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        UploadResult r = service.uploadLocalImages(req.getParts());
        resp.sendRedirect(req.getContextPath() + "/upload.jsp?uploaded=" + r.uploaded + "&rejected=" + r.rejected);
    }
}