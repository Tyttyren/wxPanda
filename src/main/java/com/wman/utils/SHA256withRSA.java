package com.wman.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;

/**
 * 私钥签名，公钥验签,基于SHA256withRSA的签名
 * @author jinzhm
 *
 */
public class SHA256withRSA {
    private static String CHARSET_ENCODING = "UTF-8";
    private static String ALGORITHM = "SHA256withRSA";

    /**
     * 签名
     * @param srcData
     * @param privateKeyPath
     * @param privateKeyPwd
     * @return
     */
    public static String sign(String srcData, String privateKeyPath, String privateKeyPwd){
        if(srcData==null || privateKeyPath==null || privateKeyPwd==null){
            return "";
        }
        try {
            // 获取证书的私钥
            PrivateKey key = readPrivate(privateKeyPath, privateKeyPwd);
            // 进行签名服务
            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initSign(key);
            signature.update(srcData.getBytes(CHARSET_ENCODING));
            byte[] signedData = signature.sign();
            return Base64.getEncoder().encodeToString(signedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 验签
     * @param srcData
     * @param signedData
     * @param publicKeyPath
     * @return
     */
    public static boolean verify(String srcData, String signedData, String publicKeyPath){
        if(srcData==null || signedData==null || publicKeyPath==null){
            return false;
        }
        try {
            PublicKey publicKey = readPublic(publicKeyPath);
            Signature sign = Signature.getInstance(ALGORITHM);
            sign.initVerify(publicKey);
            sign.update(srcData.getBytes(CHARSET_ENCODING));
            return sign.verify(Base64.getDecoder().decode(signedData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 读取公钥
     * @param publicKeyPath
     * @return
     */
    private static PublicKey readPublic(String publicKeyPath){
        if(publicKeyPath==null){
            return null;
        }
        PublicKey pk = null;
        FileInputStream bais = null;
        try {
            CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
            bais = new FileInputStream(publicKeyPath);
            X509Certificate cert = (X509Certificate)certificatefactory.generateCertificate(bais);
            pk = cert.getPublicKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally{
            if(bais != null){
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pk;
    }

    /**
     * 读取私钥
     * @param
     * @return
     */
    private static PrivateKey readPrivate(String privateKeyPath, String privateKeyPwd){
        if(privateKeyPath==null || privateKeyPwd==null){
            return null;
        }
        InputStream stream = null;
        try {
            // 获取JKS 服务器私有证书的私钥，取得标准的JKS的 KeyStore实例
            KeyStore store = KeyStore.getInstance("JKS");
            stream = new FileInputStream(new File(privateKeyPath));
            // jks文件密码，根据实际情况修改
            store.load(stream, privateKeyPwd.toCharArray());
            // 获取jks证书别名
            Enumeration en = store.aliases();
            String pName = null;
            while (en.hasMoreElements()) {
                String n = (String) en.nextElement();
                if (store.isKeyEntry(n)) {
                    pName = n;
                }
            }
            // 获取证书的私钥
            PrivateKey key = (PrivateKey) store.getKey(pName,
                    privateKeyPwd.toCharArray());
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
