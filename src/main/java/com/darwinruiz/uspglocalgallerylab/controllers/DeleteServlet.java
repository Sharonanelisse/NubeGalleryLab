package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.storage.S3Storage;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {
    private S3Storage storage;

    @Override
    public void init() {
        storage = new S3Storage();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String key = req.getParameter("path"); // aquí viene la key en S3
        if (key == null || key.isBlank() || key.contains("..")) {
            resp.sendError(400, "path inválido");
            return;
        }

        try {
            storage.delete(key);
        } catch (Exception e) {
            resp.sendError(500, "Error al borrar en S3: " + e.getMessage());
            return;
        }

        String page = req.getParameter("page");
        String size = req.getParameter("size");

        StringBuilder redirect = new StringBuilder(req.getContextPath() + "/list");
        if (page != null || size != null) {
            redirect.append("?");
            if (page != null) redirect.append("page=").append(page).append("&");
            if (size != null) redirect.append("size=").append(size);
        }

        resp.sendRedirect(redirect.toString());
    }
}
