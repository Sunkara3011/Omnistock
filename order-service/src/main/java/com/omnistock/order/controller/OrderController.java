package com.omnistock.order.controller;

import com.omnistock.order.entity.Order;
import com.omnistock.order.entity.OrderItem;
import com.omnistock.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    @GetMapping("/customer/{username}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(
            @PathVariable String username) {

        return ResponseEntity.ok(
                orderService.getOrdersByCustomer(username)
        );
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam String customerUsername,
            @RequestParam String warehouseCode,
            @RequestBody List<OrderItem> items) {

        Order createdOrder =
                orderService.createOrder(
                        customerUsername,
                        warehouseCode,
                        items
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOrder);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.cancelOrder(id)
        );
    }
}