package com.example.toroapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import java.util.zip.ZipOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;


public class ZipTool
{
	
	public static final int NULL_ZIPPATH = 0;

	
	public static final int NOTEXIST_ZIPFILE = 1;

	
	public static final int EXIST_UNZIPFILE = 2;

	
	public static final int ZIPOPTION_SUCCESS = 3;

	
	public static final int EXIST_ZIPFILE = 4;

	
	public static final int ZIPOPTION_FAIL = 5;


	public static int unzip(String zipFilePath, String unzipPath)
	{
		if (null == zipFilePath || "".equals(zipFilePath.trim()))
		{
			return NULL_ZIPPATH;
		}


		File dirFile = new File(unzipPath);
		/*if (dirFile.exists())
		{
			return EXIST_UNZIPFILE;
		}
		else
		{
			dirFile.mkdirs();
		}*/
		
		if (!dirFile.exists()){
			dirFile.mkdirs();
		}

		
		File file = new File(zipFilePath);
		if (!file.exists())
		{
			return NOTEXIST_ZIPFILE;
		}

		ZipFile zipFile = null;
		try
		{
			zipFile = new ZipFile(file,"GBK");
			
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return ZIPOPTION_FAIL;
		}

		
		Enumeration<? extends ZipEntry> entries = zipFile.getEntries();
		ZipEntry entry = null;
		String unzipAbpath = dirFile.getAbsolutePath();

		
		while (entries.hasMoreElements())
		{
			entry = entries.nextElement();
			unzipEachFile(zipFile, entry, unzipAbpath);
		}

		return ZIPOPTION_SUCCESS;
	}


	private static void unzipEachFile(ZipFile zipFile, ZipEntry entry, String unzipAbpath)
	{
		byte[] buffer = new byte[1024 * 5];
		int readSize = 0;
		String name = entry.getName();
		String fileName = name;
		int index = 0;
		String tempDir = "";

		
		if (entry.isDirectory())
		{
			File tempFile = new File(unzipAbpath + File.separator + name);
			if (!tempFile.exists())
			{
				tempFile.mkdirs();
			}

			return;
		}

		
		if ((index = name.lastIndexOf(File.separator)) != -1)
		{
			fileName = name.substring(index, name.length());
			tempDir = name.substring(0, index);
			File tempDirFile = new File(unzipAbpath + File.separator + tempDir);
			if (!tempDirFile.exists())
			{
				tempDirFile.mkdirs();
			}
		}

		
		String zipPath = unzipAbpath + File.separator + tempDir + fileName;
		File tempFile = new File(zipPath);
		InputStream is = null;
		FileOutputStream fos = null;
		try
		{
			
			is = zipFile.getInputStream(entry);
			if (!tempFile.exists())
			{
				tempFile.createNewFile();
			}

			fos = new FileOutputStream(tempFile);
			while ((readSize = is.read(buffer)) > 0)
			{
				fos.write(buffer, 0, readSize);
			}
		}
		catch (Exception e)
		{
			new File(unzipAbpath).delete();
			e.printStackTrace();
		}
		finally
		{
			try
			{
				is.close();
				fos.close();
			}
			catch (IOException e)
			{
				new File(unzipAbpath).delete();
				e.printStackTrace();
			}
		}

	}


	public static int zip(String newZipPath, File[] wantZipFile)
	{
		
		File file = new File(newZipPath);
		if (file.exists())
		{
			return EXIST_ZIPFILE;
		}

		
		String filePath = file.getAbsolutePath();
		String basePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
		File dirFile = new File(basePath);
		if (!dirFile.exists())
		{
			dirFile.mkdirs();
		}

		ZipOutputStream zos = null;
		try
		{
			
			if (!file.exists())
			{
				file.createNewFile();
			}

			
			FileOutputStream fos = new FileOutputStream(file);
			zos = new ZipOutputStream(fos);
			for (File tempFile : wantZipFile)
			{
				if (!tempFile.exists())
				{
					continue;
				}

				
				zipEachFile(zos, tempFile, "");
			}
		}
		catch (IOException ie)
		{
			file.delete();		
			return ZIPOPTION_FAIL;
		}
		finally
		{
			try
			{
				zos.close();
			}
			catch (IOException e)
			{
				file.delete();		
				e.printStackTrace();
				return ZIPOPTION_FAIL;
			}
		}

		return ZIPOPTION_SUCCESS;
	}


	private static void zipEachFile(ZipOutputStream zos, File zipFile, String baseDir)
	{
		ZipEntry entry = null;

		
		if (zipFile.isDirectory())
		{
			
			baseDir = baseDir + zipFile.getName() + File.separator;
			File[] tempFiles = zipFile.listFiles();
			for (File tempFile : tempFiles)
			{
				zipEachFile(zos, tempFile, baseDir);
			}
		}

	
		else
		{
			FileInputStream fis = null;
			try
			{
				entry = new ZipEntry(baseDir + zipFile.getName());
				zos.putNextEntry(entry);
				fis = new FileInputStream(zipFile);
				byte[] buffer = new byte[1024 * 5];
				int readSize = 0;
				while ((readSize = fis.read(buffer)) > 0)
				{
					zos.write(buffer, 0, readSize);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					fis.close();
					zos.closeEntry();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
