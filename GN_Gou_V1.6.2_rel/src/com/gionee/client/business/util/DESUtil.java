package com.gionee.client.business.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;


public class DESUtil {
    
    public static final String deskey = "a3408da8";
    
	/**
	 * 自定义一个key
	 * 
	 * @param string
	 */
	private static Key getKey(String keyRule) {
		Key key = null;
		byte[] keyByte = keyRule.getBytes();
		// 创建一个空的八位数组,默认情况下为0
		byte[] byteTemp = new byte[8];
		// 将用户指定的规则转换成八位数组
		for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
			byteTemp[i] = keyByte[i];
		}
		key = new SecretKeySpec(byteTemp, "DES");
		return key;
	}

	/**
	 * 将指定的数据根据提供的密钥进行加密
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            需要加密的数据
	 * @return byte[] 加密后的数据
	 * @throws util.EncryptException
	 */
	private static byte[] doEncrypt(Key key, byte[] data) {
		// Get a cipher object
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// Encrypt
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// byte[] stringBytes = amalgam.getBytes("UTF8");
			byte[] raw = cipher.doFinal(data);
			// BASE64Encoder encoder = new BASE64Encoder();
			// String base64 = encoder.encode(raw);
			return raw;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    public static byte[] decrypt(byte[] src, String password) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
}

	/**
	 * 得到一个密钥的密码
	 * 
	 * @param key
	 *            密钥
	 * @param cipherMode
	 *            密码的类型
	 * @return Cipher
	 * @throws util.EncryptException
	 *             当加密出现异常情况时,产生异常信息
	 */
	private static Cipher getCipher(Key key, int cipherMode) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(cipherMode, key);
			return cipher;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密
	 * @param myKey
	 * @param text
	 * @return
	 */
	public static String doEncrypt(String myKey, String text) {
		Key key = DESUtil.getKey(myKey);
		byte[] raw = DESUtil.doEncrypt(key, text.getBytes());
		String base64EncryptResult = Base64.encode(raw);
		return base64EncryptResult;
	}

	
	/**
	 * 解密
	 * @param myKey
	 * @param text cOqZgHwmXMKI VB6TRUvTFRbmOHm4MJ0M4DAcaBqiEPUqcUmvS8FYyJPkw3WRSulUEXbezHaEthXOP9n4DxfpdpdCRGhwFKCPuYTuOVpj7NSx1pHDoknGPfSyM60I3QOETMnRqZokhhrLlsWDVSXmeFF U/IqrgDhJaE1Us5cj4Om85z8lgp7soqOCFNddAC41COK2J8ZEufgZ07PI5t7DSvh/ItJknOrKfT0CC1uYLJjecBvb4yNuEESlP1ZZkHD/nvsAlMJLiAc0At5DedI/4r xOyeGX7u4B6yDwzvrU=
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String doDecrypt(String myKey, String text)
			throws Exception {
		if (TextUtils.isEmpty(text)) {
			return ""; 
		}
		if (text.contains("http://")) {
			return text;
		}
//		LogUtil.d("text:"+text);
		if (text.contains(" ")) {
			text=text.replaceAll(" ", "+"); //密文里面将空格替换
		}
//		Key key = DESUtil.getKey(myKey);
		byte[] decodeStr = Base64.decode(text);
		byte[] decrypt = DESUtil.decrypt(decodeStr,myKey);
		return decrypt == null ? null:new String(decrypt); 
	}
	/**
	 * 解密
	 * @param myKey
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String doDecrypt(String text)
			throws UnsupportedEncodingException {
		
		if (TextUtils.isEmpty(text)) {
			return ""; 
		}
		if (text.contains("http://")) {
			return text;
		}
		if (text.contains(" ")) {
			text=text.replaceAll(" ", "+"); //密文里面将空格替换
		}
		
		Key key = DESUtil.getKey(deskey);
		byte[] decodeStr = Base64.decode(text);
		byte[] decrypt=null;
        try {
            decrypt = DESUtil.decrypt(decodeStr,deskey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return decrypt == null ? null:new String(decrypt); 
	}
    public static byte[] desCrypto(byte[] datasource, String password) {            
        try{
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        //创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        //Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");
        //用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
        //现在，获取数据并加密
        //正式执行加密操作
        return cipher.doFinal(datasource);
        }catch(Throwable e){
                e.printStackTrace();
        }
        return null;
}
	public static void main(String[] args) {
        //待加密内容
        String str = "测试内容";
        //密码，长度要是8的倍数
        String password = "12345678";
        byte[] result = desCrypto(str.getBytes(),password);
        System.out.println("加密后内容为："+new String(result));
        
        //直接将如上内容解密
        try {
                byte[] decryResult = decrypt(result, password);
                System.out.println("加密后内容为："+new String(decryResult));
        } catch (Exception e1) {
                e1.printStackTrace();
        }
    }
}
