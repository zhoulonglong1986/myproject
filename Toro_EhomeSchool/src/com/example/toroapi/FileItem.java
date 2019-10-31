package com.example.toroapi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * �ļ�Ԫ���ݡ�
 * 
 * @author carver.gu
 * @since 1.0, Sep 12, 2009
 */
public class FileItem {

	private String fileName;
	private String mimeType;
	private byte[] content;
	private File file;

	/**
	 * ���ڱ����ļ��Ĺ�������
	 * 
	 * @param file
	 *            �����ļ�
	 */
	public FileItem(File file) {
		this.file = file;
	}

	/**
	 * �����ļ�����·���Ĺ�������
	 * 
	 * @param filePath
	 *            �ļ�����·��
	 */
	public FileItem(String filePath) {
		this(new File(filePath));
	}

	/**
	 * �����ļ������ֽ����Ĺ�������
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param content
	 *            �ļ��ֽ���
	 */
	public FileItem(String fileName, byte[] content) {
		this.fileName = fileName;
		this.content = content;
	}

	/**
	 * �����ļ������ֽ�����ý�����͵Ĺ�������
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param content
	 *            �ļ��ֽ���
	 * @param mimeType
	 *            ý������
	 */
	public FileItem(String fileName, byte[] content, String mimeType) {
		this(fileName, content);
		this.mimeType = mimeType;
	}

	public String getFileName() {
		if (this.fileName == null && this.file != null && this.file.exists()) {
			this.fileName = file.getName();
		}
		return this.fileName;
	}

	public String getMimeType() throws IOException {
		if (this.mimeType == null) {
			// this.mimeType = TogetherUtils.getMimeType(getContent());
		}
		return this.mimeType;
	}

	public byte[] getContent() throws IOException {
		if (this.content == null && this.file != null && this.file.exists()) {
			InputStream in = null;
			ByteArrayOutputStream out = null;

			try {
				in = new FileInputStream(this.file);
				out = new ByteArrayOutputStream();
				int ch;
				while ((ch = in.read()) != -1) {
					out.write(ch);
				}
				this.content = out.toByteArray();
			} finally {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			}
		}
		return this.content;
	}

}
