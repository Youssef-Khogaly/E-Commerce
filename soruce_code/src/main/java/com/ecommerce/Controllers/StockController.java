package com.ecommerce.Controllers;

import com.ecommerce.DTO.ErrorResponse;
import com.ecommerce.DTO.Requests.StockQuantityRequest;
import com.ecommerce.DTO.StockResponseDto;
import com.ecommerce.docs.CommonErrorDocs;
import com.ecommerce.docs.RequireAuthDocs;
import com.ecommerce.entities.Products.ProductStock;
import com.ecommerce.services.StockService.IStockService;
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

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stocks")
@Validated
@AllArgsConstructor
public class StockController{

    private final IStockService stockService;

    @Operation(summary = "retrieve available stock" , description = "Public Api")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE , schema = @Schema(implementation = StockResponseDto.class))),
            }
    )
    @CommonErrorDocs
    @GetMapping("/{id}")
    ResponseEntity<StockResponseDto> getStock(@PathVariable @Positive @NotNull Long id)
    {
        ProductStock st = stockService.findByIdReadOnly(id);
        StockResponseDto stockResponse = StockResponseDto.from(st);

        return ResponseEntity.ok(stockResponse);
    }

    @Operation(summary = "patch retrieve available stocks" , description = "Public Api")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",useReturnTypeSchema = true),
            }
    )
    @CommonErrorDocs
    @GetMapping("/")
    ResponseEntity<Set<StockResponseDto>> getStocks(@RequestParam(name = "ids") @NotNull @NotEmpty Set<Long> ids)
    {
        Set<ProductStock> stocks = stockService.findAllByIdReadOnly(ids);
        Set<StockResponseDto> stockResponses = stocks.stream()
                .map(StockResponseDto::from)
                .collect(Collectors.toUnmodifiableSet());

        return ResponseEntity.ok(stockResponses);
    }


    @Operation(summary = "add to stock" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = StockResponseDto.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/{id}/add")
    ResponseEntity<StockResponseDto>addStock(@RequestBody @Valid @NotNull StockQuantityRequest quantityRequest, @PathVariable @Positive @NotNull Long id)
    {
        var stock = stockService.add(id,quantityRequest.quantity());

        return ResponseEntity.ok(StockResponseDto.from(stock));
    }

    @Operation(summary = "remove from stock" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = StockResponseDto.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error or negative stock result", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/{id}/remove")
    ResponseEntity<StockResponseDto>removeStock(@RequestBody @Valid @NotNull StockQuantityRequest quantityRequest, @PathVariable @Positive @NotNull Long id)
    {
        var stock = stockService.remove(id,quantityRequest.quantity());

        return ResponseEntity.ok(StockResponseDto.from(stock));
    }

    @Operation(summary = "patch add to stock" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = StockResponseDto.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/add")
    ResponseEntity<Set<StockResponseDto>>addStocks(@RequestBody @NotEmpty @Valid Set<StockQuantityRequest> stockQuantityReqSet)
    {
        var idQuantityMap = stockQuantityReqSet.stream().collect(Collectors.toMap(StockQuantityRequest::id, StockQuantityRequest::quantity));
        var stocks = stockService.add(idQuantityMap);
        var responses = stocks.stream().map(StockResponseDto::from).collect(Collectors.toUnmodifiableSet());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "patch remove from stock" , description = "Required Role: Admin")
    @RequireAuthDocs
    @CommonErrorDocs
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = StockResponseDto.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Concurrency error or negative stock result", content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/remove")
    ResponseEntity<Set<StockResponseDto>>removeStocks(@RequestBody @NotEmpty @Valid Set<StockQuantityRequest> stockQuantityReqSet)
    {
        var idQuantityMap = stockQuantityReqSet.stream().collect(Collectors.toMap(StockQuantityRequest::id, StockQuantityRequest::quantity));
        var stocks = stockService.remove(idQuantityMap);
        var responses = stocks.stream().map(StockResponseDto::from).collect(Collectors.toUnmodifiableSet());
        return ResponseEntity.ok(responses);
    }


}
