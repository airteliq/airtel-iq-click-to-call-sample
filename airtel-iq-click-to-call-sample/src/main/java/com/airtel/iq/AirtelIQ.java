package com.airtel.iq;

import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = { LoggerConfig.class})
public class AirtelIQ {

	public static void main(String[] args) {
		   SpringApplication.run(AirtelIQ.class, args);
	}

}
