package com.example.toroapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class HttpUtils {
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;

	/**
	 * ��ָ������Դ·�����������ȡ��Ӧʵ����󲢷���
	 * 
	 * @param uri
	 *            ��Դ·��
	 * @param params
	 *            �����˷�������ʱ��ʵ������
	 * @param method
	 *            ���󷽷�
	 * @return
	 * @throws IOException
	 */
	public static HttpEntity getEntity(String uri, List<NameValuePair> params,
			int method) throws IOException {
		HttpEntity entity = null;
		// �����ͻ��˶���
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
		// �����������
		HttpUriRequest request = null;
		switch (method) {
		case METHOD_GET:
			StringBuilder sb = new StringBuilder(uri);
			if (params != null && !params.isEmpty()) {
				sb.append('?');
				for (NameValuePair pair : params) {
					sb.append(pair.getName()).append('=')
							.append(pair.getValue()).append('&');
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			request = new HttpGet(sb.toString());
			break;
		case METHOD_POST:
			request = new HttpPost(uri);
			if (params != null && !params.isEmpty()) {
				// ��������ʵ�����
				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(
						params);
				// ��������ʵ��
				((HttpPost) request).setEntity(reqEntity);
			}
			break;
		}
		// ִ�������ȡ��Ӧ����
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			entity = response.getEntity();
		}
		return entity;
	}

	/**
	 * ��ȡʵ���������ݳ��Ȳ�����
	 * 
	 * @param entity
	 * @return
	 */
	public static long getLength(HttpEntity entity) {
		long len = 0;
		if (entity != null) {
			len = entity.getContentLength();
		}
		return len;
	}

	/**
	 * ��ȡָ������Ӧʵ����������������
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */

	public static InputStream getStream(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = null;
		if (entity != null) {
			in = entity.getContent();
		}
		return in;
	}

}
