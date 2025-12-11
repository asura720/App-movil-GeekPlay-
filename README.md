🎮 Proyecto Final: App Móvil GeekPlay - Backend de Microservicios

Información General

Nombre de la Aplicación: GeekPlay (App Móvil Comunitaria)

Integrantes del Proyecto:

Gerardo Soto

Ricardo Díaz

Tecnologías del Backend: Java 17, Spring Boot (3.2.x), Spring Data JPA, Maven.

Arquitectura: Microservicios con Base de Datos Única (MySQL/MariaDB).

🚀 Funcionalidades Migradas a Microservicios

El proyecto migra toda la lógica de persistencia (anteriormente en Room/SQLite) a una arquitectura de 5 microservicios RESTful, asegurando una separación clara de responsabilidades y escalabilidad.

Módulo

Base de Endpoint

Responsabilidad

Auth-Service

http://localhost:8081/api/auth

Gestión de Usuarios, Registro, Login (BCrypt para hasheo).

Content-Service

http://localhost:8082/api/posts

CRUD de Publicaciones (títulos, resúmenes, contenido).

Interaction-Service

http://localhost:8083/api/interactions

Lógica social: Comentarios y Likes.

Moderation-Service

http://localhost:8084/api/moderation

Gestión de la lógica de isAdmin, Notificaciones de Baneo.

Image-Service

http://localhost:8085/api/images

Gestión de BLOBs: Almacenamiento y recuperación de imágenes (perfil/post) directamente en la base de datos.

🌐 Endpoints Usados (Propios)

Aquí se detallan los endpoints clave para cada servicio:

1. Auth-Service (Usuarios y Autenticación)

Método

Endpoint

Descripción

DTO/Payload

POST

/api/auth/register

Crea un nuevo usuario.

RegisterRequest

POST

/api/auth/login

Autentica al usuario y devuelve el token (futuro JWT).

LoginRequest

PUT

/api/auth/profile/{id}

Actualiza el nombre/teléfono/contraseña del usuario.

ProfileUpdateRequest

GET

/api/auth/profile/{email}

Obtiene los datos del usuario por email.

N/A

2. Content-Service (Publicaciones)

Método

Endpoint

Descripción

GET

/api/posts

Obtiene todas las publicaciones con detalles del autor y conteo de likes.

GET

/api/posts/category/{name}

Filtra publicaciones por categoría.

GET

/api/posts/author/{email}

Filtra publicaciones por email del autor.

GET

/api/posts/search

Búsqueda por título, resumen o contenido (?q=...).

POST

/api/posts/create

Crea una nueva publicación (requiere authorId e imageId).

DELETE

/api/posts/{id}

Elimina una publicación por ID.

3. Image-Service (BLOBs)

Método

Endpoint

Descripción

POST

/api/images/upload

Sube un archivo MultipartFile y devuelve el imageId (BIGINT).

GET

/api/images/{id}

Descarga la imagen BLOB por su ID para que la aplicación móvil la muestre.

⚙️ Instrucciones para Ejecutar el Proyecto (Backend)

Para levantar la arquitectura de microservicios, sigue estos pasos:

1. Requisitos Previos

Java Development Kit (JDK): Versión 17 o superior.

Maven: Instalado y configurado en el PATH.

Laragon/MySQL: Un servidor de base de datos MySQL/MariaDB funcionando (Puerto por defecto 3306).

2. Configuración de la Base de Datos

Crea una base de datos llamada geekplay_db en tu servidor MySQL (puedes usar la interfaz de Laragon, HeidiSQL, o la terminal).

Ejecuta el script SQL (schema_blob.sql) que define las tablas normalizadas (users, posts, images, comments, likes, ban_notifications).

3. Configuración de Spring Boot (Para cada Microservicio)

Para cada servicio (auth-service, content-service, etc.):

Abre el archivo src/main/resources/application.properties.

Verifica y ajusta las credenciales de la base de datos si son diferentes a las predeterminadas (spring.datasource.username=root, spring.datasource.password=admin).

Asegúrate de que el puerto del servidor (server.port) no esté en uso.

4. Ejecutar el Proyecto (Vía VS Code o Terminal)

Vía VS Code:

Abre cada carpeta de microservicio por separado en Visual Studio Code.

Abre el archivo *ServiceApplication.java (ej. AuthServiceApplication.java).

Haz clic en el botón "Run" o presiona F5 (si tienes las extensiones de Java instaladas) para iniciar el servidor.

Vía Terminal (Maven):

Navega a la carpeta raíz de cada microservicio (ej., cd auth-service).

Ejecuta el comando: mvn clean install (para construir el proyecto).

Ejecuta el comando: mvn spring-boot:run (para iniciar el servidor).

Una vez que todos los microservicios estén corriendo (en puertos 8081, 8082, 8083, 8084, 8085), el backend estará listo para recibir peticiones de la aplicación móvil.
