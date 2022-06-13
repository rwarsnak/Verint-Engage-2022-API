package com.verint.engageapidemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EngageApiDemoApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(EngageApiDemoApplication.class);
    public static void main(String[] args) {

        SpringApplication.run(EngageApiDemoApplication.class, args);
        LOGGER.info("I'm alive");
    }

}
