package com.tablehub.thbackend.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Management API")
                        .version("1.0.0")
                        .description("""
        This OpenAPI document describes the **REST (HTTP) endpoints** used in the application.

        ---
        
        ## WebSocket Documentation
        
        This application uses **STOMP over WebSocket** for real-time communication.
        The WebSocket API is *not* covered by OpenAPI specification.
        
        ### Connection Details
        - **Endpoint:** `ws://localhost:8080/ws` (or `wss://` for production)
        - **Protocol:** STOMP
        - **Authentication:** JWT token required (pass in CONNECT frame headers)
        
        ### Available Destinations
        
        #### Subscribe (Server → Client)
        - `/topic/*` - Broadcast messages to all connected clients
        - `/queue/*` - Point-to-point messages
        - `/user/queue/*` - User-specific messages (authenticated)
        
        #### Publish (Client → Server)
        - `/app/*` - Send messages to application handlers
        
        ### Message Prefixes
            - `/topic/*` - Broadcast to all subscribers
            - `/app/*` - Send to server message handlers
            - `/user/queue/*` - Private user messages
        
            **Note:** WebSocket protocols are not covered by OpenAPI spec.\s
            See application source code or contact the development team for detailed STOMP mapping documentation.
        """));
    }
}
