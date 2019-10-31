package com.example.toroapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.toro.json.internal.util.StringUtils;
import com.example.toroapi.model.Device;
//import com.e5ex.together.dao.helper.DBHelper;
//import com.e5ex.together.service.PushSorketService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户环境
 */
public class UserContext {
	private static UserContext context;
	public List<Device> friendList = new ArrayList<Device>();// 设备列表
	public List<Device> tobeDisplayedDevices = new ArrayList<Device>();

	public List<Device> getFriendList() {
		return friendList;
	}

	private Device user = null;

	private Device selectedMember = null;
	private boolean isLogon;
	private String partnerName;

	private long configId;
	private JSONObject configJson;

	public static UserContext getInstans() {
		if (context == null) {
			context = new UserContext();
		}
		return context;
	}

	public Device getUser() {
		return user;
	}

	public void setUser(Device newUser) {
		// this.setDevice(user, newUser);
	}
}
