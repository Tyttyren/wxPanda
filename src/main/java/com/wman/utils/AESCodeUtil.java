package com.wman.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;

public class AESCodeUtil {
//    // 算法名
//    public static final String KEY_NAME = "AES";
//    // 加解密算法/模式/填充方式
//    // ECB模式只用密钥即可对数据进行加密解密，CBC模式需要添加一个iv
//    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
//
//    /**
//     * 微信 数据解密<br/>
//     * 对称解密使用的算法为 AES-128-CBC，数据采用PKCS#7填充<br/>
//     * 对称解密的目标密文:encrypted=Base64_Decode(encryptData)<br/>
//     * 对称解密秘钥:key = Base64_Decode(session_key),aeskey是16字节<br/>
//     * 对称解密算法初始向量:iv = Base64_Decode(iv),同样是16字节<br/>
//     *
//     * @param encrypted   目标密文
//     * @param session_key 会话ID
//     * @param iv          加密算法的初始向量
//     */
//    public static String wxDecrypt(String encrypted, String session_key, String iv) {
//        String json = null;
//        byte[] encrypted64 = Base64.decodeBase64(encrypted);
//        byte[] key64 = Base64.decodeBase64(session_key);
//        byte[] iv64 = Base64.decodeBase64(iv);
//        byte[] data;
//        try {
//            init();
//            json = new String(decrypt(encrypted64, key64, generateIV(iv64)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return json;
//    }
//    /**
//     * 初始化密钥
//     */
//    public static void init() throws Exception {
//        Security.addProvider(new BouncyCastleProvider());
//        KeyGenerator.getInstance(KEY_NAME).init(128);
//    }
//
//    /**
//     * 生成iv
//     */
//    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
//        // iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
//        // Arrays.fill(iv, (byte) 0x00);
//        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_NAME);
//        params.init(new IvParameterSpec(iv));
//        return params;
//    }
//
//    /**
//     * 生成解密
//     */
//    public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv)
//            throws Exception {
//        Key key = new SecretKeySpec(keyBytes, KEY_NAME);
//        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//        // 设置为解密模式
//        cipher.init(Cipher.DECRYPT_MODE, key, iv);
//        return cipher.doFinal(encryptedData);
//    }


    public static JSONObject getUserInfo(String encryptedData,String sessionkey,String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decodeBase64(sessionkey);
        // 偏移量
        byte[] ivByte = Base64.decodeBase64(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JSONObject.parseObject(result);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidParameterSpecException |
                IllegalBlockSizeException |
                BadPaddingException |
                UnsupportedEncodingException |
                InvalidKeyException |
                InvalidAlgorithmParameterException |
                NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String AESEncode(String encodeRules,String content){
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            //3.产生原始对称密钥
            SecretKey original_key=keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte [] byte_encode=content.getBytes("utf-8");
            //9.根据密码器的初始化方式--加密：将数据加密
            byte [] byte_AES=cipher.doFinal(byte_encode);
            //10.将加密后的数据转换为字符串
            //这里用Base64Encoder中会找不到包
            //解决办法：
            //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
            String AES_encode=new String(new BASE64Encoder().encode(byte_AES));
            //11.将字符串返回
            return AES_encode;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //如果有错就返加nulll
        return null;
    }
}
