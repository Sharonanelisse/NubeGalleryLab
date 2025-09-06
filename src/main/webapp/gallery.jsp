<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <title>Galería (Local + S3)</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .thumb { object-fit: cover; height: 180px }
        .pill { padding: .1rem .5rem; border-radius: 999px; font-size: .75rem; background: #eef6ff; color:#1b64d8; }
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
                <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/upload.jsp">
                    <i class="bi bi-upload"></i> Subir</a></li>
                <li class="nav-item"><a class="nav-link active" href="#"><i class="bi bi-image"></i> Imágenes</a></li>
            </ul>
        </div>
    </div>
</nav>

<main class="container py-4">

    <%
        java.util.List<String> local = (java.util.List<String>) request.getAttribute("localImages");
        java.util.List<String> s3Urls = (java.util.List<String>) request.getAttribute("s3ImageUrls");

        Integer pageNum = (Integer) request.getAttribute("page");
        Integer pageSize = (Integer) request.getAttribute("size");
        Integer total = (Integer) request.getAttribute("total");
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 12;
        if (total == null) total = (local == null ? 0 : local.size());
        int totalPages = (int) Math.ceil(total / (double) pageSize);
        if (totalPages == 0) totalPages = 1;
        int prev = Math.max(1, pageNum - 1);
        int next = Math.min(totalPages, pageNum + 1);
    %>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="m-0">Galería <small class="text-muted fs-6">(página <%=pageNum%>/<%=totalPages%>)</small></h3>
        <div class="btn-group">
            <a class="btn btn-sm btn-outline-secondary"
               href="<%=request.getContextPath()%>/list?page=<%=prev%>&size=<%=pageSize%>">« Anterior</a>
            <a class="btn btn-sm btn-outline-secondary"
               href="<%=request.getContextPath()%>/list?page=<%=next%>&size=<%=pageSize%>">Siguiente »</a>
        </div>
    </div>

    <!-- BLOQUE S3 -->
    <h5 class="mb-2">Imágenes S3 <span class="badge bg-light text-primary border ms-1">Pre-signed</span></h5>
    <% if (s3Urls == null || s3Urls.isEmpty()) { %>
    <div class="alert alert-info">No hay imágenes en S3 todavía.</div>
    <% } else { %>
    <div class="row g-3 mb-4">
        <%
            for (String url : s3Urls) {
                String name = url;
                try {
                    int slash = url.lastIndexOf('/');
                    if (slash >= 0) name = url.substring(slash + 1);
                    int q = name.indexOf('?');
                    if (q >= 0) name = name.substring(0, q);
                    name = java.net.URLDecoder.decode(name, "UTF-8");
                } catch (Exception e) {
                    name = "imagen";
                }
        %>
        <div class="col-6 col-md-3">
            <div class="card shadow-sm">
                <a href="<%=url%>" target="_blank" rel="noopener noreferrer">
                    <img class="thumb card-img-top"
                         src="<%=url%>"
                         alt="<%=name%>"
                         loading="lazy"
                         referrerpolicy="no-referrer"
                         decoding="async">
                </a>
                <div class="card-body small text-truncate d-flex justify-content-between align-items-center">
                    <span title="<%=name%>"><%=name%></span>
                    <span class="pill">S3</span>
                </div>
                <div class="card-footer bg-white">
                    <a class="btn btn-sm btn-outline-secondary" href="<%=url%>" target="_blank" rel="noopener noreferrer">
                        <i class="bi bi-box-arrow-up-right"></i> Ver
                    </a>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

    <hr class="my-4"/>

    <!-- BLOQUE LOCAL -->
    <h5 class="mb-2">Imágenes locales</h5>
    <% if (local == null) { %>
    <div class="alert alert-info">
        Abre esta página desde <code><%=request.getContextPath()%>/list</code> para cargar el contenido.
    </div>
    <% } else if (local.isEmpty()) { %>
    <div class="alert alert-warning">No hay imágenes locales. Sube algunas desde la pestaña <strong>Subir</strong>.</div>
    <% } else { %>
    <div class="row g-3">
        <% for (String path : local) { %>
        <div class="col-6 col-md-3">
            <div class="card shadow-sm">
                <img class="thumb card-img-top"
                     src="<%=request.getContextPath()%>/view?path=<%=java.net.URLEncoder.encode(path, "UTF-8")%>"
                     alt="Imagen">
                <div class="card-body small text-truncate"><%=path%></div>
                <div class="card-footer bg-white">
                    <form method="post" action="<%=request.getContextPath()%>/delete" class="d-inline">
                        <input type="hidden" name="path" value="<%=path%>">
                        <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> Eliminar</button>
                    </form>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>