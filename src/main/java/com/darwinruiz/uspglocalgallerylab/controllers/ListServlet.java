package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.config.AwsConfig;
import com.darwinruiz.uspglocalgallerylab.storage.S3Storage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/list")
public class ListServlet extends HttpServlet {
    private S3Storage s3;


    @Override
    public void init() throws ServletException {
        Path base = Path.of(System.getProperty("java.io.tmpdir"), "uspgtests");
        s3 = new S3Storage();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String type = req.getParameter("type");
        if (type == null) type = "images";

        if ("images".equals(type)) {

            // S3
            List<String> s3Keys = s3.listKeysByPrefixAndExt("", ".png", ".jpg", ".jpeg", ".gif", ".webp");
            List<String> s3Urls = s3Keys.stream()
                    .map(k -> s3.presignedGetUrl(k, AwsConfig.PRESIGNED_MS))
                    .collect(Collectors.toList());

            req.setAttribute("s3ImageUrls", s3Urls);
            req.getRequestDispatcher("/gallery.jsp").forward(req, resp);
            return;
        }
    }
}
