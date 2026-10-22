package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.controller;

import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateUserRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateUserRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.ApiErrorResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.UserRestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/** Contrato OpenAPI / Swagger de los endpoints de usuarios. */
@Tag(name = "Users", description = "Gestión de usuarios: crear, consultar, actualizar y eliminar.")
public interface UserRestControllerDocs {

  // POST /api/users

  @Operation(
      summary = "Crear usuario",
      description =
          "Registra un nuevo usuario en el sistema. "
              + "El ID debe ser único y el correo no puede estar en uso. "
              + "Se enviará un correo de bienvenida con las credenciales al registrarlo. "
              + "El estado inicial es PENDING y un administrador debe activarlo con PUT. "
              + "Abierto solo mientras la tabla de usuarios esté vacía, que es como se crea "
              + "el primer administrador; a partir de ahí exige un JWT con rol ADMIN. "
              + "Esto evita que cualquiera pueda crear filas o disparar envíos de correo.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Usuario creado exitosamente.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRestResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Datos de entrada inválidos (campos requeridos vacíos o formato incorrecto).",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe un usuario registrado con ese correo electrónico.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Sesión requerida: la tabla de usuarios ya no está vacía.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Rol insuficiente: solo un ADMIN puede crear usuarios.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  UserRestResponse create(@Valid @RequestBody CreateUserRestRequest request);

  // GET /api/users

  @Operation(
      summary = "Listar todos los usuarios",
      description = "Retorna la lista completa de usuarios ordenada alfabéticamente por nombre.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lista de usuarios obtenida exitosamente. Puede ser vacía.",
        content =
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = UserRestResponse.class)))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  List<UserRestResponse> getAll();

  // GET /api/users/{id}

  @Operation(
      summary = "Obtener usuario por ID",
      description = "Retorna los datos de un usuario específico identificado por su ID único.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Usuario encontrado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRestResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún usuario con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  UserRestResponse getById(
      @Parameter(description = "ID único del usuario.", example = "usr-001", required = true)
          @PathVariable
          String id);

  // PUT /api/users/{id}

  @Operation(
      summary = "Actualizar usuario",
      description =
          "Actualiza los datos de un usuario existente. "
              + "Si se cambia el correo, este no debe estar en uso por otro usuario. "
              + "Se enviará un correo de notificación de actualización.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Usuario actualizado exitosamente.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRestResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Datos de entrada inválidos.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún usuario con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "El nuevo correo ya está en uso por otro usuario.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  UserRestResponse update(
      @Parameter(description = "ID único del usuario a actualizar.", example = "usr-001", required = true)
          @PathVariable
          String id,
      @Valid @RequestBody UpdateUserRestRequest request);

  // DELETE /api/users/{id}

  @Operation(
      summary = "Eliminar usuario",
      description = "Elimina permanentemente un usuario del sistema por su ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente."),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún usuario con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  void delete(
      @Parameter(description = "ID único del usuario a eliminar.", example = "usr-001", required = true)
          @PathVariable
          String id);
}

