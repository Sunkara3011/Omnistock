package com.omnistock.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8083")
                .build();
    }

    public void reserveStock(
            Long productId,
            String warehouseCode,
            Integer quantity) {

        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/inventory/reserve")
                        .queryParam("productId", productId)
                        .queryParam("warehouseCode", warehouseCode)
                        .queryParam("quantity", quantity)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }
}