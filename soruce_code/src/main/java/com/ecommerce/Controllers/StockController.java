package com.ecommerce.Controllers;

import com.ecommerce.DTO.Requests.StockQuantityRequest;
import com.ecommerce.DTO.StockResponseDto;
import com.ecommerce.entities.Products.ProductStock;
import com.ecommerce.services.StockService.IStockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stocks")
@Validated
@AllArgsConstructor
public class StockController{

    private final IStockService stockService;

    @GetMapping("/{id}")
    ResponseEntity<StockResponseDto> getStock(@PathVariable @Positive @NotNull Long id)
    {
        ProductStock st = stockService.findByIdReadOnly(id);
        StockResponseDto stockResponse = StockResponseDto.from(st);

        return ResponseEntity.ok(stockResponse);
    }
    @GetMapping("/")
    ResponseEntity<Set<StockResponseDto>> getStocks(@RequestParam(name = "ids") @NotNull @NotEmpty Set<Long> ids)
    {
        Set<ProductStock> stocks = stockService.findAllByIdReadOnly(ids);
        Set<StockResponseDto> stockResponses = stocks.stream()
                .map(StockResponseDto::from)
                .collect(Collectors.toUnmodifiableSet());

        return ResponseEntity.ok(stockResponses);
    }

    @PostMapping("/{id}/add")
    ResponseEntity<StockResponseDto>addStock(@RequestBody @Valid @NotNull StockQuantityRequest quantityRequest, @PathVariable @Positive @NotNull Long id)
    {
        var stock = stockService.add(id,quantityRequest.quantity());

        return ResponseEntity.ok(StockResponseDto.from(stock));
    }


    @PostMapping("/{id}/remove")
    ResponseEntity<StockResponseDto>removeStock(@RequestBody @Valid @NotNull StockQuantityRequest quantityRequest, @PathVariable @Positive @NotNull Long id)
    {
        var stock = stockService.remove(id,quantityRequest.quantity());

        return ResponseEntity.ok(StockResponseDto.from(stock));
    }


    @PostMapping("/add")
    ResponseEntity<Set<StockResponseDto>>addStocks(@RequestBody @NotEmpty @Valid Set<StockQuantityRequest> stockQuantityReqSet)
    {
        var idQuantityMap = stockQuantityReqSet.stream().collect(Collectors.toMap(StockQuantityRequest::id, StockQuantityRequest::quantity));
        var stocks = stockService.add(idQuantityMap);
        var responses = stocks.stream().map(StockResponseDto::from).collect(Collectors.toUnmodifiableSet());
        return ResponseEntity.ok(responses);
    }


    @PostMapping("/remove")
    ResponseEntity<Set<StockResponseDto>>removeStocks(@RequestBody @NotEmpty @Valid Set<StockQuantityRequest> stockQuantityReqSet)
    {
        var idQuantityMap = stockQuantityReqSet.stream().collect(Collectors.toMap(StockQuantityRequest::id, StockQuantityRequest::quantity));
        var stocks = stockService.remove(idQuantityMap);
        var responses = stocks.stream().map(StockResponseDto::from).collect(Collectors.toUnmodifiableSet());
        return ResponseEntity.ok(responses);
    }


}
