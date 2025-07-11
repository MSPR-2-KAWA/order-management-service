package fr.epsi.service.order;
import fr.epsi.service.order.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/api/orders")
    public List<OrderProduct> getAllOrder() {
        return orderService.getAll();
    }

    @GetMapping("api/orders/{id}")
    public OrderProduct getOrderById(@PathVariable Integer id) {
        return orderService.getById(id);
    }

    @PutMapping("api/orders/{id}")
    public OrderProduct updateOrder(@PathVariable Integer id, @RequestBody OrderDto updateOrderDto) {
        return orderService.update(id, updateOrderDto);
    }

    @PostMapping("api/orders")
    public OrderProduct createOrder(@RequestBody OrderDto createOrderDto) {
        return orderService.create(createOrderDto);
    }

    @DeleteMapping("api/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
