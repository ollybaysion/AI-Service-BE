package com.dateguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DateguideApplication {

    public static void main(String[] args) {
        SpringApplication.run(DateguideApplication.class, args);
    }

}
