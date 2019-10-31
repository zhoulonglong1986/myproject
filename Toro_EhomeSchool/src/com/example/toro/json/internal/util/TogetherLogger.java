package com.example.toro.json.internal.util;
import com.example.toroapi.Constants;
import com.example.toroapi.TogetherResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * �ͻ�����־ ͨѶ�����ʽ��time^_^api^_^app^_^ip^_^os^_^sdk^_^url^responseCode
 * ҵ������ʽ��time^_^response
 */
public class TogetherLogger {

    static class Log {

        String name;

        public Log(String name) {
            this.name = name;
        }

        public void error(String msg) {
            // System.out.println("[" + name + "] " + msg);
        }
    }
    private static final Log clog = new Log("sdk.comm.err");
    private static final Log blog = new Log("sdk.biz.err");
    private static String osName = System.getProperties().getProperty("os.name");
    private static String ip = null;
    private static boolean needEnableLogger = true;

    public static void setNeedEnableLogger(boolean needEnableLogger) {
        TogetherLogger.needEnableLogger = needEnableLogger;
    }

    public static String getIp() {
        if (ip == null) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ip;
    }

    public static void setIp(String ip) {
        TogetherLogger.ip = ip;
    }

    /**
     * ͨѶ������־
     */
    public static void logCommError(Exception e, HttpURLConnection conn, String appKey, String method, byte[] content) {
        if (!needEnableLogger) {
            return;
        }
        String contentString = null;
        try {
            contentString = new String(content, "UTF-8");
            logCommError(e, conn, appKey, method, contentString);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * ͨѶ������־
     */
    public static void logCommError(Exception e, String url, String appKey, String method, byte[] content) {
        if (!needEnableLogger) {
            return;
        }
        String contentString = null;
        try {
            contentString = new String(content, "UTF-8");
            logCommError(e, url, appKey, method, contentString);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * ͨѶ������־
     */
    public static void logCommError(Exception e, HttpURLConnection conn, String appKey, String method, Map<String, String> params) {
        if (!needEnableLogger) {
            return;
        }
        _logCommError(e, conn, null, appKey, method, params);
    }

    public static void logCommError(Exception e, String url, String appKey, String method, Map<String, String> params) {
        if (!needEnableLogger) {
            return;
        }
        _logCommError(e, null, url, appKey, method, params);
    }

    /**
     * ͨѶ������־
     */
    private static void logCommError(Exception e, HttpURLConnection conn, String appKey, String method, String content) {
        Map<String, String> params = parseParam(content);
        _logCommError(e, conn, null, appKey, method, params);
    }

    /**
     * ͨѶ������־
     */
    private static void logCommError(Exception e, String url, String appKey, String method, String content) {
        Map<String, String> params = parseParam(content);
        _logCommError(e, null, url, appKey, method, params);
    }

    /**
     * ͨѶ������־
     */
    private static void _logCommError(Exception e, HttpURLConnection conn, String url, String appKey, String method, Map<String, String> params) {
        DateFormat df = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIMEZONE));
        String sdkName = Constants.SDK_VERSION_TWO;
        String urlStr = null;
        String rspCode = "";
        if (conn != null) {
            try {
                urlStr = conn.getURL().toString();
                rspCode = "HTTP_ERROR_" + conn.getResponseCode();
            } catch (IOException ioe) {
            }
        } else {
            urlStr = url;
            rspCode = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(df.format(new Date()));// ʱ��
        sb.append("^_^");
        sb.append(method);// API
        sb.append("^_^");
        sb.append(appKey);// APP
        sb.append("^_^");
        sb.append(getIp());// IP��ַ
        sb.append("^_^");
        sb.append(osName);// ����ϵͳ
        sb.append("^_^");
        sb.append(sdkName);// SDK����,�������ӣ��뻻����������
        sb.append("^_^");
        sb.append(urlStr);// ����URL
        sb.append("^_^");
        sb.append(rspCode);
        sb.append("^_^");
        sb.append((e.getMessage() + "").replaceAll("\r\n", " "));
        clog.error(sb.toString());
    }

    private static Map<String, String> parseParam(String contentString) {
        Map<String, String> params = new HashMap<String, String>();
        if (contentString == null || contentString.trim().equals("")) {
            return params;
        }
        String[] paramsArray = contentString.split("\\&");
        if (paramsArray != null) {
            for (String param : paramsArray) {
                String[] keyValue = param.split("=");
                if (keyValue != null && keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    /**
     * ҵ��/ϵͳ������־
     */
    public static void logBizError(String rsp) {
        if (!needEnableLogger) {
            //return;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIMEZONE));
        StringBuilder sb = new StringBuilder();
        sb.append(df.format(new Date()));
        sb.append("^_^");
        sb.append(rsp);
        blog.error(sb.toString());
    }

    /**
     * �����ر����ʱ��¼���������ֳ�
     */
    public static void logErrorScene(Map<String, Object> rt, TogetherResponse tRsp, String appSecret) {
        if (!needEnableLogger) {
            return;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIMEZONE));
        StringBuilder sb = new StringBuilder();
        sb.append("ErrorScene");
        sb.append("^_^");
        sb.append(tRsp.getResult());
        sb.append("^_^");
        sb.append(tRsp.getMsg());
        sb.append("^_^");
        sb.append("appSecret***");// ���ڰ�ȫ���ǣ����ٴ�ӡappSecret
        sb.append("^_^");
        sb.append(ip);
        sb.append("^_^");
        sb.append(osName);
        sb.append("^_^");
        sb.append(df.format(new Date()));
        sb.append("^_^");
        sb.append("ProtocalMustParams:");
        appendLog((TogetherHashMap) rt.get("protocalMustParams"), sb);
        sb.append("^_^");
        sb.append("ProtocalOptParams:");
        appendLog((TogetherHashMap) rt.get("protocalOptParams"), sb);
        sb.append("^_^");
        sb.append("ApplicationParams:");
        appendLog((TogetherHashMap) rt.get("textParams"), sb);
        sb.append("^_^");
        sb.append("Body:");
        sb.append((String) rt.get("rsp"));
        blog.error(sb.toString());
    }

    private static void appendLog(TogetherHashMap map, StringBuilder sb) {
        boolean first = true;
        Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry<String, String> entry : set) {
            if (!first) {
                sb.append("&");
            } else {
                first = false;
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
    }
}
