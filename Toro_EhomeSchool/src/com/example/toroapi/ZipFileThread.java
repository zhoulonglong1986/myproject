package com.example.toroapi;

import java.io.File;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class ZipFileThread extends Thread
{
	
	private Handler handler;

	
	private File file;
	
	
	private String handlePath;

	
	private File[] files;

	private boolean isZip;

	@Override
	public void run()
	{
		
		Message msg = null;
		int msgValue = -1;
		if (isZip)
		{
			msgValue = ZipTool.zip(handlePath + ".zip", new File[] { file });
		}
		else
		{
			String filePath = file.getAbsolutePath();
			msgValue = ZipTool.unzip(filePath, handlePath);
		}

		msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("OPTION_STATUS", msgValue);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public File[] getFiles()
	{
		return files;
	}

	public void setFiles(File[] files)
	{
		this.files = files;
	}

	public boolean isZip()
	{
		return isZip;
	}

	public void setZip(boolean isZip)
	{
		this.isZip = isZip;
	}

	public String getHandlePath()
	{
		return handlePath;
	}

	public void setHandlePath(String handlePath)
	{
		this.handlePath = handlePath;
	}

}
