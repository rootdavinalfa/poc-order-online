package xyz.dvnlabs.merchant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.dvnlabs.merchant.entity.MerchantOrders;

@Repository
public interface MerchantOrdersRepository extends JpaRepository<MerchantOrders, Long> {

    @Query(value = "SELECT * FROM merchant_orders",nativeQuery = true)
    Page<MerchantOrders> getPageMerchantOrdersWithQuery(
            Pageable pageable
    );

}
