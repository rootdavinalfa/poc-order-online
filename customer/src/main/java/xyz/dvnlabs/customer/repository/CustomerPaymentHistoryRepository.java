package xyz.dvnlabs.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.dvnlabs.customer.entity.CustomerPaymentHistory;

import java.util.Optional;

@Repository
public interface CustomerPaymentHistoryRepository extends JpaRepository<CustomerPaymentHistory, Long> {

    @Query(value = "SELECT * FROM customer_payment_history " +
            "WHERE customer_ack = COALESCE(NULLIF(:customerAck,''),customer_ack) " +
            "AND customer_id = :customerID", nativeQuery = true)
    Page<CustomerPaymentHistory> getPageWithQuery(
            Pageable pageable,
            @Param("customerID") String customerID,
            @Param("customerAck") String customerAck
    );

    @Query(value = "SELECT DISTINCT * FROM customer_payment_history " +
            "WHERE customer_id = :customerID AND payment_id = :paymentID ", nativeQuery = true)
    Optional<CustomerPaymentHistory> getHistoryWithQuery(
            @Param("customerID") Long customerID,
            @Param("paymentID") Long paymentID
    );
}
