package com.wman;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @ClassName PandaApplication
 * @Author wman
 * @Date 2021/9/3
 */
@SpringBootApplication
@MapperScan("com.wman.mapper")
@EnableSwagger2
public class PandaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PandaApplication.class, args);
    }
}
