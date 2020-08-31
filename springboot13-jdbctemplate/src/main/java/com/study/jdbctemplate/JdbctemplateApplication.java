package com.study.jdbctemplate;

import com.study.jdbctemplate.entity.TestClass;
import com.study.jdbctemplate.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

@SpringBootApplication
@Slf4j
public class JdbctemplateApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(JdbctemplateApplication.class, args);

        Class uClass = User.class;
        log.info("【类名】:{}",uClass.getName());
        Method[] methods = uClass.getMethods();
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            Class returnType = method.getReturnType();
            log.info("【访问权限】:{},【返回值】:{}", Modifier.toString(modifiers), returnType.getName());
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                log.info("【方法参数】:{},{}",parameter.getType().getName(), parameter.getName());
            }
        }

        TestClass testClass = new TestClass();
        Class tClass = testClass.getClass();

        Method privateMethod = tClass.getDeclaredMethod("privateMethod", String.class, int.class);
        if (privateMethod != null) {
            privateMethod.setAccessible(true);
            privateMethod.invoke(testClass, "Java Reflect", 666);
        }

        Field privateField = tClass.getDeclaredField("MSG");
        if (privateField != null) {
            privateField.setAccessible(true);
            log.info("Before Modify:MSG = {}", testClass.getMsg());
            privateField.set(testClass, "Modified");
            log.info("After Modify:MSG = {}", testClass.getMsg());
        }
    }

}
