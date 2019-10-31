package com.example.toroapi;

/**
 * TOP��Ӧ�������ӿڡ���Ӧ��ʽ������JSON, XML�ȵȡ�
 * 
 * @author carver.gu
 * @since 1.0, Apr 11, 2010
 */
public interface TogetherParser<T extends TogetherResponse> {

	/**
	 * ����Ӧ�ַ������ͳ���Ӧ���������
	 * 
	 * @param rsp
	 *            ��Ӧ�ַ���
	 * @return �������
	 */
	public T parse(String rsp) throws ApiException;

	/**
	 * ��ȡ��Ӧ�����͡�
	 */
	public Class<T> getResponseClass() throws ApiException;

}
