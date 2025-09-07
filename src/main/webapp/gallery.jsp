<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <title>Galería</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .thumb {
            object-fit: cover;
            height: 180px;
        }
    </style>
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/">USPG Lab</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div id="navMain" class="collapse navbar-collapse show">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/upload.jsp"><i
                        class="bi bi-upload"></i> Subir</a></li>
                <li class="nav-item"><a class="nav-link active" href="#"><i class="bi bi-image"></i> Imágenes</a></li>
            </ul>
        </div>
    </div>
</nav>

<main class="container py-4">
    <h3 class="mb-3">Imágenes en S3</h3>

    <%
        // Obtener lista de imágenes S3
        java.util.List<String> s3 = (java.util.List<String>) request.getAttribute("s3ImageUrls");
        if (s3 == null) {
    %>
    <div class="alert alert-info">Haz clic en “Refrescar” para listar las imágenes.</div>
    <%
        }
    %>

    <div class="row g-3">
        <%
            if (s3 != null && !s3.isEmpty()) {
                for (String url : s3) {
        %>
        <div class="col-6 col-md-3">
            <div class="card shadow-sm">
                <img class="thumb card-img-top" src="<%=url%>" alt="">
                <div class="card-body small text-truncate">
                    <%= url.substring(url.lastIndexOf("/") + 1) %>
                </div>
            </div>
        </div>
        <%
            }
        } else if (s3 != null) {
        %>
        <div class="col-12">
            <div class="alert alert-warning">No hay imágenes en S3.</div>
        </div>
        <%
            }
        %>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
