package com.example.CoreBack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Microservicio CoreBack",
        version = "1.0.0",
        description = "APIs documentadas con Swagger (OpenAPI 3)",
        license = @License(name = "Apache 2.0"),
        contact = @Contact(
            url = "https://tuservicio.com",
            name = "Equipo CoreBack",
            email = "equipo@correo.com"
        )
    ),
    servers = {
        @Server(description = "Ambiente local", url = "http://localhost:8080/"),
        @Server(description = "Ambiente Dev", url = "https://dev.api.com"),
        @Server(description = "Ambiente QA", url = "https://qa.api.com"),
        @Server(description = "Ambiente Prod", url = "https://api.com")
    }
)
@SpringBootApplication
public class CoreBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreBackApplication.class, args);
    }
}