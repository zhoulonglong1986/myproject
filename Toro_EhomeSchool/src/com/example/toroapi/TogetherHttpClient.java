/**
 * 2.2: FriendsGetRequest<br>
 * 2.3: FriendsAddRequest<br>
 * 2.4: FriendsRemoveRequest<br>
 * 2.5: <br>
 * 2.12 RollingRequest<br>
 * 2.18 AchieveFenceActiveRequest<br>
 * 2.20 AchieveFenceRequest<br>
 * 
 * 3.6 FriendsInfoGetRequest<br>
 * 3.10 ContactRequest<br>
 * 
 */
package com.example.toroapi;

/**
 * app http client
 * 
 * @author rocky
 * 
 */
public interface TogetherHttpClient {

	/**
	 * 执行API请求。
	 * 
	 * @param <T>
	 * @param request
	 *            具体的TOP请求
	 * @return
	 * @throws ApiException
	 */
	public <T extends TogetherResponse> T execute(TogetherRequest<T> request)
			throws ApiException;

	/**
	 * 执行API请求。
	 * 
	 * @param <T>
	 * @param request
	 *            具体的TOP请求
	 * @param session
	 *            用户会话授权码
	 * @return
	 * @throws ApiException
	 */
	public <T extends TogetherResponse> T execute(TogetherRequest<T> request,
			String session) throws ApiException;
}
