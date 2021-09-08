package com.wman.utils;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wman.staticClass.necessaryID;
import okhttp3.HttpUrl;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

//基于aes gmc的加密
public class signUtils {
    private final static String mchID = "1317716401";

    private final   static String serial_no="7068FAE9E99BA8900A48D962438C337909088196";

    //生成签名
    public static String getSign(String method, String url, String body) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String schema = "WECHATPAY2-SHA256-RSA2048";
        String nonceStr = "c5ac7061fccab6bf3e254dcf98995b8c";
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
//        Base64Util.enCoded(message);
//        return "mchid=\"" + mchID + "\","
//                + "nonce_str=\"" + nonceStr + "\","
//                + "timestamp=\"" + timestamp + "\","
//                + "serial_no=\"" + serial_no + "\","
//                + "signature=\"" + signature + "\"";

        return AESCodeUtil.AESEncode("d9ae99116deb95eeac7ff85544f0e54e",message);

    }

    //通过string 的url来写签名
    String getToken(String method, String url, String body) {
        String nonceStr = "c5ac7061fccab6bf3e254dcf98995b8c";
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = null;
        try {
            signature = sign(message.getBytes(StandardCharsets.UTF_8));
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return "mchid=\"" + mchID + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + serial_no + "\","
                + "signature=\"" + signature + "\"";
    }

    //给签名加密
    String sign(byte[] message) throws SignatureException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(getPrivateKey("C:/Users/14285/Desktop/apiclient_key.pem"));
        sign.update(message);
        return Base64.getEncoder().encodeToString(sign.sign());
    }

    //通过string的url来创建message
   public static String buildMessage(String method, String url, long timestamp, String nonceStr, String body) {
        url=handUrl(url);
        return method + "\n"
                + url + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }

    //处理stirng类型的url
     public static String handUrl(String url){
         String[] split = url.split("/");
         StringBuilder sb=new StringBuilder();
         sb.append("/").append(split[3]).append("/")
                 .append(split[4]).append("/").append(split[5])
                 .append("/").append(split[6]);
         return sb.toString();
     }

   //直接传入httpurl
    String getToken(String method, HttpUrl url, String body) {
        String nonceStr = "c5ac7061fccab6bf3e254dcf98995b8c";
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = null;
        try {
            signature = sign(message.getBytes("utf-8"));
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return "mchid=\"" + mchID + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + serial_no + "\","
                + "signature=\"" + signature + "\"";
    }

    //用httpurl生成信息
    String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }



     //从电脑中获取私钥
    public static PrivateKey getPrivateKey(String filename) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(filename)), "utf-8");
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }


    public static JSONObject getWeChatURL(String url,String method,JSONObject js) throws IOException {
        try {
        PrivateKey merchantPrivateKey = null;
            merchantPrivateKey = PemUtil.loadPrivateKey(
                            new FileInputStream(new File(necessaryID.fileKey)));


        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
        AutoUpdateCertificatesVerifier verifier = null;

            verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(necessaryID.sp_mchid, new PrivateKeySigner(necessaryID.serialNo, merchantPrivateKey)),necessaryID.apiv3.getBytes("utf-8"));

         CloseableHttpResponse execute;
        // 初始化httpClient
        CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(necessaryID.sp_mchid, necessaryID.serialNo, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();
           if(method.equals("Get")){//使用get方法向微信服务器请求
            HttpGet get=new HttpGet(url);//设置url
            get.setHeader("Content-type", "application/json");//添加请求头
            get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            get.setHeader("Accept","application/json");
            execute = httpClient.execute(get);
        }else {
               HttpPost post=new HttpPost(url);
               post.setHeader("Content-type", "application/json");
               post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
               post.setHeader("Accept","application/json");
               StringEntity s = new StringEntity(js.toString(), "utf-8");
               post.setEntity(s);
               execute=httpClient.execute(post);
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
            httpClient.close();
            return jsonObject1;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    //随机数生成
    public static String redomNumber(){
        UUID uuid= UUID.randomUUID();
        String replace = uuid.toString().replace("-", "");
        return replace;
    }

}
