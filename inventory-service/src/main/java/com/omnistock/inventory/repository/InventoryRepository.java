package com.omnistock.inventory.repository;

import com.omnistock.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductIdAndWarehouseCode(
            Long productId,
            String warehouseCode
    );

    List<Inventory> findByProductId(Long productId);

    List<Inventory> findByWarehouseCode(String warehouseCode);
}
