package fr.epsi.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    @GetMapping("/order")
    public String HelloWorld() {
        return "Hello world";
    }
}
