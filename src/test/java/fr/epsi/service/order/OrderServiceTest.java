package fr.epsi.service.order;

import fr.epsi.service.order.dto.OrderDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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


    @Nested
    class getAllOrders {

        @Test
        void shouldCallFindAll() {
            orderService.getAll();
            verify(orderRepository).findAll();
        }

        @Test
        void shouldReturnListOfOrders() {
            List<OrderProduct> mockOrders = List.of(
                    new OrderProduct(1, List.of(101, 102)),
                    new OrderProduct(2, List.of(103, 104))
            );
            when(orderRepository.findAll()).thenReturn(mockOrders);

            List<OrderProduct> result = orderService.getAll();

            assertEquals(mockOrders, result);
        }
    }

    @Nested
    class getById {

        @Test
        void shouldCallFindById() {
            Integer id = 1;
            OrderProduct mockOrder = new OrderProduct(id, List.of(101));
            when(orderRepository.findById(id)).thenReturn(java.util.Optional.of(mockOrder));

            orderService.getById(id);


            verify(orderRepository).findById(id);
        }

        @Test
        void shouldReturnOrderWhenIdExists() {
            Integer id = 1;
            OrderProduct mockOrder = new OrderProduct(id, List.of(101));
            when(orderRepository.findById(id)).thenReturn(java.util.Optional.of(mockOrder));

            OrderProduct result = orderService.getById(id);

            assertEquals(mockOrder, result);
        }

        @Test
        void shouldThrowExceptionWhenIdDoesNotExist() {
            Integer id = 1;
            when(orderRepository.findById(id)).thenReturn(java.util.Optional.empty());

            org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class, () -> orderService.getById(id));
        }
    }

    @Nested
    class update {

        @Test
        void shouldUpdateCustomerIdAndProductIds() {
            Integer id = 1;
            OrderDTO updateDTO = new OrderDTO(2, List.of(201, 202));
            OrderProduct existingOrder = new OrderProduct(id, LocalDateTime.now(),1, List.of(101, 102));
            when(orderRepository.findById(id)).thenReturn(java.util.Optional.of(existingOrder));
            OrderProduct updatedOrder = new OrderProduct(
                    id,
                    existingOrder.getCreatedAt(),
                    updateDTO.getCustomerId(),
                    updateDTO.getProductIds()
            );
            when(orderRepository.save(updatedOrder)).thenReturn(updatedOrder);

            OrderProduct result = orderService.update(id, updateDTO);

            assertEquals(updatedOrder, result);
        }

        @Test
        void shouldThrowExceptionWhenOrderDoesNotExist() {
            Integer id = 1;
            OrderDTO updateDTO = new OrderDTO(2, List.of(201, 202));
            when(orderRepository.findById(id)).thenReturn(java.util.Optional.empty());

            org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class, () -> orderService.update(id, updateDTO));
        }
    }

    @Nested
    class create {

        @Test
        void shouldCallSaveWithCorrectParameters() {
            OrderDTO createDTO = new OrderDTO(2, List.of(301, 302));
            OrderProduct mockOrder = new OrderProduct(2, List.of(301, 302));
            when(orderRepository.save(mockOrder)).thenReturn(mockOrder);

            orderService.create(createDTO);

            verify(orderRepository).save(new OrderProduct(createDTO.getCustomerId(), createDTO.getProductIds()));
        }

        @Test
        void shouldReturnSavedOrder() {
            OrderDTO createDTO = new OrderDTO(2, List.of(301, 302));
            OrderProduct mockOrder = new OrderProduct(2, List.of(301, 302));
            when(orderRepository.save(new OrderProduct(createDTO.getCustomerId(), createDTO.getProductIds()))).thenReturn(mockOrder);

            OrderProduct result = orderService.create(createDTO);

            assertEquals(mockOrder, result);
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


}
