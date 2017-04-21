package com.j2clark;

import com.j2clark.model.I2CDisplayFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DisplayFactoryConfig {

    @Bean
    public I2CDisplayFactory i2CDisplayFactory(Environment env) {
        Set<String> pset = new HashSet<>();
        if (env.getActiveProfiles() != null) {
            pset = new HashSet<>(Arrays.asList(env.getActiveProfiles()));
        }

        if (pset.contains("dev")) {
            return new I2CDisplayFactory.MockI2CDisplayFactory();
        } else {
            return new I2CDisplayFactory.I2CDisplayFactoryImpl();
        }
    }


}
