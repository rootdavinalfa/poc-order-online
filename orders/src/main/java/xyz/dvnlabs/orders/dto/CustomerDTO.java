package xyz.dvnlabs.orders.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long id;
    private String customerName;
    private BigDecimal balance;
}
