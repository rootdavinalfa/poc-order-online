package xyz.dvnlabs.merchant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xyz.dvnlabs.merchant.entity.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    @Query(value = "SELECT * FROM merchant",nativeQuery = true)
    Page<Merchant> getPageWithQuery(
            Pageable pageable
    );


}
