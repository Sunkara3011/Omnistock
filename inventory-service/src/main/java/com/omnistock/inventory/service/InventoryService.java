package com.omnistock.inventory.service;

import com.omnistock.inventory.entity.Inventory;
import com.omnistock.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventory(
            Long productId,
            String warehouseCode) {

        return inventoryRepository
                .findByProductIdAndWarehouseCode(
                        productId,
                        warehouseCode
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found for product "
                                        + productId
                                        + " in warehouse "
                                        + warehouseCode
                        ));
    }

    public List<Inventory> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public List<Inventory> getInventoryByWarehouse(
            String warehouseCode) {

        return inventoryRepository.findByWarehouseCode(warehouseCode);
    }

    public Inventory createInventory(Inventory inventory) {

        if (inventory.getReservedQuantity() == null) {
            inventory.setReservedQuantity(0);
        }

        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(
            Long id,
            Inventory updatedInventory) {

        Inventory existingInventory = inventoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found with id: " + id
                        ));

        existingInventory.setProductId(
                updatedInventory.getProductId()
        );

        existingInventory.setWarehouseCode(
                updatedInventory.getWarehouseCode()
        );

        existingInventory.setQuantity(
                updatedInventory.getQuantity()
        );

        existingInventory.setReservedQuantity(
                updatedInventory.getReservedQuantity()
        );

        existingInventory.setReorderLevel(
                updatedInventory.getReorderLevel()
        );

        return inventoryRepository.save(existingInventory);
    }

    public Inventory reserveStock(
            Long productId,
            String warehouseCode,
            Integer quantity) {

        Inventory inventory = getInventory(
                productId,
                warehouseCode
        );

        int availableStock =
                inventory.getQuantity()
                        - inventory.getReservedQuantity();

        if (availableStock < quantity) {
            throw new RuntimeException(
                    "Insufficient available stock"
            );
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + quantity
        );

        return inventoryRepository.save(inventory);
    }

    public Inventory releaseStock(
            Long productId,
            String warehouseCode,
            Integer quantity) {

        Inventory inventory = getInventory(
                productId,
                warehouseCode
        );

        int newReservedQuantity =
                inventory.getReservedQuantity() - quantity;

        if (newReservedQuantity < 0) {
            throw new RuntimeException(
                    "Cannot release more stock than reserved"
            );
        }

        inventory.setReservedQuantity(newReservedQuantity);

        return inventoryRepository.save(inventory);
    }

    public boolean isLowStock(Inventory inventory) {

        int availableStock =
                inventory.getQuantity()
                        - inventory.getReservedQuantity();

        return availableStock <= inventory.getReorderLevel();
    }

    public void deleteInventory(Long id) {

        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found with id: " + id
                        ));

        inventoryRepository.delete(inventory);
    }
}
