package xyz.dvnlabs.merchant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity(name = "MERCHANT")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MERCHANT_ID")
    private Long id;

    @Column(name = "MERCHANT_NAME")
    private String merchantName;

}
