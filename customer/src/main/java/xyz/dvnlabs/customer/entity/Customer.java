package xyz.dvnlabs.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;

@Entity(name = "CUSTOMER")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID")
    private Long id;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "BALANCE", precision = 23, scale = 5)
    @Digits(integer = 18, fraction = 5, message = "BALANCE must ({integer},{fraction})")
    private BigDecimal balance;



}
