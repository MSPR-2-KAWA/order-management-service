package fr.epsi.service.order;

import fr.epsi.service.order.dto.OrderDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @MockitoBean
    OrderService orderService;

    @Autowired
    MockMvc mockMvc;


    @Nested
    class getAllOrders{

        @Test
        void testGetAllOrders() throws Exception {
            OrderProduct order1 = new OrderProduct(1, Arrays.asList(1, 2));
            OrderProduct order2 = new OrderProduct(2, Arrays.asList(3, 4));
            List<OrderProduct> orders = List.of(order1, order2);

            when(orderService.getAll()).thenReturn(orders);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].customerId").value(1))
                    .andExpect(jsonPath("$[0].productIds[0]").value(1))
                    .andExpect(jsonPath("$[0].productIds[1]").value(2))
                    .andExpect(jsonPath("$[1].customerId").value(2))
                    .andExpect(jsonPath("$[1].productIds[0]").value(3))
                    .andExpect(jsonPath("$[1].productIds[1]").value(4));

        }
    }

    @Nested
    class updateOrderTests {

        @Test
        void testUpdateOrder_ValidId() throws Exception {
            OrderDTO updateOrderDTO = new OrderDTO(1, Arrays.asList(5, 6));
            OrderProduct updatedOrder = new OrderProduct(1, Arrays.asList(5, 6));

            when(orderService.update(1, updateOrderDTO)).thenReturn(updatedOrder);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/1")
                            .contentType("application/json")
                            .content("{\"customerId\":1,\"productIds\":[5,6]}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerId").value(1))
                    .andExpect(jsonPath("$.productIds[0]").value(5))
                    .andExpect(jsonPath("$.productIds[1]").value(6));
        }

        @Test
        void testUpdateOrder_InvalidId() throws Exception {
            OrderDTO updateOrderDTO = new OrderDTO(1, Arrays.asList(5, 6));

            when(orderService.update(999, updateOrderDTO))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order 999 not found"));

            mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/999")
                            .contentType("application/json")
                            .content("{\"customerId\":1,\"productIds\":[5,6]}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void testUpdateOrder_InvalidRequestBody() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/1")
                            .contentType("application/json")
                            .content("{\"invalidField\":true}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class getOrderByIdTests {

        @Test
        void testGetOrderById_ValidId() throws Exception {
            OrderProduct order = new OrderProduct(1, Arrays.asList(1, 2));
            when(orderService.getById(1)).thenReturn(order);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerId").value(1))
                    .andExpect(jsonPath("$.productIds[0]").value(1))
                    .andExpect(jsonPath("$.productIds[1]").value(2));
        }

        @Test
        void testGetOrderById_InvalidId() throws Exception {
            when(orderService.getById(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order 999 not found"));

            mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class createOrderTests {

        @Test
        void testCreateOrder_ValidRequest() throws Exception {
            OrderDTO createOrderDTO = new OrderDTO(1, Arrays.asList(7, 8));
            OrderProduct createdOrder = new OrderProduct(1, Arrays.asList(7, 8));

            when(orderService.create(createOrderDTO)).thenReturn(createdOrder);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                            .contentType("application/json")
                            .content("{\"customerId\":1,\"productIds\":[7,8]}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerId").value(1))
                    .andExpect(jsonPath("$.productIds[0]").value(7))
                    .andExpect(jsonPath("$.productIds[1]").value(8));
        }

        @Test
        void testCreateOrder_InvalidRequestBody() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                            .contentType("application/json")
                            .content(""))
                    .andExpect(status().isBadRequest());
        }
    }
}
