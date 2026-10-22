package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests para UserAlreadyExistsException. */
@DisplayName("UserAlreadyExistsException")
class UserAlreadyExistsExceptionTest {

  @Test
  @DisplayName("becauseEmailAlreadyExists() debe incluir el email en el mensaje de error")
  void shouldIncludeEmailInMessage() {
    // Arrange
    final String email = "existing@example.com";

    // Act
    final String message = UserAlreadyExistsException.becauseEmailAlreadyExists(email).getMessage();

    // Assert
    assertTrue(message.contains(email), "el mensaje debe identificar el email duplicado");
  }
}
