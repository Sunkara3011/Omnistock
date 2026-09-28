package com.omnistock.order.service;

import com.omnistock.order.client.InventoryClient;
import com.omnistock.order.client.ProductClient;
import com.omnistock.order.entity.Order;
import com.omnistock.order.entity.OrderItem;
import com.omnistock.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;

    public OrderService(
            OrderRepository orderRepository,
            InventoryClient inventoryClient,
            ProductClient productClient) {

        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.productClient = productClient;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + id
                        )
                );
    }

    public List<Order> getOrdersByCustomer(String customerUsername) {
        return orderRepository.findByCustomerUsername(
                customerUsername
        );
    }

    @Transactional
    public Order createOrder(
            String customerUsername,
            String warehouseCode,
            List<OrderItem> items) {

        if (items == null || items.isEmpty()) {
            throw new RuntimeException(
                    "Order must contain at least one item"
            );
        }

        Order order = new Order();

        order.setCustomerUsername(customerUsername);
        order.setWarehouseCode(warehouseCode);
        order.setStatus("PLACED");

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {

            if (item.getQuantity() == null
                    || item.getQuantity() <= 0) {

                throw new RuntimeException(
                        "Item quantity must be greater than zero"
                );
            }

            /*
             * Ask Product Service for the real product details.
             */
            ProductClient.ProductResponse product =
                    productClient.getProduct(item.getProductId());

            if (product == null) {
                throw new RuntimeException(
                        "Product not found with id: "
                                + item.getProductId()
                );
            }

            if (product.active() == null || !product.active()) {
                throw new RuntimeException(
                        "Product is inactive: "
                                + item.getProductId()
                );
            }

            /*
             * Use the price from Product Service.
             * Do not trust the price supplied by the client.
             */
            BigDecimal unitPrice = product.price();

            if (unitPrice == null
                    || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {

                throw new RuntimeException(
                        "Product price must be greater than zero"
                );
            }

            BigDecimal subtotal =
                    unitPrice.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()
                            )
                    );

            /*
             * Reserve stock only after the product
             * has been successfully validated.
             */
            inventoryClient.reserveStock(
                    item.getProductId(),
                    warehouseCode,
                    item.getQuantity()
            );

            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);
            item.setOrder(order);

            order.getItems().add(item);

            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);
        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }
}