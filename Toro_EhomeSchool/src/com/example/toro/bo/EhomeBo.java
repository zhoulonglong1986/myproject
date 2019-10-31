package com.example.toro.bo;

import java.io.File;

import com.example.toro.request.SubmitCardataRequest;
import com.example.toro.request.SubmitDStatusRequest;
import com.example.toro.request.getSchNoticeRequest;
import com.example.toro.request.getStuInformationRequest;
import com.example.toro.request.getStuListRequest;
import com.example.toro.response.SubmitCardataResponse;
import com.example.toro.response.SubmitDStatusResponse;
import com.example.toro.response.getSchNoticeResponse;
import com.example.toro.response.getStuInformationResponse;
import com.example.toro.response.getStuListResponse;
import com.example.toroapi.ApiException;
import com.example.toroapi.AppConstants;
import com.example.toroapi.DefaultTogetherClient;
import com.example.toroapi.model.Device;

public class EhomeBo {
	
	
	/**
	 * ��ȡ�����б�
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public static getSchNoticeResponse getSchNotice(String mid) throws Exception{
		
		getSchNoticeRequest req=new getSchNoticeRequest();
		req.setMid(AppConstants.Mid);
		//req.setMid("XM00e");
		getSchNoticeResponse  res=DefaultTogetherClient.exec(req);
		return res;
		
	}
	
	
	/**
	 * ��ȡѧ���б�
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public static getStuListResponse getStuList(String mid) throws Exception{
		
		getStuListRequest req=new getStuListRequest();
		req.setMid(mid);
		getStuListResponse  res=DefaultTogetherClient.exec(req);
		return res;
		
	}
	
	
	/**
	 * ��ȡѧ����Ϣ
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public static getStuInformationResponse getStuInformation(String mid,String id,String sign) throws Exception{
		
		getStuInformationRequest req=new getStuInformationRequest();
		req.setMid(mid);
		req.setId(id);
		req.setSign(sign);
		getStuInformationResponse  res=DefaultTogetherClient.exec(req);
		return res;
		
	}
	

	
	/**
	 * �ϴ�ˢ ����Ϣ
	 * @param cid
	 * @param photo
	 * @param tm
	 * @return
	 * @throws ApiException
	 */
	public static SubmitCardataResponse submitgCard(String cid,File photo ,String tm)throws ApiException {

		SubmitCardataRequest req = new SubmitCardataRequest();
		req.setCid(cid);
		if(photo!=null){req.setPhoto(photo);};
		req.setTm(tm);
		SubmitCardataResponse  rq=DefaultTogetherClient.exec(req);
		return rq;
	}
	
	
	/**
	 * �ύ����״̬
	 * @param mid
	 * @param tm
	 * @param data
	 * @return
	 * @throws ApiException
	 */
	public static SubmitDStatusResponse submitDstatus(String data)throws ApiException {

		SubmitDStatusRequest req = new SubmitDStatusRequest();
		req.setData(data);
		SubmitDStatusResponse rq=DefaultTogetherClient.exec(req);
		return rq;
	}


}
