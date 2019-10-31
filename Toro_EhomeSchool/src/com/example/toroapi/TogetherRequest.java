package com.example.toroapi;

import java.util.Map;

/**
 * TOP����ӿڡ�
 * 
 * @author carver.gu
 * @since 1.0, Sep 12, 2009
 */
public interface TogetherRequest<T extends TogetherResponse> {

	/**
	 * ��ȡTOP��API���ơ�
	 * 
	 * @return API����
	 */
	public String getBusiness_type();

	/**
	 * ��ȡ���е�Key-Value��ʽ���ı�����������ϡ����У�
	 * <ul>
	 * <li>Key: ���������</li>
	 * <li>Value: �������ֵ</li>
	 * </ul>
	 * 
	 * @return �ı������������
	 */
	public Map<String, String> getTextParams() throws ApiException;

	/**
	 * @return ָ����Ĭ�ϵ�ʱ���
	 */
	public Long getTimestamp();

	/**
	 * ����ʱ��������������,��������ʱ��ʹ�õ�ʱ��ʱ�䡣
	 * 
	 * @param timestamp
	 *            ʱ���
	 */
	public void setTimestamp(Long timestamp);

	/**
	 * ��Ӧ�Ļ�Ӧ����
	 * 
	 * @return
	 */
	public Class<T> getResponseClass();

	/**
	 * ����Э��汾
	 * 
	 * @return
	 */
	public String getPv();

	/**
	 * �ͻ��˲�����飬���ٷ������Ч����
	 */
	public void check() throws ApiRuleException;

	/**
	 * ���HTTP����ͷ����
	 */
	public Map<String, String> getHeaderMap();

	/**
	 * ����Զ����������
	 */
	public void putOtherTextParam(String key, String value);
}
