package fr.epsi.service.order.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductCommandDto {

    private Integer idProduit;
    private Integer quantity;

}
