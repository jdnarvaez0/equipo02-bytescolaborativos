package com.codebytes2.recommender.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Endpoints de verificación de salud")
public class HealthCheckController {

    @Operation(summary = "Verifica que la API está activa")
    @GetMapping("/health")
    public String health() {
        return "✅ Recommender Engine is running!";
    }
}