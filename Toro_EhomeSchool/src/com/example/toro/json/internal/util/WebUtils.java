package com.example.toro.json.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.example.toro.bo.ReturnException;
import com.example.toroapi.Constants;
import com.example.toroapi.FileItem;
import com.example.toroapi.AppConstants;
import com.example.toroapi.Secret;

/**
 * ���繤���ࡣ
 *
 * @author carver.gu
 * @since 1.0, Sep 12, 2009
 */

public  class WebUtils {

    private static final String DEFAULT_CHARSET = Constants.CHARSET_UTF8;
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
    
    private  ThreadLocal<Integer> postRetry = new ThreadLocal<Integer>() {
    	public Integer initialValue() { // ��ʼ����Ĭ���Ƿ��� null
    		return 0;
    	}
    };

    private  class DefaultTrustManager implements X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }


    /**
     * ִ��HTTP POST����
     *
     * @param url �����ַ
     * @param params �������
     * @return ��Ӧ�ַ���
     * @throws IOException
     */
    public  String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout)
            throws Exception {
        return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }

    /**
     * ִ��HTTP POST����
     *
     * @param url �����ַ
     * @param params �������
     * @param charset �ַ�������UTF-8, GBK, GB2312
     * @return ��Ӧ�ַ���
     * @throws IOException
     */
    public  String doPost(String url, Map<String, String> params, String charset, int connectTimeout,
            int readTimeout) throws Exception {
        return doPost(url, params, charset, connectTimeout, readTimeout, null);
    }

    public  String doPost(String url, Map<String, String> params, String charset, int connectTimeout,int readTimeout, Map<String, String> headerMap) throws Exception {
     
    	String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        String query = buildQuery(params, charset);
        
        System.out.println("======================"+query);
        if(AppConstants.Is_encrypted){
            query =Secret.encode(AppConstants.KEY,query).toString();
        }

        byte[] content = {};
        if (query != null) {
            content = query.getBytes(charset);
        }
        return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
    }
    

    /**
     * ִ��HTTP POST����
     * @param url �����ַ
     * @param ctype ��������
     * @param content �����ֽ�����
     * @return ��Ӧ�ַ���
     * @throws IOException
     */
    @Deprecated
    public  String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
            throws Exception {
        return _doPost(url, ctype, content, connectTimeout, readTimeout, null);
    }

    private  String _doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout,Map<String, String> headerMap) throws Exception {
        
    	HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
        	try {
                conn = getConnection(new URL(url), METHOD_POST, ctype, headerMap);
               // conn.setRequestProperty("User-agent","android");
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);

              } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                TogetherLogger.logCommError(e, url, map.get("app_key"), map.get("method"), content);
                if(conn == null && postRetry.get() < 3) {
              	  postRetry.set(postRetry.get() + 1);
              	  _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
                }
                
                throw e;
            }
            try {
                out = conn.getOutputStream();
                out.write(content);
                rsp = getResponseAsString(conn);
            } catch (Exception e) {
            	System.out.println("================"+e);
                Map<String, String> map = getParamsFromUrl(url);
                TogetherLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), content);
                throw e;
            }

        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
