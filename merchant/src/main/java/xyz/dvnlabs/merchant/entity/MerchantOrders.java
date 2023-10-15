package xyz.dvnlabs.merchant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import xyz.dvnlabs.merchant.dto.OrdersDTO;

@Entity(name = "MERCHANT_ORDERS")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class MerchantOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MERCHANT_ORDERS_ID")
    private Long id;

    @Column(name = "MERCHANT_ID")
    private Long merchantID;

    @Column(name = "ORDERS_ID")
    private Long ordersID;

    @Transient
    private OrdersDTO orders;

}
