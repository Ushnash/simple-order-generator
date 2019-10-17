package com.solace.demo.simpleordergenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.solace.camel.component.jms.SolaceJmsFactoryProperties;

@SpringBootApplication
@EnableConfigurationProperties(SolaceJmsFactoryProperties.class)
public class SimpleOrderGenerator {

    /**
     * A main method to start this application.
     */
    public static void main(String[] args) {
        SpringApplication.run(SimpleOrderGenerator.class, args);
    }

}
