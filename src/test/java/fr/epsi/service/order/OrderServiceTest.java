package fr.epsi.service.order;

import fr.epsi.service.order.dto.OrderDto;
import fr.epsi.service.order.dto.OrderProductItemDto;
import fr.epsi.service.order.dto.ProductCommandDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderPublisher orderPublisher;


    @Nested
    class getAllOrders {

        @Test
        void shouldCallFindAll() {
            orderService.getAll();
            verify(orderRepository).findAll();
        }

        @Test
        void shouldReturnListOfOrders() {
            OrderProduct order1 = new OrderProduct();
            order1.setId(1);
            order1.setCustomerId(123);

            OrderProduct order2 = new OrderProduct();
            order2.setId(2);
            order2.setCustomerId(456);

            OrderProductItem item1 = new OrderProductItem(null, 101, 10, order1);
            OrderProductItem item2 = new OrderProductItem(null, 102, 5, order1);
            OrderProductItem item3 = new OrderProductItem(null, 103, 7, order2);


            order1.setItems(List.of(item1, item2));
            order2.setItems(List.of(item3));

            List<OrderProduct> mockOrders = List.of(order1, order2);

            when(orderRepository.findAll()).thenReturn(mockOrders);

            List<OrderProduct> result = orderService.getAll();

            assertEquals(mockOrders, result);
        }
    }

    @Nested
    class create {

        @Test
        void shouldCreateOrderSuccessfully() {
            OrderDto createOrderDto = new OrderDto(123, List.of(
                    new OrderProductItemDto(101, 2),
                    new OrderProductItemDto(102, 5)
            ));

            OrderProduct createdOrder = new OrderProduct(123, List.of(
                    new OrderProductItem(null, 101, 2, null),
                    new OrderProductItem(null, 102, 5, null)
            ));

            when(orderRepository.save(Mockito.any(OrderProduct.class))).thenReturn(createdOrder);

            OrderProduct result = orderService.create(createOrderDto);

            verify(orderRepository).save(Mockito.any(OrderProduct.class));
            assertEquals(createdOrder.getCustomerId(), result.getCustomerId());
            assertEquals(createdOrder.getItems().size(), result.getItems().size());
            assertEquals(createdOrder.getItems().get(0).getProductId(), result.getItems().get(0).getProductId());
        }

        @Test
        void shouldPublishOrderItemsOnCreate() {
            OrderDto createOrderDto = new OrderDto(123, List.of(
                    new OrderProductItemDto(101, 2),
                    new OrderProductItemDto(102, 5)
            ));

            OrderProduct createdOrder = new OrderProduct(123, List.of(
                    new OrderProductItem(null, 101, 2, null),
                    new OrderProductItem(null, 102, 5, null)
            ));

            List<ProductCommandDto> expectedPayload = List.of(
                    new ProductCommandDto(101, 2),
                    new ProductCommandDto(102, 5)
            );

            when(orderRepository.save(Mockito.any(OrderProduct.class))).thenReturn(createdOrder);

            orderService.create(createOrderDto);

            verify(orderPublisher).publishOrderIds(expectedPayload);
        }
    }

    @Nested
    class delete {

        @Test
        void shouldCallDeleteById() {
            Integer id = 1;

            orderService.delete(id);

            verify(orderRepository).deleteById(id);
        }

        @Test
        void shouldNotThrowExceptionWhenOrderExists() {
            Integer id = 1;

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> orderService.delete(id));
        }
    }

    @Nested
    class getById {

        @Test
        void shouldReturnOrderWhenFound() {
            Integer orderId = 1;

            OrderProduct order = new OrderProduct();
            order.setId(orderId);
            order.setCustomerId(123);

            when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));

            OrderProduct result = orderService.getById(orderId);

            assertEquals(order, result);
        }

        @Test
        void shouldThrowExceptionWhenOrderNotFound() {
            Integer orderId = 1;

            when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

            ResponseStatusException exception = org.junit.jupiter.api.Assertions.assertThrows(
                    ResponseStatusException.class, () -> orderService.getById(orderId)
            );

            assertEquals("404 NOT_FOUND \"Order1not found\"", exception.getMessage());
        }

        @Nested
        class update {

            @Test
            void shouldUpdateOrderWhenOrderExists() {
                Integer orderId = 1;
                OrderDto updateOrderDto = new OrderDto(999, List.of(
                        new OrderProductItemDto(101, 10),
                        new OrderProductItemDto(102, 15)
                ));

                OrderProduct existingOrder = new OrderProduct();
                existingOrder.setId(orderId);
                existingOrder.setCustomerId(123);
                existingOrder.setItems(List.of(new OrderProductItem(null, 201, 5, existingOrder)));

                when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(existingOrder));

                OrderProduct updatedOrder = new OrderProduct();
                updatedOrder.setId(orderId);
                updatedOrder.setCustomerId(999);
                updatedOrder.setItems(List.of(
                        new OrderProductItem(null, 101, 10, updatedOrder),
                        new OrderProductItem(null, 102, 15, updatedOrder)
                ));

                when(orderRepository.save(Mockito.any(OrderProduct.class))).thenReturn(updatedOrder);

                OrderProduct result = orderService.update(orderId, updateOrderDto);

                verify(orderRepository).save(Mockito.any(OrderProduct.class));
                assertEquals(updatedOrder.getCustomerId(), result.getCustomerId());
                assertEquals(updatedOrder.getItems().size(), result.getItems().size());
                assertEquals(updatedOrder.getItems().get(0).getProductId(), result.getItems().get(0).getProductId());
            }

            @Test
            void shouldThrowExceptionWhenOrderToUpdateNotFound() {
                Integer orderId = 1;
                OrderDto updateOrderDto = new OrderDto(999, List.of(
                        new OrderProductItemDto(101, 10)
                ));

                when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

                ResponseStatusException exception = org.junit.jupiter.api.Assertions.assertThrows(
                        ResponseStatusException.class, () -> orderService.update(orderId, updateOrderDto)
                );

                assertEquals("404 NOT_FOUND \"Order1not found\"", exception.getMessage());
            }
        }
    }


}
