package fr.epsi.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class OrderDto {
    Integer customerId;
    List<OrderProductItemDto> items;
}
