package com.omnistock.inventory.controller;

import com.omnistock.inventory.entity.Inventory;
import com.omnistock.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(
                inventoryService.getAllInventory()
        );
    }

    @GetMapping("/{productId}/{warehouseCode}")
    public ResponseEntity<Inventory> getInventory(
            @PathVariable Long productId,
            @PathVariable String warehouseCode) {

        return ResponseEntity.ok(
                inventoryService.getInventory(
                        productId,
                        warehouseCode
                )
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Inventory>> getInventoryByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProduct(productId)
        );
    }

    @GetMapping("/warehouse/{warehouseCode}")
    public ResponseEntity<List<Inventory>> getInventoryByWarehouse(
            @PathVariable String warehouseCode) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByWarehouse(
                        warehouseCode
                )
        );
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(
            @RequestBody Inventory inventory) {

        Inventory createdInventory =
                inventoryService.createInventory(inventory);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdInventory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long id,
            @RequestBody Inventory inventory) {

        return ResponseEntity.ok(
                inventoryService.updateInventory(
                        id,
                        inventory
                )
        );
    }

    @PostMapping("/reserve")
    public ResponseEntity<Inventory> reserveStock(
            @RequestParam Long productId,
            @RequestParam String warehouseCode,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.reserveStock(
                        productId,
                        warehouseCode,
                        quantity
                )
        );
    }

    @PostMapping("/release")
    public ResponseEntity<Inventory> releaseStock(
            @RequestParam Long productId,
            @RequestParam String warehouseCode,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.releaseStock(
                        productId,
                        warehouseCode,
                        quantity
                )
        );
    }

    @GetMapping("/{id}/low-stock")
    public ResponseEntity<Boolean> checkLowStock(
            @PathVariable Long id) {

        Inventory inventory = inventoryService
                .getAllInventory()
                .stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found with id: " + id
                        ));

        return ResponseEntity.ok(
                inventoryService.isLowStock(inventory)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(
            @PathVariable Long id) {

        inventoryService.deleteInventory(id);

        return ResponseEntity.noContent().build();
    }
}