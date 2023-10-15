package xyz.dvnlabs.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import xyz.dvnlabs.customer.dto.OrdersDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "CUSTOMER_PAYMENT_HISTORY")
@Table(indexes = {
        @Index(name = "IDX_UNIQUE_CUSTOMER", columnList = "CUSTOMER_ID,PAYMENT_ID", unique = true)
})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CustomerPaymentHistory {
    @Id
    @Column(name = "CUSTOMER_PAYMENT_REFERENCES")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER_ID")
    private Long customerID;

    @Column(name = "PAYMENT_ID")
    private Long paymentID;

    @Column(name = "ORDERS_ID")
    private Long ordersID;

    @Column(name = "CREATED_ON", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdOn;

    @Column(name = "AMOUNT", precision = 23, scale = 5)
    @Digits(integer = 18, fraction = 5, message = "AMOUNT must ({integer},{fraction})")
    private BigDecimal amount;

    /**
     * 0 = Customer not acknowledge
     * 1 = Customer acknowledge
     */
    @Column(name = "CUSTOMER_ACK", length = 1)
    private String customerAck;

    @Transient
    private OrdersDTO orders;

}
