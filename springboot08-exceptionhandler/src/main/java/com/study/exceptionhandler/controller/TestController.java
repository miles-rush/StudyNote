package com.study.exceptionhandler.controller;

import com.study.exceptionhandler.constant.Status;
import com.study.exceptionhandler.exception.JsonException;
import com.study.exceptionhandler.exception.PageException;
import com.study.exceptionhandler.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/26
 */
@Controller
@Slf4j
public class TestController {
    @GetMapping("/json")
    @ResponseBody
    public ApiResponse jsonException(@ModelAttribute("user") String user, Date date) {
        log.info("【user】:{}", user);
        log.info("【date】:{}", date);
        throw new JsonException(Status.UNKNOWN_ERROR);
    }

    @GetMapping("/page")
    public ModelAndView pageException() {
        throw new PageException(Status.UNKNOWN_ERROR);
    }
}
