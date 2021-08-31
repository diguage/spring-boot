package com.diguage.truman;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Truman 应用
 *
 * @author D瓜哥, https://www.diguage.com/
 * @since 2021-08-04 08:50:57
 */
@Configuration
@EnableConfigurationProperties
@SpringBootApplication
public class TrumanApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TrumanApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
