package com.study.aoplog.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/24
 */
@RestController
public class TestController {
    @GetMapping("/test")
    public Dict test(String who) {
        return Dict.create().set("who", StrUtil.isBlank(who) ? "me" : who);
    }
}
