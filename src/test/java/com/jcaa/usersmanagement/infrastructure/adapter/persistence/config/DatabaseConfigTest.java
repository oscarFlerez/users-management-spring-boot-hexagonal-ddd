package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

  private static final String HOST = "mysql.example.com";
  private static final int PORT = 15425;
  private static final String DATABASE = "crud_usuarios";
  private static final String USERNAME = "avnadmin";
  private static final String PASSWORD = "secret";
  private static final String SSL_MODE = "REQUIRED";

  @Test
  void shouldBuildJdbcUrlWithConfiguredSslMode() {
    // Arrange
    final DatabaseConfig config =
        new DatabaseConfig(HOST, PORT, DATABASE, USERNAME, PASSWORD, SSL_MODE, "mysql");

    // Act
    final String jdbcUrl = config.buildJdbcUrl();

    // Assert
    assertThat(jdbcUrl)
        .isEqualTo(
            "jdbc:mysql://mysql.example.com:15425/crud_usuarios"
                + "?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true");
  }

  @Test
  void shouldBuildPostgresJdbcUrlForSupabase() {
    // Arrange
    final DatabaseConfig config =
        new DatabaseConfig(
            "db.abc.supabase.co",
            5432,
            "postgres",
            "postgres",
            PASSWORD,
            "require",
            "postgresql");

    // Act
    final String jdbcUrl = config.buildJdbcUrl();

    // Assert: el driver de PostgreSQL exige sslmode en minusculas, a diferencia de MySQL
    assertThat(jdbcUrl)
        .isEqualTo("jdbc:postgresql://db.abc.supabase.co:5432/postgres?sslmode=require");
  }

  @Test
  void shouldDefaultToMysqlWhenDialectIsMissing() {
    // Arrange
    final DatabaseConfig config =
        new DatabaseConfig(HOST, PORT, DATABASE, USERNAME, PASSWORD, SSL_MODE, null);

    // Act & Assert
    assertThat(config.buildJdbcUrl()).startsWith("jdbc:mysql://");
  }

  @Test
  void shouldAcceptAliasesOfPostgres() {
    // Arrange, Act & Assert: los tres nombres se usan en la documentacion de Supabase
    assertThat(DatabaseConfig.Dialect.from("postgres")).isEqualTo(DatabaseConfig.Dialect.POSTGRESQL);
    assertThat(DatabaseConfig.Dialect.from("PG")).isEqualTo(DatabaseConfig.Dialect.POSTGRESQL);
    assertThat(DatabaseConfig.Dialect.from("postgresql")).isEqualTo(DatabaseConfig.Dialect.POSTGRESQL);
    assertThat(DatabaseConfig.Dialect.from("mysql")).isEqualTo(DatabaseConfig.Dialect.MYSQL);
  }
}