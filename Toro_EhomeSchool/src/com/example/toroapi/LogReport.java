/*
 * log上报
 */
package com.example.toroapi;

import com.example.toro.request.LogReportRequest;

/**
 * 
 * @author rocky
 */
public class LogReport {

	/**
	 * 已实现异步
	 * 
	 * @param deviceId
	 * @param errCode
	 * @param e
	 */
	public static void log(int deviceId, String errCode, Exception e) {
		LogReportRequest req = new LogReportRequest();
		req.setDeviceId(deviceId);
		req.setErrCode(errCode);
		req.setErrMsg(makeStr(e));
		_report(req);
	}

	/**
	 * 
	 * @param arr
	 */
	public static void logMultiple(Object... arr) {
		LogReportRequest req = new LogReportRequest();
		req.setDeviceId(0);
		req.setErrCode("sys.err");
		req.setErrMsg(makeStr(arr));
		_report(req);
	}

	/**
	 * 
	 * @param e
	 * @param req
	 */
	public static <T extends TogetherRequest> void log(int deviceId,
			Exception e, T req) {
		LogReportRequest logReq = new LogReportRequest();
		try {
			logReq.setErrCode("sdk." + req.getClass().getSimpleName());
			logReq.setErrMsg(makeStr(e, "SDK Request:" + req.getTextParams()));
		} catch (Exception ex) {
			logReq.setErrMsg(makeStr(e, ex));
		}
		_report(logReq);
	}

	private static String makeStr(Object... arr) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, l = arr.length; i < l; i++) {
			if (i > 0) {
				sb.append("^_^");
			}
			if (arr[i] == null) {
				continue;
			} else if (arr[i] instanceof ApiException) {
				sb.append(getStackTrace((Exception) arr[i]));
				sb.append("\n Server Response: ");
				sb.append(((ApiException) arr[i]).getRspBody());
				sb.append("\n");
			} else if (arr[i] instanceof Exception) {
				sb.append(getStackTrace((Exception) arr[i]));
			} else {
				sb.append(arr[i]);
			}
		}
		return sb.toString();
	}

	private static String getStackTrace(Exception e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : e.getStackTrace()) {
			if (LogReport.class.getName().equals(ste.getClassName())) {
				continue;
			}
			sb.append("\tat ").append(ste.toString()).append("\n");
		}
		//
		sb.insert(0, e.fillInStackTrace() + "\n");

		return sb.toString();
	}

	/**
	 * 上报服务器, 已实现异步
	 * 
	 * @param req
	 */
	private static void _report(final LogReportRequest req) {
		// System.out.println("Msg:" + req.getErrMsg());
		// System.out.println("Code:" + req.getErrCode());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (req.getDeviceId() == null) {
						req.setDeviceId(0);
					}
					// System.out.println(">>>>> 错误上报服务器");
					DefaultTogetherClient.exec(req);
				} catch (Exception e) {
					// 报错日志都报错, 算了吧
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) {

		LogReportRequest req = new LogReportRequest();
		req.setDeviceId(15077);

		req.bug();

	}
}
