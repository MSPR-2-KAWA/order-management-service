package fr.epsi.service.order;

import fr.epsi.service.order.dto.OrderDto;
import fr.epsi.service.order.dto.ProductCommandDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderPublisher orderPublisher;

    public List<OrderProduct> getAll() {
        return orderRepository.findAll();
    }

    public OrderProduct getById(Integer id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order" + id + "not found")
        );
    }

    public OrderProduct update(Integer id, OrderDto updateOrderDto) {
        OrderProduct dbOrderProduct = getById(id);

        List<OrderProductItem> items = updateOrderDto.getItems().stream()
                .map(dto -> new OrderProductItem(null, dto.getProductId(), dto.getQuantity(), null))
                .toList();

        OrderProduct updatedOrder = new OrderProduct(
                dbOrderProduct.getId(),
                dbOrderProduct.getCreatedAt(),
                updateOrderDto.getCustomerId(),
                items
        );

        items.forEach(i -> i.setOrder(updatedOrder));

        return orderRepository.save(updatedOrder);
    }

    public OrderProduct create(OrderDto createOrderDto) {
        List<OrderProductItem> items = createOrderDto.getItems().stream()
                .map(dto -> new OrderProductItem(null, dto.getProductId(), dto.getQuantity(), null))
                .toList();

        OrderProduct order = new OrderProduct(createOrderDto.getCustomerId(), items);

        items.forEach(i -> i.setOrder(order));

        List<ProductCommandDto> pubsubPayload = items.stream()
                .map(i -> new ProductCommandDto(i.getProductId(), i.getQuantity()))
                .toList();

        orderPublisher.publishOrderIds(pubsubPayload);

        return orderRepository.save(order);
    }


    public void delete(Integer id) {
        orderRepository.deleteById(id);
    }


}
