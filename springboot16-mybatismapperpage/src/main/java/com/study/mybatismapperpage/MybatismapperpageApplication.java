package com.study.mybatismapperpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.study.mybatismapperpage.mapper"}) // 注意：这里的 MapperScan 是 tk.mybatis.spring.annotation.MapperScan 这个包下的
public class MybatismapperpageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatismapperpageApplication.class, args);
    }

}
