package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

/**
 * Construye la URL JDBC segun el motor de base de datos configurado.
 *
 * <p>El proyecto usa JDBC a pelo, sin JPA, asi que la URL es lo unico que decide que driver se
 * carga. Se soportan dos motores porque conviven dos entornos: MySQL en local (compose.yaml) y
 * PostgreSQL en produccion (Supabase, que es PostgreSQL y no MySQL).
 *
 * <p>Los parametros de SSL no coinciden entre motores: MySQL usa {@code sslMode} y PostgreSQL
 * {@code sslmode}. El valor se pasa tal cual lo declara cada driver.
 */
public record DatabaseConfig(
    String host,
    int port,
    String databaseName,
    String username,
    String password,
    String sslMode,
    String dialect) {

  private static final String MYSQL_URL_TEMPLATE =
      "jdbc:mysql://%s:%d/%s?sslMode=%s&serverTimezone=UTC&allowPublicKeyRetrieval=true";

  private static final String POSTGRES_URL_TEMPLATE =
      "jdbc:postgresql://%s:%d/%s?sslmode=%s";

  /** Motores soportados. */
  public enum Dialect {
    MYSQL,
    POSTGRESQL;

    public static Dialect from(final String rawValue) {
      if (rawValue == null || rawValue.isBlank()) {
        return MYSQL;
      }
      final String normalized = rawValue.trim().toUpperCase().replace("-", "").replace("_", "");
      return switch (normalized) {
        case "POSTGRES", "POSTGRESQL", "PG" -> POSTGRESQL;
        default -> MYSQL;
      };
    }
  }

  public String buildJdbcUrl() {
    return switch (Dialect.from(dialect)) {
      case POSTGRESQL ->
          String.format(POSTGRES_URL_TEMPLATE, host, port, databaseName, sslMode.toLowerCase());
      case MYSQL -> String.format(MYSQL_URL_TEMPLATE, host, port, databaseName, sslMode);
    };
  }
}