package com.study.adminclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/24
 */
@RestController
public class IndexController {
    @GetMapping(value = {"", "/"})
    public String index() {
        return "this is a Spring Boot Admin Client.";
    }
}
