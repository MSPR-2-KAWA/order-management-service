package fr.epsi.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class OrderProductItemDto {
    Integer productId;
    Integer quantity;
}
