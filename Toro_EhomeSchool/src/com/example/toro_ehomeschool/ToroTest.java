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
		sch.setTitle("���ڷż�֪ͨ");
		sch.setTxt("���ݹ����йع涨����һ�ڷż�ʱ��Ϊ��5��1�D3��(������--������)�������졣5��4��(����һ)�ճ��ϿΡ�Ϊ���ú����Ƕȹ�һ����ȫ��������������ļ��ڣ��ٴ����Ѽҳ��ڼ���������������¹�����1����ǿ�����ȫ�������׶����ʱһ��Ҫ�г��˼໤���չˣ���ע����౻���׺��Եİ�ȫ�������Է���ʧ�����ؽ�ͨ���������ܵ�����Ҫ���˺���2����ʳ˯�߾����ܱ��ֹ��ɣ��мɱ�����ʳ��ע����ʳ��ȫ��3��4��5�·ݣ����ļ����ഫȾ���߷����ر��������С�����ڲ��ȴ�Ⱦ�������Ѽҳ������������׶����ˮ����ǰ����󡢻���������ȷ�ķ���ϴ�֣���������ʳ������׶����õ�����ϰ�ߡ�ͬʱ���ҳ�ҲҪע�������������ֹ������������ﴫȾ���׶���ԤףС���Ѽ��ҳ����Ѷȹ�һ����ȫ��������ļ���!");
		sch.setPhoto("");
		sch.setVideo("");
		schList.add(sch);
		
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf372");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img1.png");
    	sch.setTxt("��ѧ�ڻ���������С���ѣ�");
		sch.setVideo("");
		schList.add(sch);
		
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf374");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
   	    sch.setPhoto("http://e5ex.com/img3.png");
   	    sch.setTxt("Ԫ����ᾫ�ʱ���");
		sch.setVideo("");
		schList.add(sch);
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf375");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
   	    sch.setPhoto("http://e5ex.com/img4.png");
   	    sch.setTxt("���ֿμ��");
		sch.setVideo("");
		schList.add(sch);
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf376");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
     	sch.setPhoto("http://e5ex.com/img5.png");
   	    sch.setTxt("С�����������");
		sch.setVideo("");
		schList.add(sch);
		
		sch=new SchNotice();
		sch.setId("5191d185e4b07eb1e74cf377");
		sch.setTime(1460358182095L);
		sch.setType(1);
		sch.setTitle("");
   	    sch.setPhoto("http://e5ex.com/img6.png");
   	    sch.setTxt("���ǰ׼��");
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
     * ɾ������ͼƬ
     */
	
	public void delefile(){

    	File file=new File(FileOperateUtil.getFolderPath(this.getContext(), FileOperateUtil.TYPE_STU));
    	System.out.println("============"+file.getPath());
    	deleteFile(file);
	}
	 /** 
     * ͨ���ݹ����ɾ��һ���ļ��м�����������ļ� 
     * @param file 
     */  
    public  void deleteFile(File file){  
    	
        if(file.isFile()){//��ʾ���ļ������ļ���  
            file.delete();  
        }else{  
            //���ȵõ���ǰ��·��  
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
     * ������� xml ʵʱ����
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
							   //��������̳߳�����ͼƬ
							 
						   }
						  
					}
					break;

				case XmlPullParser.END_TAG:
					if (tag.equals("stu")) {
						 //���ｫ���ݲ������ݿ�
						
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
