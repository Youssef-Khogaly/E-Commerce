package com.ecommerce.inventory;


import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.docs.CommonErrorDocs;
import com.ecommerce.docs.RequireAuthDocs;
import com.ecommerce.inventory.infra.InventoryMetaDataView;
import com.ecommerce.inventory.infra.ProductAvailableStockView;
import com.ecommerce.inventory.legacy.dtos.StockQuantityRequest;
import com.ecommerce.inventory.legacy.dtos.StockResponseDto;
import com.ecommerce.inventory.legacy.entity.ProductStock;
import com.ecommerce.inventory.responses.InventoryUpdateOperation;
import com.ecommerce.inventory.responses.InventoryUpdateResponse;
import com.ecommerce.inventory.responses.ProductAvailableStockResponse;
import com.ecommerce.inventory.service.InventoryQueryService;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.util.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/inventory")
@RestController
@Validated
@AllArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    private final InventoryQueryService inventoryQueryService;

    @Operation(summary = "retrieve available stock for product" , description = "Public Api")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE , schema = @Schema(implementation = ProductAvailableStockResponse.class))),
            }
    )
    @CommonErrorDocs
    @GetMapping("/{id}")
    ResponseEntity<ProductAvailableStockResponse> getStock(@PathVariable @Positive @NotNull Long id)
    {
        var response = ProductAvailableStockResponse.from(inventoryQueryService.getAvailableStockView(id));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "patch retrieve prodct available stocks" , description = "Public Api")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",useReturnTypeSchema = true),
            }
    )
    @CommonErrorDocs
    @GetMapping("/")
    ResponseEntity<List<ProductAvailableStockResponse>> getStocks(@RequestParam(name = "ids") @NotNull @NotEmpty Set<@Positive Long> ids)
    {
        var response = inventoryQueryService.getAvailableStockView(ids).stream().map(ProductAvailableStockResponse::from).toList();
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "add stocks to product inventory" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = InventoryUpdateResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/{id}/add")
    ResponseEntity<InventoryUpdateResponse>addStock(@RequestBody @Valid @NotNull StockQuantityRequest quantityRequest, @PathVariable @Positive @NotNull Long id)
    {
        if(!quantityRequest.id().equals(id))
            throw new BadRequestException("Bad request, product id in path variable does not match request body");

        inventoryService.add(Map.of(quantityRequest.id(),quantityRequest.quantity()));
        var ret = InventoryUpdateResponse.builder().productId(quantityRequest.id()).operation(InventoryUpdateOperation.ADD).affectedQuantity(quantityRequest.quantity()).build();
        return ResponseEntity.ok(ret);
    }

    @Operation(summary = "remove from product stock" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = InventoryUpdateResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error or negative stock result", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/{id}/remove")
    ResponseEntity<InventoryUpdateResponse>removeStock(@RequestBody @Valid @NotNull StockQuantityRequest quantityRequest, @PathVariable @Positive @NotNull Long id)
    {
        if(!quantityRequest.id().equals(id))
            throw new BadRequestException("Bad request, product id in path variable does not match request body");

        inventoryService.remove(Map.of(quantityRequest.id(),quantityRequest.quantity()));
        var ret = InventoryUpdateResponse.builder().productId(quantityRequest.id()).operation(InventoryUpdateOperation.REMOVE).affectedQuantity(quantityRequest.quantity()).build();
        return ResponseEntity.ok(ret);
    }

    @Operation(summary = "patch add to stock" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = InventoryUpdateResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/add")
    ResponseEntity<List<InventoryUpdateResponse>>addStocks(@RequestBody @NotEmpty @Valid Set<StockQuantityRequest> stockQuantityReqSet)
    {
        var idQuantityMap = stockQuantityReqSet.stream().collect(Collectors.toMap(StockQuantityRequest::id, StockQuantityRequest::quantity));
        inventoryService.add(idQuantityMap);
        var responses = stockQuantityReqSet.stream().map(a -> {
            return InventoryUpdateResponse.builder().productId(a.id()).operation(InventoryUpdateOperation.ADD).affectedQuantity(a.quantity()).build();
        }).toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "patch remove from stock" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = InventoryUpdateResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error or negative stock result", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/remove")
    ResponseEntity<List<InventoryUpdateResponse>>removeStocks(@RequestBody @NotEmpty @Valid Set<StockQuantityRequest> stockQuantityReqSet)
    {
        var idQuantityMap = stockQuantityReqSet.stream().collect(Collectors.toMap(StockQuantityRequest::id, StockQuantityRequest::quantity));
        inventoryService.add(idQuantityMap);
        var responses = stockQuantityReqSet.stream().map(a -> {
            return InventoryUpdateResponse.builder().productId(a.id()).operation(InventoryUpdateOperation.REMOVE).affectedQuantity(a.quantity()).build();
        }).toList();
        return ResponseEntity.ok(responses);
    }
}
