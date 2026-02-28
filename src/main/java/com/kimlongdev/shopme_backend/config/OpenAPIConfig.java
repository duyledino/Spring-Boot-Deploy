package com.kimlongdev.shopme_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    // 1. Cấu hình nút "Authorize" nhập JWT
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    // 2. Cấu hình Server (Dev & Prod)
    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
    }

    private Info createApiInfo() {
        return new Info()
                .title("ShopMe E-commerce API")
                .version("2.0")
                .description("Tài liệu API cho hệ thống thương mại điện tử ShopMe 2.0.")
                .license(createLicense());
    }

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(List.of(
                        createServer("http://localhost:8080", "Server Local (DEV)"),
                        createServer("https://api.shopme.com", "Server Production (PROD)")
                ))
                // Kích hoạt bảo mật JWT cho toàn bộ API
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}
