# Users Management — Spring Boot, Arquitectura Hexagonal y DDD

Aplicación de gestión de usuarios construida con Java 17 y Spring Boot. La API REST es el punto de entrada activo. El código de la antigua CLI se conserva como adaptador inactivo y no posee un contenedor de dependencias independiente.

Spring es el único *composition root*: `Main` inicia el contexto y las dependencias se resuelven mediante configuración y component scanning de Spring.

## Verificación

```bash
./mvnw clean test
./mvnw clean package
```

En Windows se puede utilizar `mvnw.cmd`.

## Entorno local con Docker

El motor se elige por perfil. Cada perfil levanta su base **y** la API conectada a ella:

```bash
docker compose --profile mysql up        # MySQL
docker compose --profile postgres up     # PostgreSQL, el mismo camino que Supabase
```

No hay un `docker compose up` a secas. Son dos servicios de API (`api` y `api-postgres`) en
vez de uno porque `depends_on` no admite servicios con perfil no habilitado: un `api` único
sin perfil haría que el archivo entero fuera rechazado con
`depends on undefined service "mysql"`.

Los esquemas se montan solos en el primer arranque (`schema.sql` y `schema-postgres.sql`).
Ojo: Docker solo los ejecuta cuando el volumen está vacío; si ya hay datos, hay que
ejecutarlos a mano.

## Despliegue en Render

El archivo `render.yaml` define el servicio web, el build con Docker y el despliegue
automático de cada commit que llegue a la rama `main`.

1. Crear un Blueprint en Render y seleccionar este repositorio.
2. Completar en el panel los secretos marcados como requeridos: `DB_HOST`,
   `DB_USERNAME`, `DB_PASSWORD`, `SMTP_USERNAME`, `SMTP_PASSWORD` y
   `SMTP_FROM_ADDRESS`.
3. Usar una instancia **PostgreSQL** accesible desde Internet (Supabase es lo recomendado) y
   ejecutar `src/main/resources/schema-postgres.sql` una vez para crear el esquema. Render no
   ofrece MySQL en su plan gratuito, y `DB_DIALECT=postgresql` es lo que decide el driver.
4. Desplegar el Blueprint. La API quedará disponible en el subdominio
   `onrender.com` asignado por Render y Swagger UI en `/swagger-ui.html`.

Las credenciales nunca deben guardarse en `application.properties` ni en
`render.yaml`. Para desarrollo local, deben proporcionarse como variables de entorno.
