package com.jcaa.usersmanagement.infrastructure.security;

import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/** POST /api/users: abierto solo con la tabla vacia (arranque); despues, solo ADMIN. */
@Component
@RequiredArgsConstructor
public class BootstrapCreateUserAuthorization
    implements AuthorizationManager<RequestAuthorizationContext> {

  private static final String ROLE_ADMIN = "ROLE_ADMIN";

  private final GetAllUsersPort getAllUsersPort;

  @Override
  public AuthorizationDecision check(
      final Supplier<Authentication> authentication,
      final RequestAuthorizationContext context) {
    if (getAllUsersPort.getAll().isEmpty()) {
      return new AuthorizationDecision(true);
    }
    return new AuthorizationDecision(isAdmin(authentication.get()));
  }

  private boolean isAdmin(final Authentication authentication) {
    if (authentication == null) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> ROLE_ADMIN.equals(authority.getAuthority()));
  }
}
