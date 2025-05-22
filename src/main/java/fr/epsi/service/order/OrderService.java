package fr.epsi.service.order;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import fr.epsi.service.order.dto.OrderDTO;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order getById(Integer id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order" + id + "not found")
        );
    }

    public Order update(Integer id, OrderDTO updateOrderDTO) {
        Order dbOrder = getById(id);
        return orderRepository.save(new Order(
                dbOrder.getId(),
                dbOrder.getCreatedAt(),
                updateOrderDTO.getCustomerId()
        ));
    }

    public Order create(OrderDTO createOrderDTO) {
        return orderRepository.save(new Order(
            createOrderDTO.getCustomerId()
        ));
    }

    public void delete(Integer id) {
        orderRepository.deleteById(id);
    }


}
