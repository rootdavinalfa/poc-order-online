package xyz.dvnlabs.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "PAYMENT")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_REFERENCES")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderID;

    @Column(name = "PAID_ON")
    private LocalDateTime paidOn;

    @Column(name = "REQUESTED_ON")
    private LocalDateTime requestedOn;

    /**
     * 0 = Pending
     * 1 = Paid
     * 2 = Expired
     */
    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

}
