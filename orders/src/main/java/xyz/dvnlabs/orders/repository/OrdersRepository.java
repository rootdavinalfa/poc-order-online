package xyz.dvnlabs.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.dvnlabs.orders.entity.Orders;

import java.time.LocalDateTime;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query(value = "SELECT * FROM orders " +
            "WHERE trx_date BETWEEN :dateFrom AND :dateTo ", nativeQuery = true)
    Page<Orders> getOrderWithQuery(
            Pageable pageable,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo
    );

}
