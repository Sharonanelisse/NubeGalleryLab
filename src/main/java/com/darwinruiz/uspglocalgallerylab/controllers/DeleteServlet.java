package com.darwinruiz.uspglocalgallerylab.controllers;

import com.darwinruiz.uspglocalgallerylab.repositories.LocalFileRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {
    private LocalFileRepository repo;

    @Override
    public void init() {
        repo = LocalFileRepository.createDefault();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String rel = req.getParameter("path");
        if (rel == null || rel.isBlank() || rel.contains("..")) {
            resp.sendError(400, "path inválido");
            return;
        }

        try {
            repo.delete(rel);
        } catch (IOException e) {
            resp.sendError(500, "Error al borrar: " + e.getMessage());
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
        // TODO-5: llamar repo.delete(rel) y redirigir a la página actual
        resp.sendRedirect(req.getContextPath() + "/list");
    }
}