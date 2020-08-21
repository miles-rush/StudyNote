package com.study.properties;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/21
 */
@Configuration
public class PropertiesConfig {
    @Bean
    @ConfigurationPropertiesBinding
    public WeightConverter weightConverter() {
        return new WeightConverter();
    }
}
