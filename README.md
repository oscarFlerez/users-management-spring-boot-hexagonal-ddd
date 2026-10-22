# Users Management — Spring Boot, Arquitectura Hexagonal y DDD

Aplicación de gestión de usuarios construida con Java 17 y Spring Boot. La API REST es el punto de entrada activo. El código de la antigua CLI se conserva como adaptador inactivo.

Spring es el único *composition root*: `Main` inicia el contexto y las dependencias se resuelven mediante configuración y component scanning de Spring.

## Verificación

```bash
./mvnw clean test
./mvnw clean package
```

En Windows se puede utilizar `mvnw.cmd`.

## Estructura

| Paquete | Contenido |
|---|---|
| `domain/` | Modelo puro: `UserModel`, value objects (`UserId`, `UserEmail`, `UserPassword`), enums, eventos y excepciones. Sin Spring. |
| `application/` | Casos de uso y puertos de entrada y salida (`LoginService`, `CreateUserService`...). |
| `infrastructure/` | Adaptadores: `entrypoint/rest` para la API, `adapter/persistence` con JPA, `adapter/email` con Brevo y `entrypoint/desktop` con la CLI. `security/` contiene JWT y las reglas de acceso. |

## API

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/api/auth/login` | Público. Devuelve `accessToken` con 900 s de vida |
| `POST` | `/api/users` | Público solo con la tabla vacía. Después, `ROLE_ADMIN` |
| `GET` | `/api/users` | `ROLE_ADMIN` o `ROLE_REVIEWER` |
| `GET` | `/api/users/{id}` | `ROLE_ADMIN` o `ROLE_REVIEWER` |
| `PUT` | `/api/users/{id}` | `ROLE_ADMIN` |
| `DELETE` | `/api/users/{id}` | `ROLE_ADMIN` |
| `GET` | `/swagger-ui.html`, `/v3/api-docs/**` | Público |

`REVIEWER` es de solo lectura: lista y consulta, pero no crea, edita ni borra. Los roles no se cambian desde fuera porque el `PUT` exige `ROLE_ADMIN`.

## Control de acceso al registro

El registro era el único punto abierto: permitía meter filas sin identificarse y enviar correos de bienvenida a direcciones arbitrarias.

`BootstrapCreateUserAuthorization` deja `POST /api/users` sin autenticación **solo mientras la tabla `users` está vacía**, que es como se crea el primer `ADMIN`. En cuanto hay un usuario, la petición exige un `ROLE_ADMIN`.

Los usuarios nuevos nacen en estado `PENDING` y no pueden entrar hasta que un `ADMIN` los pase a `ACTIVE`.

## Ejecución local

Copiar `application-secrets.properties.example` como `.environments/application-secrets.properties`, rellenar los valores y arrancar:

```bash
./mvnw spring-boot:run
```

Ese archivo guarda la conexión a MySQL, la semilla JWT y las credenciales de Brevo. La carpeta `.environments` está excluida de Git.

## Notificaciones por correo

`notifyUserCreated` y `notifyUserUpdated` envían el correo con `POST /v3/smtp/email` de Brevo, fuera del hilo HTTP y con reintentos y circuit breaker. Requiere `BREVO_API_KEY` y `BREVO_FROM_ADDRESS`, y el remitente debe estar verificado en Brevo.
