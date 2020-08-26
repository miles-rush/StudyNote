package com.study.exceptionhandler.handler;

import com.study.exceptionhandler.Tool.DateConverter;
import com.study.exceptionhandler.exception.JsonException;
import com.study.exceptionhandler.exception.PageException;
import com.study.exceptionhandler.model.ApiResponse;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/26
 */
@ControllerAdvice
@Slf4j
public class DemoExceptionHandler {
    private static final String DEFAULT_ERROR_VIEW = "error";
    /**
     * 统一 json 异常处理
     *
     * @param exception JsonException
     * @return 统一返回 json 格式
     */
    @ExceptionHandler(value = JsonException.class)
    @ResponseBody
    public ApiResponse jsonErrorHandler(JsonException exception) {
        log.error("【JsonException】:{}", exception.getMessage());
        return ApiResponse.ofException(exception);
    }

    /**
     * 统一 页面 异常处理
     *
     * @param exception PageException
     * @return 统一跳转到异常页面
     */
    @ExceptionHandler(value = PageException.class)
    public ModelAndView pageErrorHandler(PageException exception) {
        log.error("【DemoPageException】:{}", exception.getMessage());
        ModelAndView view = new ModelAndView();
        view.addObject("message", exception.getMessage());
        view.setViewName(DEFAULT_ERROR_VIEW);
        return view;
    }

    @ModelAttribute
    public void addMyAttribute(Model model) {
        model.addAttribute("user", "miles");
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
//        GenericConversionService genericConversionService = (GenericConversionService) binder.getConversionService();
//        if (genericConversionService != null) {
//            genericConversionService.addConverter(new DateConverter());
//        }

        binder.registerCustomEditor(String.class,
                new StringTrimmerEditor(true));

        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), false));
    }
}
