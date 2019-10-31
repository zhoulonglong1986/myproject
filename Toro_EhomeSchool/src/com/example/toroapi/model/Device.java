/*
 * �豸��
 */
package com.example.toroapi.model;

import java.io.File;

import com.example.toroapi.ApiException;
import com.example.toro.json.internal.mapping.ApiField;
import com.example.toro.json.internal.util.StringUtils;


/**
 * �豸
 *
 * @author rocky
 */
public class Device extends BaseModel implements Comparable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ApiField("device")
    private String device; //device:19999,11,1,1		//�豸ID,�豸����,��ĿID,�Ƿ�����

    @ApiField("did")
    private int deviceId;
    @ApiField("account")
    private String account;
    @ApiField("device_type")
    private int deviceType = -1;//�豸����
    @ApiField("project_id")
    private int projectId;//��Ŀ����
    @ApiField("nick_name")
    private String nickName; // ����
    @ApiField("remark_name")
    private String remarkName; // ����
    @ApiField("email")
    private String email;
    @ApiField("face")
    private String headIcon;
    @ApiField("is_online")
    private int isOnline; // �Ƿ�����
    @ApiField("lon")
    private double lon; // ����
    @ApiField("lat")
    private double lat; // γ��
    @ApiField("mobile")
    private String mobile;// �绰����

    @ApiField("mobile2")
    private String mobile2;

    @ApiField("password")
    private String password;

    private String imsi;
    private String imei;
    private String sn;
    @ApiField("loc_way")
    private int locWay;
    private int distance;
    @ApiField("locus_count")
    private int locusCount;
    @ApiField("friends_count")
    private int friendsCount;
    @ApiField("group_id")
    private int groupId;
    @ApiField("is_strange_area")
    public int isStrangeAreaOn = 0;
    @ApiField("desc")
    private String desc = "";  //����ƽ̨����
    private String cr;
    private String code;
    private int sign;
    private long lastLoginTime;
    @ApiField("website")
    private String website;   //��վ
    @ApiField("category")
    private int category;                 //����[1:ѧУ,2:��ѵ����]
    @ApiField("teacher")
    private String teacher;      //��ʦ(WAP)
    @ApiField("class")
    private String classString; //�༶(WAP)
    private String className;
    private String classUrl;
    @ApiField("schedule")
    private String schedule;    //�γ̱�(WAP)
    @ApiField("notice")
    private int notice;    //�Ƿ����֪ͨ[0:������,1:����(Ĭ��)]
    @ApiField("address")
    private String address;  //��ַ��Ϣ
    @ApiField("lonlat")
    private String lonlat;         //��γ�� 114.123,22.234


    /**
     * ��Ʒ�汾��
     */
    @ApiField("tl_version")
    private String tlVersion;

    /**
     * ��Ʒ�汾Code
     */
    @ApiField("tl_build")
    private int tlBuild;

    /**
     * �ڵ�ǰλ��ͣ���Ŀ�ʼ�����ʱ��
     */
    private String times;
    private long[] stopOver = new long[2];
    /**
     * ϵͳ�汾��
     */
    @ApiField("sys_version")
    private String sysVersion;
    /**
     * ����
     */
    @ApiField("model")
    private String model;
    /**
     * ������ʽ
     */
    @ApiField("net_type")
    private String netType;
    /**
     * ��λ����
     */
    private int accuracy = 50;

    public int getProjectId() {
        return projectId;
    }


    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    /**
     * MAC��ַ
     */
    private String mac;

    /**
     * ����
     */
    private String lang;
    /**
     * wifi
     */
    private String wifi;
    /**
     * ����GSM��վ
     */
    private String bs;
    /**
     * ����cdma��վ
     */
    private String cdma;
    /**
     * ͷ��
     */
    private File headIconFile;

    /**
     * Ϊİ���Ƽ�����׼���������ֶ�
     */
    @ApiField("type")
    private int type = 1;
    @ApiField("target_id")
    private int target_id = 0;
    @ApiField("isAccepted")
    private boolean isAccepted;
    @ApiField("msg")
    private String msg;

    @ApiField("push_password")
    private int push_password;


    @ApiField("sex")
    private int sex=-1;
    @ApiField("birthday")
    private String birthday;





    /**
     * �޸��ֶ�
     *
     * @throws ApiException
     */
    public void fixFields() throws ApiException {
        if (!StringUtils.isEmpty(getTimes())) {
            try {
                long[] _stopOver = new long[2];
                String[] arr = getTimes().split(",");
                _stopOver[0] = Long.parseLong(arr[0]);
                _stopOver[1] = Long.parseLong(arr[1]);
                setStopOver(_stopOver);
            } catch (Exception e) {
                e.printStackTrace();
                //����ֱ�ӱ��������
                throw new ApiException("����Device.times�ִ�ʱ����", e.getCause());
            }
        }

        if (!StringUtils.isEmpty(getClassString())) {
            try {
                String[] arr = getClassString().split(",", 2);
                setClassName(arr[0]);
                setClassUrl(arr[1]);
            } catch (Exception e) {
                e.printStackTrace();
                //����ֱ�ӱ��������
                throw new ApiException("����Device.class�ִ�ʱ����", e.getCause());
            }
        }

        if (!StringUtils.isEmpty(getLonlat())) {
            try {
                String[] arr = getLonlat().split(",");
                setLon(Double.parseDouble(arr[0]));
                setLat(Double.parseDouble(arr[1]));
            } catch (Exception e) {
                e.printStackTrace();
                //����ֱ�ӱ��������
                throw new ApiException("����Device.lonlat�ִ�ʱ����", e.getCause());
            }
        }

        if (!StringUtils.isEmpty(getDevice())) {
            try {
                String[] arr = getDevice().split(",");
                setDeviceId(Integer.parseInt(arr[0]));
                setDeviceType(Integer.parseInt(arr[1]));
                setProjectId(Integer.parseInt(arr[2]));
                setIsOnline(Integer.parseInt(arr[3]));
            } catch (Exception e) {
                e.printStackTrace();
                //����ֱ�ӱ��������
                throw new ApiException("����Device.device�ִ�ʱ����", e.getCause());
            }
        }
    }
   

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public void setLon(double lon) {
        //�κ�����£�û��λ����Ϣ���滻ԭ����λ����Ϣ�ģ�Ӣ��û��λ����Ϣ�Ķ��ǻ�ȡ����
        //Ӧ����ʾԭ��λ����Ϣ
        this.lon = lon;
    }

    public void setLat(double lat) {
        //�κ�����£�û��λ����Ϣ���滻ԭ����λ����Ϣ�ģ�Ӣ��û��λ����Ϣ�Ķ��ǻ�ȡ����
        //Ӧ����ʾԭ��λ����Ϣ
        this.lat = lat;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setTlVersion(String tlVersion) {
        this.tlVersion = tlVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public void setCdma(String cdma) {
        this.cdma = cdma;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long[] getStopOver() {
        return stopOver;
    }

    public void setStopOver(long[] stopOver) {
        this.stopOver = stopOver;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public String getEmail() {
        return email;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public String getImsi() {
        return imsi;
    }

    public String getImei() {
        return imei;
    }

    public String getSn() {
        return sn;
    }

    public String getTlVersion() {
        return tlVersion;
    }

    public String getSysVersion() {
        return sysVersion;
    }

    public String getModel() {
        return model;
    }

    public String getNetType() {
        return netType;
    }

    public String getMac() {
        return mac;
    }

    public String getLang() {
        return lang;
    }

    public String getWifi() {
        return wifi;
    }

    public String getBs() {
        return bs;
    }

    public String getCdma() {
        return cdma;
    }

    public File getHeadIconFile() {
        return headIconFile;
    }

    public void setHeadIconFile(File headIconFile) {
        this.headIconFile = headIconFile;
    }

    public int getLocWay() {
        return locWay;
    }

    public void setLocWay(int locWay) {
        this.locWay = locWay;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getLocusCount() {
        return locusCount;
    }

    public void setLocusCount(int locusCount) {
        this.locusCount = locusCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getTlBuild() {
        return tlBuild;
    }

    public void setTlBuild(int tlBuild) {
        this.tlBuild = tlBuild;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public int getIsStrangeAreaOn() {
        return isStrangeAreaOn;
    }

    public void setIsStrangeAreaOn(int isStrangeAreaOn) {
        this.isStrangeAreaOn = isStrangeAreaOn;
    }

    public String getCr() {
        return cr;
    }

    public void setCr(String cr) {
        this.cr = cr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    @Override
    public int compareTo(Object arg0) {
        Device d2 = (Device) arg0;
        if (this.getIsOnline() > d2.getIsOnline())
            return -1;
        else if (this.getIsOnline() == 1 && d2.getIsOnline() == 1) {
            return ((Long) d2.getLastLoginTime()).compareTo(this.getLastLoginTime());
        } else if (this.getIsOnline() == 0 && d2.getIsOnline() == 0) {
//            if (this.getDeviceType() == AppConstants.LOCATION_DEVICE_ANDROID_FLAG
//                    || this.getDeviceType() == AppConstants.LOCATION_DEVICE_IOS_FLAG)
//                return -1;
//            else if (d2.getDeviceType() == AppConstants.LOCATION_DEVICE_ANDROID_FLAG
//                    || d2.getDeviceType() == AppConstants.LOCATION_DEVICE_IOS_FLAG)
//                return 1;
            return ((Long) d2.getLastLoginTime()).compareTo(this.getLastLoginTime());
        }
        return 1;
    }


    public void setDeviceLatLng(double latitude, double longitude, long[] stopOver) {
        this.setLat(latitude);
        this.setLon(longitude);
        this.setStopOver(stopOver);
    }


    public String getDeviceName() {
        if (StringUtils.isEmpty(getRemarkName())) {
            return getNickName();
        } else {
            return getRemarkName();
        }
    }

    public void setLocationEndingTime(long time) {
        this.stopOver[1] = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String getName() {
        return StringUtils.isEmpty(getRemarkName()) ? getNickName() : getRemarkName();
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }


    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassString() {
        return classString;
    }

    public void setClassString(String classString) {
        this.classString = classString;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassUrl() {
        return classUrl;
    }

    public void setClassUrl(String classUrl) {
        this.classUrl = classUrl;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLonlat() {
        return lonlat;
    }

    public void setLonlat(String lonlat) {
        this.lonlat = lonlat;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPush_password() {
        return push_password;
    }

    public void setPush_password(int push_password) {
        this.push_password = push_password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


}

