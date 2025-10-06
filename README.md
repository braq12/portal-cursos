# portal-cursos
Portal de cursos 
* Resumen de Funcionalidades del Portal de Cursos

El Portal de Cursos es una plataforma web integral para la gestión, creación y seguimiento de cursos y capacitaciones.
Su objetivo es facilitar el aprendizaje digital y la entrega automática de insignias electrónicas al completar cursos.

* Gestión de Usuarios

Registro y administración de usuarios con roles Administrador y Usuario.

Autenticación mediante JWT (JSON Web Tokens).

Visualización de usuarios en tabla dinámica con opción para crear, editar y eliminar.

Envío automático de notificaciones por correo electrónico al completar cursos.

* Panel de Administración

Creación de cursos con:

Título, descripción y categoría.

Carga de imagen de insignia personalizada (guardada en Amazon S3).

Gestión de capacitaciones:

Asociación de materiales (videos o documentos PDF) a cada curso.

Carga de archivos mediante multipart/form-data directamente a S3.

Reporte visual de progreso:

Gráficos en tiempo real (Chart.js) mostrando cursos pendientes, en progreso y completados por los usuarios.

Gestión de usuarios:

Creación de cuentas y asignación de roles desde la interfaz administrativa.

* Portal de Usuario

Vista personalizada con los cursos disponibles.

Acciones según estado:

Iniciar curso, continuar o ver capacitaciones asociadas.

Modal emergente con detalle de las capacitaciones del curso.

Reproducción integrada de contenido:

Visualización de videos o documentos dentro de un card interactivo.

Temporizador de visualización y opción para finalizar capacitación.

Al completar todas las capacitaciones:

El curso cambia automáticamente a “Completado”.

Se emite una insignia digital visible desde la tabla del usuario.

* Insignias Digitales

Generación automática al finalizar un curso completo.

Imagen almacenada en S3 y vinculada al perfil del usuario.

Opción “Ver Insignia” en la tabla de cursos completados.

Notificación automática por correo con plantilla HTML profesional y enlace al portal.

* Backend — Spring Boot

API REST modular (Java 21, Spring Boot 3, JPA, Hibernate).

Base de datos MySQL con relaciones entre usuarios, cursos, capacitaciones y progreso.

Integración con AWS S3 para almacenamiento de archivos multimedia e insignias.

Envío de correos asíncronos con Spring Mail (Gmail SMTP).

Seguridad con Spring Security + JWT.

* Frontend — Angular + PrimeNG

Interfaz limpia, responsiva y moderna con PrimeNG + PrimeFlex.

Formularios reactivos con validaciones dinámicas.

Componentes reutilizables: tablas, diálogos, cards, toasts y gráficas interactivas.

Despliegue optimizado en AWS S3 como sitio web estático.

* Infraestructura AWS

Elastic Beanstalk: despliegue automatizado del backend (Spring Boot).

Amazon RDS (MySQL): base de datos relacional gestionada.

Amazon S3: hosting del frontend y almacenamiento de archivos multimedia.

Gmail SMTP: envío de notificaciones de finalización de cursos.


                           ┌──────────────────────────┐
                           │      Usuario Final       │
                           │  (Navegador Web / App)   │
                           └────────────┬─────────────┘
                                        │
                          HTTPS (JWT Auth + REST API)
                                        │
                           ┌────────────▼─────────────┐
                           │     Frontend Angular     │
                           │  AWS S3 + CloudFront CDN │
                           └────────────┬─────────────┘
                                        │
                         HTTPS requests (API Gateway/Direct)
                                        │
                           ┌────────────▼─────────────┐
                           │   Backend Spring Boot    │
                           │ Elastic Beanstalk (Java) │
                           └────────────┬─────────────┘
                                        │
               ┌────────────────────────┼─────────────────────────┐
               │                        │                         │
        Amazon RDS (MySQL)       Amazon S3 (Multimedia)      Gmail SMTP (Email)
        Componentes principales

        | Componente        | Tecnología / Servicio            | Descripción                                                                      |
