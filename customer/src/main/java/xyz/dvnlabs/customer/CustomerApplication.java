package xyz.dvnlabs.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication(
		scanBasePackages = {
				"xyz.dvnlabs.customer",
				"xyx.dvnlabs.corelib"
		}
)
@EnableDiscoveryClient
public class CustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

}
