package com.wman;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wman.staticClass.necessaryID;
import com.wman.utils.Base64Util;
import com.wman.utils.signUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
class PandaApplicationTests {

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Test
    void contextLoads() {
        System.out.println();
        //将密码加密（编码）
        String encode = bCryptPasswordEncoder.encode("123456");
        System.out.println(bCryptPasswordEncoder.matches("123456", encode));
        System.out.println(encode);
    }

    @Test
    void Test(){
        String encode = bCryptPasswordEncoder.encode("1opIbz5D30gMzE-NcbW03fio6LV4o");
        System.out.println(bCryptPasswordEncoder.matches("1opIbz5D30gMzE-NcbW03fio6LV4o", encode));
        System.out.println(encode);
    }

    @Test
    void Test2(){
        String encode = Base64Util.enCoded("0cb395476864095ce79ff7ed766b1e51");
        System.out.println(Base64Util.enCoded("0cb395476864095ce79ff7ed766b1e51"));
        System.out.println(Base64Util.deCoded(encode));
    }

    @Test
    void testUUID(){
        System.out.println(UUID.randomUUID().toString().replace("-",""));
    }

//    @Test
//    void Test3(){
//        // TODO Auto-generated method stub
//        Process proc;
//        try {
//            proc = Runtime.getRuntime().exec("python E:\\PythonProject\\pythonFIle\\a.py");
//            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
//            in.close();
//            proc.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    void Test4(){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 32; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        System.out.println(sb);

    }

//    @Test
//    void tes() throws UnsupportedEncodingException {
//        byte[] b="周政瀚".getBytes(StandardCharsets.UTF_8);
//        String a=new String(b,"UTF-8");
//        System.out.println(JSONObject.parseObject(a).toString());
//    }


    @Test
    void r(){
        String s="https://api.mch.weixin.qq.com/v3/pay/transactions/app";
        String[] split = s.split("/");
        for (String tr:split) {
            System.out.println(tr);
        }
        StringBuilder sb=new StringBuilder();
        sb.append("/").append(split[3]).append("/")
                .append(split[4]).append("/").append(split[5])
                .append("/").append(split[6]);
        System.out.println(sb.toString());
    }

    @Test
    void t()  {
        //私钥的路劲
        String filename="C:/Users/14285/Desktop/apiclient_key.pem";
        PrivateKey privateKey = null;
        try {
            privateKey = signUtils.getPrivateKey(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(privateKey.toString());
    }



    @Test
    void testn() throws IOException {
        PrivateKey merchantPrivateKey = null;
        try {
            merchantPrivateKey = PemUtil
                    .loadPrivateKey(new FileInputStream(new File("C:/Users/14285/Desktop/apiclient_key.pem")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
        AutoUpdateCertificatesVerifier verifier = null;
        try {
            verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(necessaryID.mchid, new PrivateKeySigner(necessaryID.serialNo, merchantPrivateKey)),necessaryID.apiv3.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 初始化httpClient
        CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(necessaryID.mchid, necessaryID.serialNo, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();
        HttpGet get=new HttpGet("https://api.mch.weixin.qq.com/v3/certificates");
        get.setHeader("Content-type", "application/json");
        get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        get.setHeader("Accept","application/json");
        CloseableHttpResponse execute = httpClient.execute(get);
        for (Header allHeader : execute.getAllHeaders()) {
            System.out.println(allHeader.toString());
            System.out.println("/n");
        }
        String body="";
        JSONObject jsonObject1=new JSONObject();
        HttpEntity entity = execute.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, "UTF-8");
            jsonObject1 = JSONObject.parseObject(body);
        }
        System.out.println(jsonObject1.toString());
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