| ----------------- | -------------------------------- | -------------------------------------------------------------------------------- |
| **Frontend**      | Angular 17 (PrimeNG + PrimeFlex) | Interfaz web responsiva para usuarios y administradores.                         |
| **Backend**       | Spring Boot 3 (Java 21)          | API REST con autenticación JWT y servicios de cursos, capacitaciones y usuarios. |
| **Base de Datos** | Amazon RDS (MySQL 8)             | Almacena usuarios, cursos, progreso y registros.                                 |
| **Archivos**      | Amazon S3                        | Almacenamiento de videos, PDFs e insignias.                                      |
| **Correo**        | Gmail SMTP                       | Envío de notificaciones automáticas cuando el usuario completa un curso.         |



Instrucciones de deploy.

Requisitos previos

| Herramienta     | Versión recomendada | Comando de verificación |
| --------------- | ------------------- | ----------------------- |
| **Java**        | 17 o 21 (Corretto)  | `java -version`         |
| **Maven**       | 3.8+                | `mvn -v`                |
| **AWS CLI**     | 2.x                 | `aws --version`         |
| **Angular CLI** | 17+                 | `ng version`            |


Instacion Elastic BeanStalk configurada.

Tener configurado el s3 con opcion de sitio web statatico 

Base de datos mysql

Eejcutar scritps de la ruta 
\portal-cursos\BD

Deploy del Back en aws Elastic BeanStalk
Varirables de entorno a tener en cuenta

| Variable                                    | Descripción                                               | Ejemplo / Valor sugerido                        |
| ------------------------------------------- | --------------------------------------------------------- | ----------------------------------------------- |
| **SERVER_PORT**                             | Puerto en el que se ejecutará el servidor Spring Boot     | `5000`                                          |
| **SPRING_MAIL_password**                    | Contraseña de la cuenta de correo (app password de Gmail) | `abcd1234xyz`                                   |
| **SPRING_MULTIPART_MAX_FILE_SIZE**          | Tamaño máximo permitido para archivos subidos             | `512MB`                                         |
| **SPRING_MULTIPART_MAX_REQUEST_SIZE**       | Tamaño máximo total permitido por solicitud               | `512MB`                                         |
| **SPRING_DATASOURCE_URL**                   | URL de conexión JDBC a la base de datos                   | `jdbc:mysql://localhost:3306/portal_cursos`     |
| **SPRING_DATASOURCE_USERNAME**              | Usuario de la base de datos                               | `root`                                          |
| **SPRING_DATASOURCE_PASSWORD**              | Contraseña de la base de datos                            | `123456`                                        |
| **SPRING_DATASOURCE_DRIVER_CLASS_NAME**     | Driver JDBC (opcional, tiene valor por defecto)           | `com.mysql.cj.jdbc.Driver`                      |
| **SPRING_JPA_OPEN_IN_VIEW**                 | Controla el modo “open-in-view” de JPA                    | `false`                                         |
| **SPRING_JPA_HIBERNATE_DDL_AUTO**           | Estrategia de generación del esquema                      | `validate` / `update`                           |
| **SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT** | Dialecto de Hibernate                                     | `org.hibernate.dialect.MySQL8Dialect`           |
| **JWT_SECRET**                              | Clave secreta usada para firmar los tokens JWT            | `clave-super-secreta-123`                       |
| **JWT_EXPIRATIONMINUTES**                   | Minutos de duración del token JWT                         | `120`                                           |
| **AWS_REGION**                              | Región de AWS donde se encuentra el bucket                | `us-east-1`                                     |
| **BUCKET**                                  | Nombre del bucket S3                                      | `portal-cursos-bucket`                          |
| **AWS_S3_PUBLIC_BASE_URL**                  | URL base pública de los archivos en S3                    | `https://portal-cursos-bucket.s3.amazonaws.com` |
| **CORS_ALLOWED_ORIGIN**                     |                                                           |                                                 |
ejecutar en la raiz del micro servicio \portal-cursos\Back\portal-cursos

Compilar y empaquetar para AWS
cd portal-cursos/Back/portal-cursos
mvn clean package -Paws
Esto genera en target/ un archivo:

portal-cursos-0.0.1-SNAPSHOT.zip

⚠️ Importante: sube el .zip, no el .jar.
El ZIP contiene la configuración personalizada de Nginx (client_max_body_size 512M;).
mvn clean package -Paws
Desplegar en Elastic Beanstalk

