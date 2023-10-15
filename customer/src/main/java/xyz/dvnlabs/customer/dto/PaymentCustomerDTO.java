package xyz.dvnlabs.customer.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCustomerDTO {

    private Long id;
    private Long orderID;
    private Long customerID;
    private LocalDateTime paidOn;
    private LocalDateTime requestedOn;

    /**
     * 0 = Pending
     * 1 = Paid
     * 2 = Expired
     */
    private String paymentStatus;

}
