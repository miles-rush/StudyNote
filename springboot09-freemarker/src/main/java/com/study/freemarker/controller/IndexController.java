package com.study.freemarker.controller;

import cn.hutool.core.util.ObjectUtil;
import com.study.freemarker.model.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/26
 */
@Controller
@Slf4j
public class IndexController {
    @GetMapping(value = {"","/"})
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        User user = (User) request.getSession().getAttribute("user");
        if (ObjectUtil.isNull(user)) {
            mv.setViewName("redirect:/user/login");
        }else {
            mv.setViewName("index");
            mv.addObject(user);
        }

        return mv;
    }
}
