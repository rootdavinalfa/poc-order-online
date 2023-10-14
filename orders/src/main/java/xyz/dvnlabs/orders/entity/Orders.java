package xyz.dvnlabs.orders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ORDERS")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_REFERENCES")
    private Long id;

    @Column(name = "TRX_DATE",columnDefinition = "TIMESTAMP")
    private LocalDateTime trxDate;

    @Column(name = "TRX_AMOUNT", precision = 23, scale = 5)
    @Digits(integer = 18, fraction = 5, message = "TRX AMOUNT must ({integer},{fraction})")
    private BigDecimal trxAmount;

    @Column(name = "CUSTOMER_ID")
    private String customerID;

    @Transient
    private String customerName;

    @Column(name = "MERCHANT_ID")
    private String merchantID;

    @Transient
    private String merchantName;



    /**
     * Trx Status
     * <p>
     * 0 = PENDING PAYMENT
     * 1 = SUCCESS
     * 2 = REJECTED REQUEST
     */
    @Column(name = "TRX_STATUS", length = 1)
    @Size(max = 1, min = 1, message = "TRX STATUS max {max} min {min}")
    private String trxStatus;

    @Column(name = "TRX_REMARK", columnDefinition = "text")
    private String trxRemark;

}