//        // System.out.println("--------------responseCode:"+conn.getResponseCode());

        return rsp;
    }

    /**
     * ִ�д��ļ��ϴ���HTTP POST����
     *
     * @param url �����ַ
     * @param textParams �ı��������
     * @param fileParams �ļ��������
     * @return ��Ӧ�ַ���
     * @throws IOException
     */
    public  String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams,
            int connectTimeout, int readTimeout) throws Exception {
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
        } else {
            return doPost(url, params, fileParams, DEFAULT_CHARSET, connectTimeout, readTimeout);
        }
    }

    public  String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams,
            String charset, int connectTimeout, int readTimeout) throws Exception {
        return doPost(url, params, fileParams, charset, connectTimeout, readTimeout, null);
    }

    /**
     * ִ�д��ļ��ϴ���HTTP POST����
     *
     * @param url �����ַ
     * @param textParams �ı��������
     * @param fileParams �ļ��������
     * @param charset �ַ�������UTF-8, GBK, GB2312
     * @param headerMap ��Ҫ���ݵ�headerͷ������Ϊ��
     * @return ��Ӧ�ַ���
     * @throws IOException
     */
    public  String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams,
            String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap) throws Exception {
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, params, charset, connectTimeout, readTimeout, headerMap);
        } else {
            return _doPostWithFile(url, params, fileParams, charset, connectTimeout, readTimeout, headerMap);
        }

    }

    private  String _doPostWithFile(String url, Map<String, String> params, Map<String, FileItem> fileParams,
            String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap) throws Exception {
        String boundary = System.currentTimeMillis() + ""; // ����ָ���
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
        	try {
                String ctype = "multipart/form-data;charset=" + charset + ";boundary=" + boundary;
                conn = getConnection(new URL(url), METHOD_POST, ctype, headerMap);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                TogetherLogger.logCommError(e, url, map.get("app_key"), map.get("method"), params);
                if(conn == null && postRetry.get() < 3) {
              	  postRetry.set(postRetry.get() + 1);
              	  _doPostWithFile(url, params, fileParams, charset, connectTimeout, readTimeout, headerMap);
                }
                throw e;
            }
            try {
                out = conn.getOutputStream();

                byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes(charset);

                // ��װ�ı��������
                Set<Entry<String, String>> textEntrySet = params.entrySet();
                for (Entry<String, String> textEntry : textEntrySet) {
                    byte[] textBytes = getTextEntry(textEntry.getKey(), textEntry.getValue(), charset);
                    out.write(entryBoundaryBytes);
                    out.write(textBytes);
                }

                // ��װ�ļ��������
                Set<Entry<String, FileItem>> fileEntrySet = fileParams.entrySet();
                for (Entry<String, FileItem> fileEntry : fileEntrySet) {
                    FileItem fileItem = fileEntry.getValue();
                    if (fileItem.getContent() == null) {
                        continue;
                    }
                    byte[] fileBytes = getFileEntry(fileEntry.getKey(), fileItem.getFileName(), fileItem.getMimeType(), charset);
                    out.write(entryBoundaryBytes);
                    out.write(fileBytes);
                    out.write(fileItem.getContent());
                }

                // ������������־
                byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes(charset);
                out.write(endBoundaryBytes);
                
                rsp = getResponseAsString(conn);
            } catch (Exception e) {
                Map<String, String> map = getParamsFromUrl(url);
                TogetherLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), params);
                throw e;
            }
        } finally {
        	
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    private  byte[] getTextEntry(String fieldName, String fieldValue, String charset) throws IOException {
        StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
        entry.append(fieldValue);
        return entry.toString().getBytes(charset);
    }

    private  byte[] getFileEntry(String fieldName, String fileName, String mimeType, String charset)
            throws IOException {
        StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\";filename=\"");
        entry.append(fileName);
        entry.append("\"\r\nContent-Type:");
        entry.append(mimeType);
        entry.append("\r\n\r\n");
        return entry.toString().getBytes(charset);
    }

    /**
     * ִ��HTTP GET����
     *
     * @param url �����ַ
     * @param params �������
     * @return ��Ӧ�ַ���
     * @throws IOException
     */
    public  String doGet(String url, Map<String, String> params) throws Exception {
        return doGet(url, params, DEFAULT_CHARSET);
    }

    /**
     * ִ��HTTP GET����
     *
     * @param url �����ַ
     * @param params �������
     * @param charset �ַ�������UTF-8, GBK, GB2312
     * @return ��Ӧ�ַ���
     * @throws IOException
     */
    public  String doGet(String url, Map<String, String> params, String charset) throws Exception {
        HttpURLConnection conn = null;
        String rsp = null;

        try {
            String ctype = "application/x-www-form-urlencoded;charset=" + charset;
            String query = buildQuery(params, charset);
            try {
                conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype, null);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                TogetherLogger.logCommError(e, url, map.get("app_key"), map.get("method"), params);
                throw e;
            }

            try {
                rsp = getResponseAsString(conn);
            } catch (Exception e) {
                Map<String, String> map = getParamsFromUrl(url);
                TogetherLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), params);
                throw e;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    private  HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            } catch (Exception e) {
                //throw new IOException(e);
            }
            HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;// Ĭ�϶���֤ͨ��
                }
            });
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        conn.setRequestProperty("User-Agent", "top-sdk-java");
        conn.setRequestProperty("Content-Type", ctype);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    private  URL buildGetUrl(String strUrl, String query) throws IOException {
        URL url = new URL(strUrl);
        if (StringUtils.isEmpty(query)) {
            return url;
        }

        if (StringUtils.isEmpty(url.getQuery())) {
            if (strUrl.endsWith("?")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "?" + query;
            }
        } else {
            if (strUrl.endsWith("&")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "&" + query;
            }
        }

        return new URL(strUrl);
    }

    public  String buildQuery(Map<String, String> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // ���Բ����������ֵΪ�յĲ���
            if (StringUtils.areNotEmpty(name, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }

        return query.toString();
    }

    protected  String getResponseAsString(HttpURLConnection conn) throws Exception {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        
        if(conn.getResponseCode()==200){
        	
        	 if (es == null) {
                 return getStreamAsString(conn.getInputStream(), charset);
             } else {
                 String msg = getStreamAsString(es, charset);
                 if (StringUtils.isEmpty(msg)) {
                     throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
                 } else {
                     System.out.print("msg=================: "+msg);
                     throw new IOException(msg);
                 }
             }
        	 
        }else{
        	throw new ReturnException();
        }
       
    }

    private  String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private  String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;

        if (!StringUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

    /**
     * ʹ��Ĭ�ϵ�UTF-8�ַ����������������ֵ��
     *
     * @param value ����ֵ
     * @return �������Ĳ���ֵ
     */
    public  String decode(String value) {
        return decode(value, DEFAULT_CHARSET);
    }

    /**
     * ʹ��Ĭ�ϵ�UTF-8�ַ��������������ֵ��
     *
     * @param value ����ֵ
     * @return �����Ĳ���ֵ
     */
    public  String encode(String value) {
        return encode(value, DEFAULT_CHARSET);
    }

    /**
     * ʹ��ָ�����ַ����������������ֵ��
     *
     * @param value ����ֵ
     * @param charset �ַ���
     * @return �������Ĳ���ֵ
     */
    public  String decode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLDecoder.decode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * ʹ��ָ�����ַ��������������ֵ��
     *
     * @param value ����ֵ
     * @param charset �ַ���
     * @return �����Ĳ���ֵ
     */
    public  String encode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLEncoder.encode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private  Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> map = null;
        if (url != null && url.indexOf('?') != -1) {
            map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
        }
        if (map == null) {
            map = new HashMap<String, String>();
        }
        return map;
    }

    /**
     * ��URL����ȡ���еĲ�����
     *
     * @param query URL��ַ
     * @return ����ӳ��
     */
    public  Map<String, String> splitUrlQuery(String query) {
        Map<String, String> result = new HashMap<String, String>();

        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }

        return result;
    }
}
