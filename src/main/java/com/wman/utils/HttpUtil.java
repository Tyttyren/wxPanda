package com.wman.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.HttpUrl;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

/**
 * 与微信小程序连同工具类
 * @author ajie
 * @createTime 2021年06月13日 13:47:00
 */
@Component
public class HttpUtil {

    private static final String authorition="WECHATPAY2-SHA256-RSA2048,";
    /**
     * 发送请求获取信息
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String getResponse(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key));
                }
            }
            URI uri = builder.build();
            HttpGet get = new HttpGet(uri);
            //接受get请求
            httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
            httpClient.close();
        }
        return "";
    }

    public static JSONObject  sendHttpGETRequest(StringBuilder url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();//构建一个Client
        HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
        HttpResponse response = null;//提交GET请求
        response = client.execute(get);
        HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
        String content = EntityUtils.toString(result);
        //把信息封装为json
        JSONObject res = JSONObject.parseObject(content);
        return res;
    }

    public static JSONObject  sendHttpGETRequestW(StringBuilder url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();//构建一个Client
        HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
        get.setHeader("Content-type", "application/json");
        get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        get.setHeader("Accept","application/json");
        String sign = new signUtils().getToken("Get", HttpUrl.parse("https://api.mch.weixin.qq.com/v3/certificates"),"\n");
        StringBuilder authoritionHead = new StringBuilder();
        authoritionHead.append(authorition).append(sign);
        get.setHeader("Authorition",authoritionHead.toString());
        HttpResponse response = null;//提交GET请求
        response = client.execute(get);
        HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
        String content = EntityUtils.toString(result);
        //把信息封装为json
        JSONObject res = JSONObject.parseObject(content);
        return res;
    }

    public static JSONObject sendHttpPOSTRequest(StringBuilder url,JSONObject jsonObject) throws IOException {
        String body = "";
        JSONObject jsonObject1 = new JSONObject();

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(String.valueOf(url));

        //装填参数
        StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);
        System.out.println("请求地址："+url);
//        System.out.println("请求参数："+nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
//        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        httpPost.setHeader("Accept","application/json");
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, "UTF-8");
            jsonObject1 = JSONObject.parseObject(body);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return jsonObject1;
    }


    public static JSONObject sendHttpPOSTRequestWheChat(StringBuilder url,JSONObject jsonObject) throws IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String body = "";
        JSONObject jsonObject1 = new JSONObject();

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(String.valueOf(url));

        //装填参数
        StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);
        System.out.println("请求地址："+url);
//        System.out.println("请求参数："+nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
//        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        Long timeStap=System.currentTimeMillis()/1000;
//        String timeStapString = timeStap.toString();
//        String serialNo="7068FAE9E99BA8900A48D962438C337909088196";
        httpPost.setHeader("Accept","application/json");
        //处理协议
        String sign = new signUtils().getToken("Post", HttpUrl.parse("https://api.mch.weixin.qq.com/v3/pay/partner/transactions/jsapi"), jsonObject.toString());

        StringBuilder authoritionHead = new StringBuilder();
        authoritionHead.append(authorition)
//                .append("signature=\"")
                .append(sign);
//                .append(",timestamp=\"")
//                .append(timeStapString)
//                .append("\",serial_no=\"")
//                .append(serialNo)
//                .append("\"");
//        System.out.println(authoritionHead.toString());
        httpPost.setHeader("Authorition",authoritionHead.toString());
//        httpPost.setHeader("Authorition",authorition+"time_stap="+timeStapString+",serial_no="
//                +serialNo+",signature="+sign);
        for (Header allHeader : httpPost.getAllHeaders()) {
            System.out.println(allHeader.toString());
        }
        //输出请求体
        System.out.println(httpPost.getEntity().toString());
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, "UTF-8");
            jsonObject1 = JSONObject.parseObject(body);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return jsonObject1;

    }


}
