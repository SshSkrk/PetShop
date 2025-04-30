package sasha.org.petshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sasha.org.petshop.dto.OrderDTO;
import sasha.org.petshop.dto.OrderStatusUpdateDTO;
import sasha.org.petshop.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/order/createOrder")
    public ResponseEntity<Boolean> createOrder(@RequestBody OrderDTO orderDTO) {
        boolean created = orderService.createOrder(orderDTO);
        return created
                ? ResponseEntity.status(HttpStatus.CREATED).body(true)  // Created successfully
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); // Failed to create
    }

    @PostMapping("/admin/updateOrder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> updateOrder(@RequestBody OrderStatusUpdateDTO orderStatusDTO) {
        boolean updated = orderService.updateOrder(orderStatusDTO);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).body(true)  // Updated successfully
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Order not found to update
    }

    @GetMapping("/admin/ListOfAllOrders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return orders; // Return list of orders
    }

    @DeleteMapping("/admin/deleteOrder/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteOrder(@PathVariable("id") Integer id) {
        boolean deleted = orderService.deleteOrder(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)  // Deleted successfully
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Order not found to delete
    }

    @GetMapping("/admin/ordersByCustomer/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDTO> ordersByCustomer(@PathVariable("username") String username) {
        List<OrderDTO> orders = orderService.ordersByCustomer(username);
        return orders; // Return list of orders
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<OrderDTO> getMyOrders(Authentication auth) {
        String username = auth.getName();
        return orderService.ordersByCustomer(username);
    }
}
