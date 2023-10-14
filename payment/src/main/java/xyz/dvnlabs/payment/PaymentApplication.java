package xyz.dvnlabs.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(
		scanBasePackages = {
				"xyz.dvnlabs.payment",
				"xyz.dvnlabs.corelib"
		}
)
@EnableKafka
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
