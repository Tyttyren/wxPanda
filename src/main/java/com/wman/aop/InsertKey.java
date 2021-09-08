package com.wman.aop;

import org.apache.http.impl.client.CloseableHttpClient;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@Aspect
public class InsertKey {

    static CloseableHttpClient httpClient;

    @Before("execution(* com.wman.controller.*.*(..))")
    public void setup() throws IOException {
        // 加载商户私钥（privateKey：私钥字符串）


    }

    @After("execution(* com.wman.controller.*.*(..))")
    public void after() throws IOException {
        httpClient.close();
    }
}
