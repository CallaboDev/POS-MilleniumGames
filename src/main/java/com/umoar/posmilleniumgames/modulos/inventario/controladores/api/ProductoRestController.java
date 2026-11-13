package com.umoar.posmilleniumgames.modulos.inventario.controladores.api;

import com.umoar.posmilleniumgames.modulos.inventario.dto.ProductoRequestDTO;
import com.umoar.posmilleniumgames.modulos.inventario.dto.ProductoResponseDTO;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Categoria;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Plataforma;
import com.umoar.posmilleniumgames.modulos.inventario.modelos.Producto;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.CategoriaRepository;
import com.umoar.posmilleniumgames.modulos.inventario.repositorios.PlataformaRepository;
import com.umoar.posmilleniumgames.modulos.inventario.servicios.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "API REST para consulta y gestión del catálogo de videojuegos, consolas y accesorios.")
public class ProductoRestController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;
    private final PlataformaRepository plataformaRepository;

    public ProductoRestController(ProductoService productoService,
                                  CategoriaRepository categoriaRepository,
                                  PlataformaRepository plataformaRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
        this.plataformaRepository = plataformaRepository;
    }

    @GetMapping
    @Operation(
            summary = "Listar catálogo de productos",
            description = "Retorna todos los productos del inventario. Admite un filtro opcional por coincidencia de texto en el nombre."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de productos recuperado exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoResponseDTO.class)))
            )
    })
    public ResponseEntity<List<ProductoResponseDTO>> listar(
            @Parameter(description = "Texto opcional para buscar por título del videojuego o accesorio", example = "Zelda")
            @RequestParam(value = "buscar", required = false) String buscar) {

        List<Producto> productos = (buscar != null && !buscar.isBlank())
                ? productoService.buscarPorNombre(buscar)
                : productoService.obtenerTodos();

        List<ProductoResponseDTO> resultado = productos.stream()
                .map(ProductoResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener producto por ID",
            description = "Consulta la información detallada de un producto específico mediante su identificador numérico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado con el ID indicado",
                    content = @Content
            )
    })
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(
            @Parameter(description = "Identificador único del producto en base de datos", example = "1", required = true)
            @PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(ProductoResponseDTO.fromEntity(producto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/sku/{sku}")
    @Operation(
            summary = "Buscar producto por SKU / Código de barras",
            description = "Endpoint útil para la pistola lectora de código de barras en la caja del POS."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado para el código de barras proporcionado",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún artículo registrado con ese SKU",
                    content = @Content
            )
    })
    public ResponseEntity<ProductoResponseDTO> obtenerPorSku(
            @Parameter(description = "Código de barras o SKU del artículo", example = "PS5-GOW-RAGNAROK", required = true)
            @PathVariable String sku) {
        Producto producto = productoService.buscarPorSku(sku);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(ProductoResponseDTO.fromEntity(producto));
    }

    @PostMapping
    @Operation(
            summary = "Registrar nuevo producto",
            description = "Crea un nuevo videojuego, consola o accesorio en el inventario asociándolo opcionalmente a su categoría y plataforma."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado satisfactoriamente",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (ej. precio negativo, SKU duplicado)",
                    content = @Content
            )
    })
    public ResponseEntity<ProductoResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cuerpo con los datos del nuevo producto a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductoRequestDTO.class))
            )
            @RequestBody ProductoRequestDTO dto) {

        if (dto.getNombre() == null || dto.getNombre().isBlank() || dto.getSku() == null || dto.getSku().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Producto producto = new Producto();
        producto.setSku(dto.getSku().trim());
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock() != null ? dto.getStock() : 0);

        if (dto.getCategoriaId() != null) {
            categoriaRepository.findById(dto.getCategoriaId()).ifPresent(producto::setCategoria);
        }
        if (dto.getPlataformaId() != null) {
            plataformaRepository.findById(dto.getPlataformaId()).ifPresent(producto::setPlataforma);
        }

        try {
            Producto guardado = productoService.guardar(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ProductoResponseDTO.fromEntity(guardado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina permanentemente un producto del catálogo por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        try {
            productoService.obtenerPorId(id);
            productoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
