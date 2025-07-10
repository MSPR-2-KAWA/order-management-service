package fr.epsi.service.order;

import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @MockitoBean
    OrderService orderService;

    @Autowired
    MockMvc mockMvc;


    @Nested
    class getAllOrders{


    }

    @Nested
    class updateOrderTests {


    }

    @Nested
    class getOrderByIdTests {


    }

    @Nested
    class createOrderTests {


    }
}