Inicia sesión en AWS y crea una aplicación en Elastic Beanstalk.

Selecciona:

Plataforma: Corretto 21 running on 64bit Amazon Linux 2023

Sube el archivo .zip generado en /target/.
<img width="1919" height="996" alt="image" src="https://github.com/user-attachments/assets/dac485b6-7ac6-4c37-b099-7a1ae64337bc" />


Espera a que el entorno esté Healthy (verde).

Accede a la URL:

http://portal-cursos-env.eba-xxxxx.us-east-2.elasticbeanstalk.com/
Despliegue Local (Backend)
cd portal-cursos/Back/portal-cursos
mvn clean spring-boot:run
Acceso local:
http://localhost:8085

Despliegue del Frontend en AWS S3

1. Compilar Angular para producción:
cd portal-cursos/Front/portal-cursos-frontend
ng build --configuration=production
2. Subir a S3 (desde la carpeta dist/portal-cursos-frontend/browser):
aws s3 sync . s3://app-portal-cursos --exclude "index.html" --delete --cache-control "public, max-age=31536000"
aws s3 cp index.html s3://app-portal-cursos/index.html --cache-control "no-cache, no-store, must-revalidate" --content-type "text/html"
3. Habilitar el bucket como sitio web estático:
En S3 → Propiedades → “Static website hosting”.
Index document: index.html
4. Accede a tu aplicación:
http://app-portal-cursos.s3-website.us-east-2.amazonaws.com/

Despliegue Local (Frontend)

cd portal-cursos/Front/portal-cursos-frontend
ng serve
Acceder
http://localhost:4200

Demo Administrador:
1. Login
   <img width="1918" height="992" alt="image" src="https://github.com/user-attachments/assets/89929ea2-0ea5-404a-85dc-ff694c154524" />
2. Reportes
   <img width="1919" height="989" alt="image" src="https://github.com/user-attachments/assets/98f27282-7d08-4f42-83e1-8ba23dfea62a" />

3. Cursos
   <img width="1918" height="989" alt="image" src="https://github.com/user-attachments/assets/94bcb793-108a-4abf-b81a-b3eee9a7f379" />

4. Crear Curso
   <img width="1917" height="990" alt="image" src="https://github.com/user-attachments/assets/20c61eb2-9fd2-437a-9b04-c199bdf228fc" />

5. Crear Capacitacion
   <img width="1913" height="988" alt="image" src="https://github.com/user-attachments/assets/a6f47ec7-c364-4e04-9ae9-14d844e94bd0" />

6. Crear Usuarios
   <img width="1915" height="989" alt="image" src="https://github.com/user-attachments/assets/49e8485e-c3cc-4611-b630-81ec1e6bacb4" />
Demo Usuario:
1. Mis cursos
   <img width="1918" height="991" alt="image" src="https://github.com/user-attachments/assets/763c4fcc-1357-4f90-9db1-2bab37fa8a12" />
   <img width="1919" height="993" alt="image" src="https://github.com/user-attachments/assets/764d25a3-573a-4b56-a121-5a29c0a9d90c" />

2. Ver detalle curso
   <img width="1916" height="988" alt="image" src="https://github.com/user-attachments/assets/fd5eac1b-c6d6-40ca-85da-d2375015c0b1" />
3.Ver capcacitacion
  <img width="1919" height="990" alt="image" src="https://github.com/user-attachments/assets/481ba72a-0266-4c8a-80fc-03b61fb42165" />
4.Ver insignia
<img width="1918" height="987" alt="image" src="https://github.com/user-attachments/assets/53483d1e-919e-4b53-bcfd-e1125cf602b7" />
5.Correo de notificacion finalizo curso
<img width="1594" height="789" alt="image" src="https://github.com/user-attachments/assets/80e98a09-ce20-4ce3-bf89-37b11cb63e2f" />

Notificaciones Automáticas

El sistema envía correos automáticos cuando un usuario completa un curso y recibe una insignia digital, usando el servicio Gmail configurado con Spring Mail.

Autor

Proyecto académico y técnico — Portal de Capacitaciones
Desarrollado por Brhayan Roa
Colombia 🇨🇴













