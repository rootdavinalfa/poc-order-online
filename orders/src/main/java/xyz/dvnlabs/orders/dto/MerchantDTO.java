package xyz.dvnlabs.orders.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class MerchantDTO {

    private Long id;
    private String merchantName;

}
