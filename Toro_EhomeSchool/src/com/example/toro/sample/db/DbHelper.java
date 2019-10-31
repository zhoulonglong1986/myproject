package com.example.toro.sample.db;

import java.util.ArrayList;
import java.util.List;

import com.example.toro.doman.Card;
import com.example.toro.doman.Member;
import com.example.toro.doman.SchNotice;
import com.example.toro.doman.Student;
import com.example.toro.doman.SuaiCar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
	
	private static final String DATABASE_NAME ="USER.db";
	private static int VERSION = 3 ;
	private static SQLiteDatabase mDB;
	private static DbHelper mHelper ;

	private DbHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	public static DbHelper getInstance(Context context){
		if(mHelper == null){
			mHelper = new DbHelper(context) ;
		}
		return mHelper ;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//保存机器码
		 db.execSQL("CREATE TABLE IF NOT EXISTS 'tb_mid' (" + 
	                "'id' INTEGER  PRIMARY KEY AUTOINCREMENT," + 
	                "'mid' VARCHAR(64));"); // MID
		//公告表
		 db.execSQL("CREATE TABLE IF NOT EXISTS 'tb_schnotice' (" + 
	                "'id' VARCHAR(32) PRIMARY KEY NOT NULL ," + // 公告唯一ID
	                "'time' INTEGER," + // 1: time  公告时间
	                "'type' INTEGER," + // 2: type  公告类型
	                "'title' VARCHAR(64)," + // 3: 公告标题
	                "'txt' TEXT," + // 4: 文本公告
	                "'photo' VARCHAR(64)," + // 5: 图片URL
	                "'video' VARCHAR(64)," +//6: 视频URL
	                 "'state' INTEGER);" ); // 是否停播
		 
		//学生表
		 db.execSQL("CREATE TABLE IF NOT EXISTS 'tb_stus' (" + 
	                "'id' VARCHAR(32) PRIMARY KEY NOT NULL);"); // 学生ID
		 
		 //卡 数据 表
		 db.execSQL("CREATE TABLE IF NOT EXISTS 'tb_cids' (" + //
	                "'cid' VARCHAR(32) PRIMARY KEY NOT NULL ," + // 卡唯一ID
	                "'rid' VARCHAR(32)," +
	                " FOREIGN KEY(rid) REFERENCES tb_stus(id));"); //1: rid  关联的 学生主 ID
		 
		 //亲属 数据 表
		 db.execSQL("CREATE TABLE IF NOT EXISTS 'tb_member' (" + //
	                "'rid' VARCHAR(32)," + // rid  关联的 学生主 ID
	                "'name' VARCHAR(64)," +
	                "'relatship' VARCHAR(32)," +
	                "'clazz' VARCHAR(32)," +
	                "'age' INTEGER," +
	                "'photo' VARCHAR(64)," +
	                " FOREIGN KEY(rid) REFERENCES tb_stus(id));"); //1: rid  关联的 学生主 ID
		 
		 //刷卡 数据保存 表
		 db.execSQL("CREATE TABLE IF NOT EXISTS 'tb_suaicar' (" + //
	                "'cid' VARCHAR(32) ," + // 卡唯一ID
	                "'photo' VARCHAR(32)," +
	                "'time' VARCHAR(32)," + // 
				    "'state' INTEGER);"); // 
		 
		 
	}
	
	private SQLiteDatabase openReadable(){
		if(mDB != null){
			if(!mDB.isOpen()){
				mDB = this.getReadableDatabase() ;
			}else{
				return mDB ;
			}
		}else{
			mDB = this.getReadableDatabase() ;
		}
		return mDB ;
	}

	private SQLiteDatabase openWritable(){
		if(mDB != null){
			if(!mDB.isOpen()){
				mDB = this.getWritableDatabase() ;
			}else{
				return mDB ;
			}
		}else{
			mDB = this.getReadableDatabase() ;
		}
		return mDB ;
	}
	
	/**
	 * 插入机器码
	 * @param mid
	 */
	public synchronized void insertOrtMid(String mid){
		openWritable() ;
		Cursor cursor = null ;
		ContentValues values=null;
		    		 try {
						cursor = mDB.rawQuery("SELECT mid FROM tb_mid WHERE mid = ？",new String[] {mid });
						 values = new ContentValues();
						 values.put("mid",mid);
						
						long rowId = 0;
						if (cursor != null && cursor.getCount() > 0) { // update

							mDB.update("tb_schnotice", values, "id = ? ",new String[] { mid });

						} else {
							rowId = mDB.insert("tb_schnotice", null, values);
						}

		}catch(Exception e){
			
		}finally{
		    if(null!=cursor){
		    	cursor.close() ;
		    }
			mDB.close() ;
		}
	}
	/**
	 * 查询机器码
	 * @return
	 */
	public synchronized String queryMid(){
		openReadable() ;
		String mid = "";
		Cursor cursor=null;
		try{
			 cursor = mDB.rawQuery("SELECT mid FROM tb_mid WHERE cid = ?",new String[] { "1001" });
			
			if(null!=cursor&&cursor.moveToFirst()){
				 mid=cursor.getString(cursor.getColumnIndex("mid"));
	
			}
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			if(null!=cursor){
				cursor.close();
			}
			mDB.close() ;
		}
		
		return mid;
	}
	/**
	 * 插入公告数据
	 * @param schList
	 */
	public synchronized void insertOrSchnotice(ArrayList<SchNotice> schList){
		openWritable() ;
		Cursor cursor = null ;
		ContentValues values=null;
		try{
			
			mDB.execSQL("update tb_schnotice set state=? ",new String[]{"0"});
			
		    if(schList!=null&&schList.size()>0){
		    	for(SchNotice sch:schList){
		    		 try {
		    			 
						cursor = mDB.rawQuery("SELECT * FROM tb_schnotice WHERE id = ?",new String[] { sch.getId() });
						 values = new ContentValues();
						 values.put("id",sch.getId());//key为字段名，value为值
						 values.put("time",sch.getTime());
						 values.put("type", sch.getType());
						 values.put("title", sch.getTitle());
						 values.put("txt", sch.getTxt());
						 values.put("photo", sch.getPhoto());
						 values.put("video", sch.getVideo());
						 values.put("state", 1);
						long rowId = 0;
						if (cursor != null && cursor.getCount() > 0) { // update

							mDB.update("tb_schnotice", values, "id = ? ",new String[] { sch.getId() });

						} else {
							rowId = mDB.insert("tb_schnotice", null, values);
						}
					} catch (Exception e) {
						// TODO: handle exception
						
					}finally{
						 if(null!=cursor){
						    	cursor.close() ;
						    }
					}
					
		    	}
		    }
				
		
		} catch (Exception e) {
			// TODO: handle exception
			
			e.printStackTrace();
			
		}finally{
			
			mDB.close() ;
		}
	}
	
	
	/**
	 * 修改公告记录
	 * @param isall
	 * @param state
	 * @param id
	 */
	public synchronized void updateSchnotice(boolean isall,ArrayList<SchNotice> schList){
		
		openReadable() ;
		
		try{
			
			if(isall){
				mDB.execSQL("update tb_schnotice set state=? ",new String[]{"0"});
			}else{
				
				
				if(schList!=null&&schList.size()>0){
			    	for(SchNotice sch:schList){
			    		 try {
			    			 mDB.execSQL("update tb_schnotice set state=? where id=? ",new String[]{"1",sch.getId()});
							
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							
						}
						
			    	}
			    }
			}
			
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			
			mDB.close() ;
			
		}
		
		
	}
	
	/**
	 * 删除公告记录
	 * @param cid
	 */
	public synchronized void deleteSch(String id){
		openWritable() ;
		Cursor cursor = null ;
		ContentValues values=null;
		try{
			
			mDB.execSQL("DELETE FROM tb_schnotice WHERE id = ? ",new Object[] { id });

		}catch(Exception e){
			
		}finally{
			
			mDB.close() ;
		}
	}
	
	/**
	 * 插入刷卡数据
	 * @param schList
	 */
	public synchronized void insertOrSuaiCar(SuaiCar scdata){
		openWritable() ;
		ContentValues values=null;
		try{
			
		    if(scdata!=null){
		    	
		    		 try {
						
						 values = new ContentValues();
						 values.put("cid",scdata.getCid());//key为字段名，value为值
						 values.put("photo",scdata.getPhoto());
						 values.put("time", scdata.getTime());
						 values.put("state", scdata.getState());
						
						
						 long r= mDB.insert("tb_suaicar", null, values);
						 

						
						
					} catch (Exception e) {
						// TODO: handle exception
						
					}

		    }
				
		
		}catch(Exception e){
			
		}finally{
			
			mDB.close() ;
		}
	}
	
	/**
	 * 删除单条刷卡记录
	 * @param cid
	 */
	public synchronized void deleteSuaiCar(String cid){
		openWritable() ;
		Cursor cursor = null ;
		ContentValues values=null;
		try{
			
		//	mDB.execSQL("DELETE FROM tb_suaicar WHERE cid = ? ",new Object[] { cid });
			
			//int num = db.delete("userinfo", "name=?", new String[] {"hujintao"});
			
			mDB.delete("tb_suaicar", "photo = ?", new String[] {cid});
			

		}catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			
			mDB.close() ;
		}
	}
	
	/**
	 * 每次取十条刷卡数据
	 * @return
	 */
	public synchronized ArrayList<SuaiCar> queryScfeng(){

		openReadable() ;
		ArrayList<SuaiCar> suaicarList=new ArrayList<SuaiCar>();
		Cursor cursor =  mDB.rawQuery("select * from tb_suaicar order by cid limit ? offset ?",new String[]{"10","0"});
		try{
	
			if(cursor != null && cursor.getCount() > 0){
				while(cursor.moveToNext()){
					 SuaiCar scdata = new SuaiCar();
					 scdata.setCid(cursor.getString(cursor.getColumnIndex("cid")));
					 scdata.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
					 scdata.setTime(cursor.getString(cursor.getColumnIndex("time")));
					 scdata.setState(cursor.getInt(cursor.getColumnIndex("state")));
					
					suaicarList.add(scdata) ;
				}
			}
			
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			 if(null!=cursor){
			    	cursor.close() ;
			    }
			mDB.close() ;
		}
		return suaicarList ;
		
	}
	
	/**
	 * 查询刷卡数据
	 * @return
	 */
	public synchronized ArrayList<SuaiCar> querySuaiCar(){
		openReadable() ;
		ArrayList<SuaiCar> suaicarList=new ArrayList<SuaiCar>();
		Cursor cursor =  mDB.query("tb_suaicar", null, null, null, null, null, null) ; ;
		try{
	
			if(cursor != null && cursor.getCount() > 0){
				while(cursor.moveToNext()){
					 SuaiCar scdata = new SuaiCar();
					 scdata.setCid(cursor.getString(cursor.getColumnIndex("cid")));
					 scdata.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
					 scdata.setTime(cursor.getString(cursor.getColumnIndex("time")));
					 scdata.setState(cursor.getInt(cursor.getColumnIndex("state")));
					
					suaicarList.add(scdata) ;
				}
			}
			
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			 if(null!=cursor){
			    	cursor.close() ;
			    }
			mDB.close() ;
		}
		return suaicarList ;
	}
	
	/**
	 * 插入学生信息
	 * @param rid
	 * @param stus
	 */
	public synchronized void insertOrStu( String rid,Student stus){
		openWritable() ;
		Cursor cursor = null ;
		ContentValues values=null;
		long rowId = 0 ;
		try{
			
			if(stus!=null&&stus.getCids().size()>0&&stus.getMembers().size()>0){
				
				try{

					cursor = mDB.rawQuery("SELECT * FROM tb_stus WHERE id = ?",new String[] { stus.getId()});
					if(cursor != null && cursor.getCount() > 0){ // update
						 mDB.delete("tb_cids", "rid = ? ", new String[]{rid}) ;
						 mDB.delete("tb_member", "rid = ? ", new String[]{rid}) ;
						 values = new ContentValues();
						 values.put("id",stus.getId());//key为字段名，value为值
						 
						 rowId = mDB.update("tb_stus", values,  "id = ? ", new String[]{rid});

					}else{
						 values = new ContentValues();
						 values.put("id",stus.getId());//key为字段名，value为值

						 rowId = mDB.insert("tb_stus", null, values) ;

					}
					 
				}catch(Exception e){
					
					e.printStackTrace();
					
				}finally{
					 if(null!=cursor){
					    	cursor.close() ;
					    }
				}
			
				
				if(rowId>0){
					
					for(Card card:stus.getCids()){
						try{
							
							cursor = mDB.rawQuery("SELECT * FROM tb_cids WHERE cid = ?",new String[] {card.getCid()});
							
							if(null!=cursor&&cursor.getCount()>0){
								 values = new ContentValues();
								 values.put("cid",card.getCid());//key为字段名，value为值
								 values.put("rid",rid);
								 rowId = mDB.update("tb_cids", values,  "cid = ? ", new String[]{card.getCid()});
								 
							}else{
								
								 values = new ContentValues();
								 values.put("cid",card.getCid());//key为字段名，value为值
								 values.put("rid",rid);
								 mDB.insert("tb_cids", null, values) ;
							}

						}catch(Exception e){
							e.printStackTrace();
						}finally{
							 if(null!=cursor){
							    	cursor.close() ;
							    }
						}
					}
					
					for(Member member:stus.getMembers()){
						try{
							 values = new ContentValues();
							 values.put("rid",rid);//key为字段名，value为值
							 values.put("name",member.getName());
							 values.put("relatship", member.getRelatship());
							 values.put("clazz", member.getClazz());
							 values.put("age", member.getAge());
							 values.put("photo", member.getPhoto());
							 
							 rowId = mDB.insert("tb_member", null, values) ;
						}catch(Exception e){
							e.printStackTrace();
						}
					}

				}
				
			}

		}catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			mDB.close() ;
		}
	}
	
	/**
	 * 删除学生记录
	 * @param cid
	 */
	public synchronized void deleteStu(String rid){
		openWritable() ;
		Cursor cursor = null ;
		ContentValues values=null;
		try{
			 mDB.delete("tb_cids", "rid = ? ", new String[]{rid}) ;
			 mDB.delete("tb_member", "rid = ? ", new String[]{rid}) ;
			 mDB.execSQL("DELETE FROM tb_stus WHERE id = ? ",new Object[] { rid });

		}catch(Exception e){
			
		}finally{
			
			mDB.close() ;
		}
	}
	/**
	 * 查询学生信息
	 * @param cid
	 * @return
	 */
	public synchronized Student queryStus(String cid){
		openReadable() ;
		Student stus = new Student() ;
		Cursor cursor=null;
		try{
			 cursor = mDB.rawQuery("SELECT * FROM tb_cids WHERE cid = ?",new String[] { cid });
			
			if(cursor.moveToFirst()){
				String rid=cursor.getString(cursor.getColumnIndex("rid"));
				String cids=cursor.getString(cursor.getColumnIndex("cid"));
				if(rid!=null&&!"".equals(rid)){
					stus.setId(rid);
					 if(null!=cursor){
					    	cursor.close() ;
					    }
					
					try{
						ArrayList<Card> cards=new ArrayList<Card>();
						cursor = mDB.rawQuery("SELECT cid FROM tb_cids WHERE rid = ?",new String[] { rid });
						Card card=null;
						if(cursor != null && cursor.getCount() > 0){
							while(cursor.moveToNext()){
								card = new Card() ;
								card.setCid(cursor.getString(cursor.getColumnIndex("cid")));
								card.setRid(rid) ;
								cards.add(card) ;
							}
						}
						stus.setCids(cards);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						 if(null!=cursor){
						    	cursor.close() ;
						    }
					}
					
					try{
						ArrayList<Member> members=new ArrayList<Member>();
						cursor = mDB.rawQuery("SELECT * FROM tb_member WHERE rid = ?",new String[] { rid });
						Member member=null;
						if(cursor != null && cursor.getCount() > 0){
							while(cursor.moveToNext()){
								member = new Member() ;
								member.setRid(cursor.getString(cursor.getColumnIndex("rid"))) ;
								member.setName(cursor.getString(cursor.getColumnIndex("name"))) ;
								member.setRelatship(cursor.getString(cursor.getColumnIndex("relatship"))) ;
								member.setClazz(cursor.getString(cursor.getColumnIndex("clazz"))) ;
								member.setAge(cursor.getInt(cursor.getColumnIndex("age"))) ;
								member.setPhoto(cursor.getString(cursor.getColumnIndex("photo"))) ;
							
								members.add(member) ;
							}
						}
						
						stus.setMembers(members);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						 if(null!=cursor){
						    	cursor.close() ;
						    }
					}
					
				}else{
					 if(null!=cursor){
					    	cursor.close() ;
					    }
				}
			}
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			mDB.close() ;
		}
		
		return stus;
	}
	
	
	private Cursor query(String table_name){
		openReadable() ;
		Cursor cursor = null ;
		cursor = mDB.query(table_name, null, null, null, null, null, null) ;
		return cursor;
	}
	
	/**
	 * 查询公告数据
	 * @return
	 */
	public synchronized ArrayList<SchNotice> querySchnoticevideoandphoto(){
		openReadable() ;
		ArrayList<SchNotice> schs = new ArrayList<SchNotice>() ;
		//Cursor cursor =  mDB.query("tb_schnotice", null, null, null, null, null, null) ; 
		Cursor cursor =  mDB.rawQuery("select * from tb_schnotice where type>=1  and state=1 order by time desc limit ? offset ?",new String[]{"6","0"});
		try{
			if(cursor != null && cursor.getCount() > 0){
				while(cursor.moveToNext()){
					SchNotice sch = new SchNotice() ;
					sch.setId(cursor.getString(cursor.getColumnIndex("id")));
					sch.setTime(cursor.getLong(cursor.getColumnIndex("time")));
					sch.setType(cursor.getInt(cursor.getColumnIndex("type")));
					sch.setTitle(cursor.getString(cursor.getColumnIndex("title")));
					sch.setTxt(cursor.getString(cursor.getColumnIndex("txt")));
					sch.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
					sch.setVideo(cursor.getString(cursor.getColumnIndex("video")));
					
					schs.add(sch) ;
				}
			}
			
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			 if(null!=cursor){
			    	cursor.close() ;
			    }
			mDB.close() ;
		}
		return schs ;
	}
	

	
	/**
	 * 查询公告数据
	 * @return
	 */
	public synchronized ArrayList<SchNotice> querySchnotice(String type,String size){
		openReadable() ;
		ArrayList<SchNotice> schs = new ArrayList<SchNotice>() ;
		//Cursor cursor =  mDB.query("tb_schnotice", null, null, null, null, null, null) ; 
		Cursor cursor =  mDB.rawQuery("select * from tb_schnotice where type=? and state=1 order by time desc limit ? offset ?",new String[]{type,size,"0"});
		try{
			if(cursor != null && cursor.getCount() > 0){
				while(cursor.moveToNext()){
					SchNotice sch = new SchNotice() ;
					sch.setId(cursor.getString(cursor.getColumnIndex("id")));
					sch.setTime(cursor.getLong(cursor.getColumnIndex("time")));
					sch.setType(cursor.getInt(cursor.getColumnIndex("type")));
					sch.setTitle(cursor.getString(cursor.getColumnIndex("title")));
					sch.setTxt(cursor.getString(cursor.getColumnIndex("txt")));
					sch.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
					sch.setVideo(cursor.getString(cursor.getColumnIndex("video")));
					
					schs.add(sch) ;
				}
			}
			
		}catch(Exception  e){
			e.printStackTrace();
		}finally{
			 if(null!=cursor){
			    	cursor.close() ;
			    }
			mDB.close() ;
			
		}
		return schs ;
	}
	
	
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'tb_foot'";
	      //  db.execSQL(sql);
		 db.execSQL("DROP TABLE IF EXISTS  tb_suaicar ");
		
		//刷卡 数据保存 表
		 db.execSQL("CREATE TABLE IF NOT EXISTS 'tb_suaicar' (" + //
	                "'cid' VARCHAR(32)  ," + // 卡唯一ID
	                "'photo' VARCHAR(32)," +
	                "'time' VARCHAR(32)," + // 
				    "'state' INTEGER);"); // 
		
		
	}


}
