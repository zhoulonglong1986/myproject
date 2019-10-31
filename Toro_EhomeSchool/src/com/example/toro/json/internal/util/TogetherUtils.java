package com.example.toro.json.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.example.toroapi.ApiException;
import com.example.toroapi.Constants;
import com.example.toroapi.TogetherResponse;
import com.example.toro.json.internal.parser.json.ObjectJsonParser;
import com.example.toro.json.internal.parser.json.util.JSONReader;
import com.example.toro.json.internal.parser.json.util.JSONValidatingReader;

/**
 * ϵͳ�����ࡣ
 *
 * @author carver.gu
 * @since 1.0, Sep 12, 2009
 */
public abstract class TogetherUtils {

    private static String localIp;

    private TogetherUtils() {
    }

    /**
     * ��TOP����ǩ����
     *
     * @param requestHolder �����ַ��͵�TOP�������
     * @param secret ǩ����Կ
     * @return ǩ��
     * @throws IOException
     */
    public static String signTopRequest(RequestParametersHolder requestHolder, String secret) throws IOException {
        // ��һ�����������Ƿ��Ѿ�����
        Map<String, String> params = requestHolder.getAllParams();
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // �ڶ����������в������Ͳ���ֵ����һ��
        StringBuilder query = new StringBuilder(secret);
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.areNotEmpty(key, value)) {
                query.append(key).append(value);
            }
        }

        // ��������ʹ��MD5����
        byte[] bytes = encryptMD5(query.toString());

        // ���Ĳ����Ѷ�����ת��Ϊ��д��ʮ������
        return byte2hex(bytes);
    }

    /**
     * ��TOP����ǩ����
     *
     * @param requestHolder �����ַ��͵�TOP�������
     * @param secret ǩ����Կ
     * @param isHmac �Ƿ�ΪHMAC��ʽ����
     * @return ǩ��
     * @throws IOException
     */
    public static String signTopRequestNew(RequestParametersHolder requestHolder, String secret, boolean isHmac) throws IOException {
        return signTopRequestNew(requestHolder.getAllParams(), secret, isHmac);
    }

    public static String signTopRequestNew(Map<String, String> params, String secret, boolean isHmac) throws IOException {
        // ��һ�����������Ƿ��Ѿ�����
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // �ڶ����������в������Ͳ���ֵ����һ��
        StringBuilder query = new StringBuilder();
        if (!isHmac) {
            query.append(secret);
        }
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.areNotEmpty(key, value)) {
                query.append(key).append(value);
            }
        }

        // ��������ʹ��MD5/HMAC����
        byte[] bytes;
        if (isHmac) {
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            query.append(secret);
            bytes = encryptMD5(query.toString());
        }

        // ���Ĳ����Ѷ�����ת��Ϊ��д��ʮ������
        return byte2hex(bytes);
    }

    private static byte[] encryptHMAC(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(Constants.CHARSET_UTF8), "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(Constants.CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            String msg = getStringFromException(gse);
            throw new IOException(msg);
        }
        return bytes;
    }

    private static String getStringFromException(Throwable e) {
        String result = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos);
        e.printStackTrace(ps);
        try {
            result = bos.toString(Constants.CHARSET_UTF8);
        } catch (IOException ioe) {
        }
        return result;
    }

    private static byte[] encryptMD5(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data.getBytes(Constants.CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            String msg = getStringFromException(gse);
            throw new IOException(msg);
        }
        return bytes;
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    /**
     * ��֤TOP�ص���ַ��ǩ���Ƿ�Ϸ���Ҫ�����в�����Ϊ��URL������ġ�
     *
     * @param topParams TOP˽�в�����δ��BASE64���ܣ�
     * @param topSession TOP˽�лỰ��
     * @param topSign TOP�ص�ǩ��
     * @param appKey Ӧ�ù�Կ
     * @param appSecret Ӧ����Կ
     * @return ��֤�ɹ�����true�����򷵻�false
     * @throws IOException
     */
    public static boolean verifyTopResponse(String topParams, String topSession, String topSign,
            String appKey, String appSecret) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append(appKey).append(topParams).append(topSession).append(appSecret);
        byte[] bytes = encryptMD5(result.toString());
        return Base64.encodeToString(bytes, false).equals(topSign);
    }

    /**
     * ����TOP�ص�����Ϊ��ֵ�ԡ�(����container�ص�ʱ�����Ľ���)
     *
     * @param topParams ����BASE64������ַ���
     * @return ��ֵ��
     * @throws IOException
     */
    public static Map<String, String> decodeTopParams(String topParams) throws IOException {
        return decodeTopParams(topParams, Constants.CHARSET_GBK);
    }

    /**
     * ����TOP�ص�����Ϊ��ֵ�ԡ�(����container�ص�ʱ�����Ľ���)
     *
     * @param topParams ����BASE64������ַ���
     * @param charset �ַ�������
     * @return
     * @throws IOException
     */
    public static Map<String, String> decodeTopParams(String topParams, String charset) throws IOException {
        if (StringUtils.isEmpty(topParams)) {
            return null;
        }

        WebUtils webUtils=new WebUtils();

        byte[] buffer = Base64.decode(webUtils.decode(topParams).getBytes());
        String originTopParams = new String(buffer, charset);

        return webUtils.splitUrlQuery(originTopParams);
    }

    /**
     * ����Ӧ�ñ�Ż�ȡ��ʽ����WEBӦ��SessionKey�ĵ�ַ��
     *
     * @param appKey Ӧ�ñ��
     * @return ��ַ
     */
    public static String getProductWebSessionUrl(String appKey) {
        StringBuilder url = new StringBuilder(Constants.PRODUCT_CONTAINER_URL);
        url.append("?appkey=").append(appKey);
        return url.toString();
    }

    /**
     * ����Ӧ�ñ�Ż�ȡ��ʽ�����ͻ���Ӧ��SessionKey�ĵ�ַ��
     *
     * @param authCode ��Ȩ��
     * @return ��ַ
     */
    public static String getProductClientSessionUrl(String authCode) {
        StringBuilder url = new StringBuilder(Constants.PRODUCT_CONTAINER_URL);
        url.append("?authcode=").append(authCode);
        return url.toString();
    }

    /**
     * ��ȡ�ļ�����ʵ��׺����Ŀǰֻ֧��JPG, GIF, PNG, BMP����ͼƬ�ļ���
     *
     * @param bytes �ļ��ֽ���
     * @return JPG, GIF, PNG or null
     */
    public static String getFileSuffix(byte[] bytes) {
        if (bytes == null || bytes.length < 10) {
            return null;
        }

        if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            return "GIF";
        } else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
            return "PNG";
        } else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I' && bytes[9] == 'F') {
            return "JPG";
        } else if (bytes[0] == 'B' && bytes[1] == 'M') {
            return "BMP";
        } else {
            return null;
        }
    }

    /**
     * ��ȡ�ļ�����ʵý�����͡�Ŀǰֻ֧��JPG, GIF, PNG, BMP����ͼƬ�ļ���
     *
     * @param bytes �ļ��ֽ���
     * @return ý������(MEME-TYPE)
     */
    public static String getMimeType(byte[] bytes) {
        String suffix = getFileSuffix(bytes);
        String mimeType;

        if ("JPG".equals(suffix)) {
            mimeType = "image/jpeg";
        } else if ("GIF".equals(suffix)) {
            mimeType = "image/gif";
        } else if ("PNG".equals(suffix)) {
            mimeType = "image/png";
        } else if ("BMP".equals(suffix)) {
            mimeType = "image/bmp";
        } else {
            mimeType = "application/octet-stream";
        }

        return mimeType;
    }

    /**
     * ����ֵ���ֵΪ�յ��
     *
     * @param <V> ����
     * @param map ��������ֵ�
     * @return �������ֵ�
     */
    public static <V> Map<String, V> cleanupMap(Map<String, V> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, V> result = new HashMap<String, V>(map.size());
        Set<Entry<String, V>> entries = map.entrySet();

        for (Entry<String, V> entry : entries) {
            if (entry.getValue() != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * ��JSON�ַ���ת��ΪMap�ṹ��
     *
     * @param body JSON�ַ���
     * @return Map�ṹ
     */
    public static Map<?, ?> parseJson(String body) {
        JSONReader jr = new JSONValidatingReader();
        Object obj = jr.read(body);
        if (obj instanceof Map<?, ?>) {
            return (Map<?, ?>) obj;
        } else {
            return null;
        }
    }

    /**
     * ��JSON�ַ�������Ϊ����ṹ��
     *
     * @param <T> API��Ӧ����
     * @param json JSON�ַ���
     * @param clazz API��Ӧ��
     * @return API��Ӧ����
     */
    public static <T extends TogetherResponse> T parseResponse(String json, Class<T> clazz) throws ApiException {
        ObjectJsonParser<T> parser = new ObjectJsonParser<T>(clazz);
        T rsp = parser.parse(json);
        rsp.setBody(json);
        return rsp;
    }
}
