package xyz.dvnlabs.merchant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import xyz.dvnlabs.corelib.exception.ResourceExistException;
import xyz.dvnlabs.corelib.exception.ResourceNotFoundException;
import xyz.dvnlabs.merchant.dto.OrdersDTO;
import xyz.dvnlabs.merchant.entity.Merchant;
import xyz.dvnlabs.merchant.entity.MerchantOrders;
import xyz.dvnlabs.merchant.repository.MerchantOrdersRepository;
import xyz.dvnlabs.merchant.repository.MerchantRepository;

@Service
public class MerchantService {

    private static final String SERVICE_ID_NAME = "MERCHANT";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MerchantRepository merchantRepository;
    private final MerchantOrdersRepository merchantOrdersRepository;

    public MerchantService(RestTemplate restTemplate, ObjectMapper objectMapper, MerchantRepository merchantRepository,
                           MerchantOrdersRepository merchantOrdersRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.merchantRepository = merchantRepository;
        this.merchantOrdersRepository = merchantOrdersRepository;
    }

    public Page<Merchant> merchantPage(
            Pageable pageable
    ) {
        return merchantRepository.getPageWithQuery(pageable);
    }

    public Page<MerchantOrders> merchantOrdersPage(
            Pageable pageable
    ) {
        return merchantOrdersRepository.getPageMerchantOrdersWithQuery(pageable)
                .map(merchantOrders -> {
                    try {
                        OrdersDTO ordersDTO =
                                restTemplate.getForObject("http://ORDERS/orders/" + merchantOrders.getOrdersID(), OrdersDTO.class);
                        merchantOrders.setOrders(ordersDTO);
                    } catch (RestClientException exception) {
                        exception.printStackTrace();
                    }

                    return merchantOrders;
                });
    }

    public Merchant findById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SERVICE_ID_NAME + " is not found!"));
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Merchant save(Merchant merchant) {
        if (merchant.getId() != null && merchantRepository.existsById(merchant.getId())) {
            throw new ResourceExistException(SERVICE_ID_NAME + " is exist");
        }
        return merchantRepository.save(merchant);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public MerchantOrders save(MerchantOrders merchantOrders) {
        if (merchantOrders.getId() != null && merchantRepository.existsById(merchantOrders.getId())) {
            throw new ResourceExistException(SERVICE_ID_NAME + " is exist");
        }
        return merchantOrdersRepository.save(merchantOrders);
    }

    @KafkaListener(topics = "orders_to_merchant", groupId = "merchant")
    @Transactional(rollbackFor = RuntimeException.class)
    public void subscribeOrdersToMerchant(String payload) throws JsonProcessingException {
        OrdersDTO ordersDTO = objectMapper.readValue(payload, OrdersDTO.class);

        MerchantOrders merchantOrders = new MerchantOrders();
        merchantOrders.setMerchantID(ordersDTO.getMerchantID());
        merchantOrders.setOrdersID(ordersDTO.getId());
        // Save Merchant Orders
        save(merchantOrders);

    }


}
