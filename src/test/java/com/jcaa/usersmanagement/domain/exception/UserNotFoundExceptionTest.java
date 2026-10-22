package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests para UserNotFoundException. */
@DisplayName("UserNotFoundException")
class UserNotFoundExceptionTest {

  @Test
  @DisplayName("becauseIdWasNotFound() debe incluir el id del usuario en el mensaje de error")
  void shouldIncludeUserIdInMessage() {
    // Arrange
    final String userId = "user-404";

    // Act
    final String message = UserNotFoundException.becauseIdWasNotFound(userId).getMessage();

    // Assert
    assertTrue(
        message.contains(userId), "el mensaje debe identificar el id del usuario no encontrado");
  }
}
