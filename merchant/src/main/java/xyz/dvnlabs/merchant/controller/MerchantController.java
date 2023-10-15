package xyz.dvnlabs.merchant.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.dvnlabs.merchant.entity.Merchant;
import xyz.dvnlabs.merchant.entity.MerchantOrders;
import xyz.dvnlabs.merchant.service.MerchantService;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/page")
    public Page<Merchant> merchantPage(
            Pageable pageable
    ) {
        return merchantService.merchantPage(pageable);
    }

    @GetMapping("/{id}")
    public Merchant findById(
            @PathVariable Long id
    ) {
        return merchantService.findById(id);
    }

    @GetMapping("/merchant-orders/page")
    public Page<MerchantOrders> merchantOrdersPage(
            Pageable pageable
    ) {
        return merchantService.merchantOrdersPage(pageable);
    }

    @PostMapping
    public void createMerchant(
            @RequestBody Merchant merchant
    ) {
        merchantService.save(merchant);
    }

}
