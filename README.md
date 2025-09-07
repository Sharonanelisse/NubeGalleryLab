# NubeGalleryLab
Integrantes:
<pre>
<strong>Sharon Anelisse Marroquín Hernández</strong>
<strong>Eddy Alexander Cheguen García</strong>
<strong>Araceli de los Angeles Asencio y Asencio</strong>
<strong>David Roberto Escobar Mérida</strong>
<strong>Ismael Alejandro Liquez Muñoz</strong>
<strong>Josimar Brandon Andrée Hernández Calzadia</strong>
</pre>

# Seguridad

Para esta parte se tomaron en cuenta dos cosas principales:

- *Credenciales:* No se subieron llaves de AWS al repositorio. La aplicación usa el SDK de AWS (Java v2) que toma los datos desde variables de entorno o desde el perfil local configurado con aws configure.  
  Ejemplo de configuración local (valores de prueba):

<pre>
  AWS_ACCESS_KEY_ID=XXXXXXXX
  AWS_SECRET_ACCESS_KEY=YYYYYYYY
  AWS_REGION=us-east-1
  BUCKET=uspg-equipo-imagenes
</pre>

Además, el archivo .gitignore evita que .env, .aws/ o archivos similares se suban por error.

- *Bucket S3 privado:* El bucket se dejó con *Block Public Access activado, sin ACLs ni políticas públicas. Las imágenes se sirven únicamente a través de **pre-signed URLs* que generan el backend con un tiempo de expiración corto (10 min).  
  De esta manera, si se intenta abrir un archivo directamente sin URL firmada, S3 devuelve *AccessDenied* y solo la aplicación puede mostrarlo.