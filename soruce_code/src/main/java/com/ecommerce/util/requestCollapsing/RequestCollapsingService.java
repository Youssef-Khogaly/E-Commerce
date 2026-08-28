package com.ecommerce.util.requestCollapsing;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;

@Component
public class RequestCollapsingService {

    private final ConcurrentMap<String, CompletableFuture<Object>> map;

    public RequestCollapsingService() {
        this.map = new ConcurrentHashMap<>(512);
    }

    public RequestCollapsingService(int capacity) {
        this.map = new ConcurrentHashMap<>(capacity);
    }


    public Object execute(final String key, final Supplier<Object> supplier) throws Throwable {
        var func =  map.computeIfAbsent(key,(k) -> CompletableFuture.supplyAsync(supplier));

        func.whenCompleteAsync((result,error) -> map.remove(key,func));

        try{
            return func.join();
        }catch (CompletionException e){
            throw e.getCause();
        }
    }
    public Object execute(final String key, final Supplier<Object> supplier , final Executor executor) throws Throwable {
        var func =  map.computeIfAbsent(key,(k) -> CompletableFuture.supplyAsync(supplier,executor));
        // async to avoid block result for retries
        func.whenCompleteAsync((result,error) -> map.remove(key,func));

        try{
            return func.join();
        }catch (CompletionException e){
            throw e.getCause();
        }

    }
}
