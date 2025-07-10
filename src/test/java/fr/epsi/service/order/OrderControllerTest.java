package fr.epsi.service.order;

import fr.epsi.service.order.dto.OrderDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        void shouldReturnAllOrders() throws Exception {
            List<OrderProduct> mockOrders = List.of(
                    new OrderProduct(1, new ArrayList<>()),
                    new OrderProduct(2, new ArrayList<>())
            );
            when(orderService.getAll()).thenReturn(mockOrders);

            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    class updateOrderTests {

        @Test
        void shouldUpdateOrderSuccessfully() throws Exception {
            OrderProduct updatedOrder = new OrderProduct();
            updatedOrder.setId(1);
            updatedOrder.setCustomerId(1);

            OrderProductItem item1 = new OrderProductItem(null, 101, 2, updatedOrder);
            OrderProductItem item2 = new OrderProductItem(null, 102, 3, updatedOrder);
            updatedOrder.setItems(List.of(item1, item2));

            when(orderService.update(Mockito.eq(1), Mockito.any(OrderDTO.class)))
                    .thenReturn(updatedOrder);

            mockMvc.perform(put("/api/orders/{id}", 1)
                            .contentType("application/json")
                            .content("""
                        {
                          "customerId": 1,
                          "items": [
                            {"productId": 101, "quantity": 2},
                            {"productId": 102, "quantity": 3}
                          ]
                        }
                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerId").value(1))
                    .andExpect(jsonPath("$.items.length()").value(2))
                    .andExpect(jsonPath("$.items[0].productId").value(101))
                    .andExpect(jsonPath("$.items[0].quantity").value(2))
                    .andExpect(jsonPath("$.items[1].productId").value(102))
                    .andExpect(jsonPath("$.items[1].quantity").value(3));
        }



        @Test
        void shouldReturnNotFoundWhenUpdatingNonExistentOrder() throws Exception {
            when(orderService.update(Mockito.eq(999), Mockito.any(OrderDTO.class)))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order 999 not found"));

            mockMvc.perform(put("/api/orders/{id}", 999)
                            .contentType("application/json")
                            .content("""
                {
                  "customerId": 1,
                  "items": []
                }
            """))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class getOrderByIdTests {

        @Test
        void shouldReturnOrderByIdWhenExists() throws Exception {
            OrderProduct mockOrder = new OrderProduct(1, new ArrayList<>());

            when(orderService.getById(1)).thenReturn(mockOrder);

            mockMvc.perform(get("/api/orders/{id}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerId").value(1));
        }

        @Test
        void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
            when(orderService.getById(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order 999 not found"));

            mockMvc.perform(get("/api/orders/{id}", 999))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class createOrderTests {

        @Test
        void shouldCreateOrderSuccessfully() throws Exception {
            OrderProduct createdOrder = new OrderProduct();
            createdOrder.setId(1);
            createdOrder.setCustomerId(1);

            OrderProductItem item1 = new OrderProductItem(null, 101, 2, createdOrder);
            OrderProductItem item2 = new OrderProductItem(null, 102, 3, createdOrder);
            createdOrder.setItems(List.of(item1, item2));

            when(orderService.create(Mockito.any(OrderDTO.class))).thenReturn(createdOrder);

            mockMvc.perform(post("/api/orders")
                            .contentType("application/json")
                            .content("""
                                        {
                                          "customerId": 1,
                                          "items": [
                                            {"productId": 101, "quantity": 2},
                                            {"productId": 102, "quantity": 3}
                                          ]
                                        }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerId").value(1))
                    .andExpect(jsonPath("$.items.length()").value(2))
                    .andExpect(jsonPath("$.items[0].productId").value(101))
                    .andExpect(jsonPath("$.items[0].quantity").value(2))
                    .andExpect(jsonPath("$.items[1].productId").value(102))
                    .andExpect(jsonPath("$.items[1].quantity").value(3));
        }

        @Test
        void shouldReturnBadRequestForInvalidOrderInput() throws Exception {
            mockMvc.perform(post("/api/orders")
                            .contentType("application/json")
                            .content("""
                                      
                                    """))
                    .andExpect(status().isBadRequest());
        }

    }
}
