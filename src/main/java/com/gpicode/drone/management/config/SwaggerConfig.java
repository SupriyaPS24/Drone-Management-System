package com.gpicode.drone.management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Drone Movement API",
                version = "1.0.0",
                description = "API for Registering, controlling and monitoring drone movements"
        ),
        servers = {
                @Server(
                        url = "host-url",
                        description = "Host server - (Example)"
                )
        }
)
public class SwaggerConfig {
}
