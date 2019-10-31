package com.example.toro_ehomeschool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.xml.sax.ext.DeclHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.example.toro.doman.Card;
import com.example.toro.doman.Member;
import com.example.toro.doman.Push;
import com.example.toro.doman.SchNotice;
import com.example.toro.doman.Student;
import com.example.toro.doman.SuaiCar;
import com.example.toro.sample.Application;
import com.example.toro.sample.db.DbHelper;
import com.example.toro_ehomeschool.LoadingActivity.StuImageThread;
import com.example.toroapi.AppConstants;
import com.example.toroapi.HttpUtils;
import com.google.gson.Gson;
import com.linj.FileOperateUtil;



import android.test.AndroidTestCase;
import android.util.Log;
import android.util.Xml;

public class ToroTest extends AndroidTestCase{
	
	
	DbHelper db;
	public void testDbHelper() {
		 
		db = DbHelper.getInstance(getContext());
	} 
	
	public void testjson(){
		
		Gson gson=new Gson();
		Push push=gson.fromJson("{t:3}", Push.class);

	}
	
	
   public void testQueryfeng(){
	   
	   db = DbHelper.getInstance(getContext());
	   ArrayList<SuaiCar> suaicarList=db.queryScfeng();
	   
	   System.out.println("==============="+suaicarList.size());
   }
   
   public void testModel(){
	   
	   testSaveSchNotice();
	   testStuXml();
   }
	
	public void testSaveSchNotice() {
		db = DbHelper.getInstance(getContext());
		ArrayList<SchNotice> schList=new ArrayList<SchNotice>();
		SchNotice sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf371");
		sch.setTime(1460358182095L);
		sch.setType(0);
		sch.setTitle("关于放假通知");
		sch.setTxt("根据国家有关规定，五一节放假时间为：5月1―3日(星期五--星期日)，共三天。5月4日(星期一)照常上课。为了让孩子们度过一个安全、健康、有意义的假期，再次提醒家长在假期内配合做好以下工作：1、加强外出安全教育。幼儿外出时一定要有成人监护与照顾，请注意许多被容易忽略的安全隐患，以防走失。遵守交通规则，以免受到不必要的伤害。2、饮食睡眠尽可能保持规律，切忌暴饮暴食，注意饮食安全。3、4、5月份，春夏季各类传染病高发，特别是禽流感、手足口病等传染病，提醒家长教育、督促幼儿多喝水，饭前、便后、户外活动后用正确的方法洗手，不吃生冷食物，培养幼儿良好的卫生习惯。同时，家长也要注意个人卫生，防止将病菌带入家里传染给幼儿。预祝小朋友及家长朋友度过一个安全并有意义的假期!");
		sch.setPhoto("");
		sch.setVideo("");
		schList.add(sch);
		
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf372");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img1.png");
    	sch.setTxt("本学期画画比赛获奖小朋友！");
		sch.setVideo("");
		schList.add(sch);
		
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf374");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
   	    sch.setPhoto("http://e5ex.com/img3.png");
   	    sch.setTxt("元旦晚会精彩表演");
		sch.setVideo("");
		schList.add(sch);
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf375");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
   	    sch.setPhoto("http://e5ex.com/img4.png");
   	    sch.setTxt("欢乐课间操");
		sch.setVideo("");
		schList.add(sch);
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf376");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
     	sch.setPhoto("http://e5ex.com/img5.png");
   	    sch.setTxt("小朋友在用午餐");
		sch.setVideo("");
		schList.add(sch);
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf377");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
   	    sch.setPhoto("http://e5ex.com/img6.png");
   	    sch.setTxt("晚会前准备");
		sch.setVideo("");
		schList.add(sch);
		
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf378");
		sch.setTime(1460358182095L);
		sch.setType(2);
		sch.setTitle(" ");
		sch.setTxt("");
		sch.setPhoto("");
		sch.setVideo("http://e5ex.com/sch.mp4");
		schList.add(sch);
		
		db.insertOrSchnotice(schList);
	}
	
	
	
    /**
     * 删除所有图片
     */
	
	public void delefile(){

    	File file=new File(FileOperateUtil.getFolderPath(this.getContext(), FileOperateUtil.TYPE_STU));
    	System.out.println("============"+file.getPath());
    	deleteFile(file);
	}
	 /** 
     * 通过递归调用删除一个文件夹及下面的所有文件 
     * @param file 
     */  
    public  void deleteFile(File file){  
    	
        if(file.isFile()){//表示该文件不是文件夹  
            file.delete();  
        }else{  
            //首先得到当前的路径  
            String[] childFilePaths = file.list();  
           
            for(String childFilePath : childFilePaths){  
                File childFile=new File(file.getAbsolutePath()+"//"+childFilePath);
                
                System.out.println("==============="+childFile.getPath());
                deleteFile(childFile);  
            }  
            file.delete();  
        }  
    }  
    
    
    /**
     * 网络解析 xml 实时解析
     * @param url
     * @throws Exception
     */
	public void testStuXml() {
		
		db = DbHelper.getInstance(getContext());
		System.out.println("================00000");
		Student stu=new Student();
		ArrayList<Card> cids=new ArrayList<Card>();
		Card card=new Card();
		ArrayList<Member> members=new ArrayList<Member>();
		Member member=new Member();

		File file=new File(Application.schPath+"stu.xml");
		System.out.println("================00000"+file.getAbsolutePath());
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new FileInputStream(file), "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				String tag = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					if (tag.equals("stu")) {
                      stu=new Student();
                      stu.setId(parser.getAttributeValue(0));
					} else if (tag.equals("cids")) {
                          cids=new ArrayList<Card>();
					} else if (tag.equals("cid")) {
                          card=new Card();
                          card.setCid(parser.nextText());
                          cids.add(card);
					} else if (tag.equals("members")) {
                          members=new ArrayList<Member>();
					} else if (tag.equals("member")) {
                           member=new Member();
					} else if (tag.equals("name")) {
						   member.setName(parser.nextText());
					} else if (tag.equals("relatship")) { 
                           member.setRelatship(parser.nextText());
					} else if (tag.equals("clazz")) {
						   member.setClazz(parser.nextText());
					} else if (tag.equals("photo")) {
						   String photo=parser.nextText(); 
						   if(!"".equals(photo)){
							   member.setPhoto(photo);
							   //这里加入线程池下载图片
							 
						   }
						  
					}
					break;

				case XmlPullParser.END_TAG:
					if (tag.equals("stu")) {
						 //这里将数据插入数据库
						
						db.insertOrStu(stu.getId(), stu);
					} else if (tag.equals("cids")) {
						  stu.setCids(cids);
					} else if (tag.equals("members")) {
						  stu.setMembers(members);
					} else if (tag.equals("member")) {
                          members.add(member);
					} 
					break;
				default:
					break;
				}
				type = parser.next();
			}
			
		
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	

}
