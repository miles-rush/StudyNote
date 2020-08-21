package com.study.properties.property;


import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/21
 */
@Data
@ConfigurationProperties(prefix = "developer")
@Component
@Validated
public class DeveloperProperty {
    @NotNull
    private String name;
    @NotEmpty
    private String website;
    private String qq;
    private String phoneNumber;

    private Weight weight;
}
