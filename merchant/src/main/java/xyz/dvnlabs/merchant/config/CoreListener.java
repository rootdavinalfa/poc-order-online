package xyz.dvnlabs.merchant.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CoreListener {

    private final KafkaListener kafkaListener;

    public CoreListener(KafkaListener kafkaListener) {
        this.kafkaListener = kafkaListener;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        kafkaListener.seekToStart();
    }
}
