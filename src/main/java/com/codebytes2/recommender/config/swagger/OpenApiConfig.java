package com.codebytes2.recommender.config.swagger;

import com.codebytes2.recommender.backend.TournamentStatus;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Iterator;
import java.util.UUID;

@Configuration
public class OpenApiConfig {

    static {

        ModelConverters.getInstance().addConverter(new io.swagger.v3.core.jackson.ModelResolver(
                new com.fasterxml.jackson.databind.ObjectMapper()
        ) {
            @Override
            public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
                if (type.getType() == UUID.class) {
                    return new Schema<>().type("string").format("uuid");
                }
                if (type.getType() == Instant.class) {
                    return new Schema<>().type("string").format("date-time");
                }
                return super.resolve(type, context, chain);
            }
        });
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Recommender Engine API")
                        .version("1.0.0")
                        .description("Backend para torneos y recomendaciones")
                        .contact(new Contact().name("CodeBytes Team").email("dev@codebytes2.com"))
                        .license(new License().name("MIT"))
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                        // ✅ Registrar enum explícitamente
                        .addSchemas("TournamentStatus",
                                new Schema<>()
                                        .type("string")
                                        .description("Estado del torneo")
                                        ._enum(java.util.Arrays.asList(
                                                TournamentStatus.UPCOMING.getValue(),
                                                TournamentStatus.OPEN.getValue(),
                                                TournamentStatus.CLOSED.getValue()
                                        ))
                        )
                );
    }
}