package com.codebytes2.recommender.controller;

import com.codebytes2.recommender.dto.request.ProductCreateRequest;
import com.codebytes2.recommender.dto.request.ProductUpdateRequest;
import com.codebytes2.recommender.dto.response.ProductDetailDto;
import com.codebytes2.recommender.dto.response.ProductSummaryDto;
import com.codebytes2.recommender.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "🛍️ Products", description = "Gestión de productos (solo ADMIN puede crear/actualizar/eliminar)")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Crear un nuevo producto",
            description = "Solo accesible para usuarios con rol **ADMIN**.\n" +
                    "Crea un nuevo producto con la información proporcionada.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de producto",
                                    value = """
                        {
                          "name": "Counter-Strike 2",
                          "description": "Juego de disparos en primera persona",
                          "category": "FPS",
                          "tags": ["shooter", "competitive", "multiplayer"],
                          "popularityScore": 100
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Producto creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDetailDto.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                            {
                              "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                              "name": "Counter-Strike 2",
                              "description": "Juego de disparos en primera persona",
                              "category": "FPS",
                              "tags": ["shooter", "competitive", "multiplayer"],
                              "popularityScore": 100,
                              "createdAt": "2025-05-01T12:00:00Z"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos (campos faltantes)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (usuario no es ADMIN)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetailDto> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductDetailDto created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Obtener detalle de un producto",
            description = "Endpoint público. Devuelve información detallada del producto.",
            parameters = @Parameter(
                    name = "id",
                    description = "ID del producto (UUID)",
                    required = true,
                    example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Producto encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDetailDto.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                            {
                              "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                              "name": "Counter-Strike 2",
                              "description": "Juego de disparos en primera persona",
                              "category": "FPS",
                              "tags": ["shooter", "competitive", "multiplayer"],
                              "popularityScore": 100,
                              "createdAt": "2025-05-01T12:00:00Z"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Producto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getProductDetail(@PathVariable UUID id) {
        ProductDetailDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Listar productos con paginación",
            description = "Endpoint público. Lista todos los productos con paginación y ordenación.",
            parameters = {
                    @Parameter(name = "page", description = "Número de página (0-based)", example = "0", in = ParameterIn.QUERY),
                    @Parameter(name = "size", description = "Elementos por página", example = "10", in = ParameterIn.QUERY),
                    @Parameter(name = "sort", description = "Criterio de ordenación (ej: name,asc)", example = "name,asc", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista paginada de productos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "object",
                                            example = """
                            {
                              "content": [
                                {
                                  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                                  "name": "Counter-Strike 2",
                                  "category": "FPS",
                                  "popularityScore": 100
                                }
                              ],
                              "pageable": { "pageNumber": 0, "pageSize": 20 },
                              "totalElements": 1,
                              "totalPages": 1,
                              "last": true,
                              "first": true
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<ProductSummaryDto>> getAllProducts(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ProductSummaryDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Buscar productos por nombre",
            description = "Endpoint público. Busca productos cuyo nombre contenga el texto proporcionado (búsqueda parcial, no sensible a mayúsculas).",
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Nombre o parte del nombre del producto",
                            required = true,
                            example = "counter-strike"
                    ),
                    @Parameter(name = "page", description = "Número de página", example = "0"),
                    @Parameter(name = "size", description = "Elementos por página", example = "10")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista paginada de productos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "object",
                                            example = """
                            {
                              "content": [
                                {
                                  "id": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
                                  "name": "Counter-Strike 2",
                                  "category": "FPS",
                                  "popularityScore": 100
                                }
                              ],
                              "totalElements": 1
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<Page<ProductSummaryDto>> searchProducts(
            @RequestParam String name,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ProductSummaryDto> products = productService.searchProducts(name, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Actualizar un producto",
            description = "Solo accesible para usuarios con rol **ADMIN**.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(
                    name = "id",
                    description = "ID del producto (UUID)",
                    required = true,
                    example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del producto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de actualización",
                                    value = """
                        {
                          "name": "Counter-Strike 2 Updated",
                          "description": "Juego de disparos mejorado",
                          "category": "FPS",
                          "tags": ["shooter", "competitive", "multiplayer", "tactical"],
                          "popularityScore": 150
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Producto actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDetailDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (no es ADMIN)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Producto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetailDto> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductDetailDto updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar un producto",
            description = "Solo accesible para usuarios con rol **ADMIN**.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(
                    name = "id",
                    description = "ID del producto (UUID)",
                    required = true,
                    example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado (no es ADMIN)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Producto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}