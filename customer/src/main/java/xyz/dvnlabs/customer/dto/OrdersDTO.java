package xyz.dvnlabs.customer.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDTO {
    private Long id;
    private LocalDateTime trxDate;
    @Digits(integer = 18, fraction = 5, message = "TRX AMOUNT must ({integer},{fraction})")
    private BigDecimal trxAmount;
    private Long customerID;
    private String customerName;
    private String merchantID;
    private String merchantName;
    /**
     * Trx Status
     * <p>
     * 0 = PENDING PAYMENT
     * 1 = SUCCESS
     * 2 = REJECTED REQUEST
     */
    @Size(max = 1, min = 1, message = "TRX STATUS max {max} min {min}")
    private String trxStatus;
    private String trxRemark;

}
