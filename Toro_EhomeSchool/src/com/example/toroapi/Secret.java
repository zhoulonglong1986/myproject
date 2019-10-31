package com.example.toroapi;

import java.util.Formatter;

public class Secret {

	public static String dict(int pos, String strUUID) {
		int length = (strUUID.length() - pos >= 32 ? 32
				: (strUUID.length() - pos));

		StringBuilder sb = new StringBuilder();
		sb.append(strUUID.substring(pos, pos + length));
		sb.append(strUUID.substring(0, 32 - length));

		return sb.toString();
	}

	public static void blur(byte[] data, int pos, int size, String strDict) {
		byte dict[] = strDict.getBytes();
		int i = pos;

		while (i < size + pos) {
			byte bit = dict[(i - pos) - (((i - pos) >> 3) << 3)];
			bit = (byte) (bit - ((bit >> 3) << 3));
			data[i] = (byte) ((((data[i] & 0x00ff) << bit) & 0x00ff) | ((data[i] & 0x00ff) >> (8 - bit)) & 0x00ff);
			i++;
		}
	}

	public static byte[] encode(String UUID, String str) {
		int seed = (int) (Math.random() * 32);
		String strDict = dict(seed, UUID);
		StringBuilder body = new StringBuilder();
		body.append(UUID);
		body.append(new Formatter().format("%02x", seed).toString());
		body.append(str);
		byte[] data = body.toString().getBytes();
		blur(data, 38, data.length - 38, strDict);
		return data;
	}
}
