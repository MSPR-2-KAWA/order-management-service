package fr.epsi.service.order;

import fr.epsi.service.order.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderProduct> getAll() {
        return orderRepository.findAll();
    }

    public OrderProduct getById(Integer id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order" + id + "not found")
        );
    }

    public OrderProduct update(Integer id, OrderDTO updateOrderDTO) {
        OrderProduct dbOrderProduct = getById(id);
        return orderRepository.save(new OrderProduct(
                dbOrderProduct.getId(),
                dbOrderProduct.getCreatedAt(),
                updateOrderDTO.getCustomerId(),
                updateOrderDTO.getProductIds()
        ));
    }

    public OrderProduct create(OrderDTO createOrderDTO) {
        return orderRepository.save(new OrderProduct(
                createOrderDTO.getCustomerId(),
                createOrderDTO.getProductIds()
        ));
    }

    public void delete(Integer id) {
        orderRepository.deleteById(id);
    }


}
