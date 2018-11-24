package app.com.bledemo.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Convert {

	public final static char[] hexArray = "0123456789abcdef".toCharArray();

	private Convert() {

	}

	/**
	 * 把16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		hex = hex.trim();
		if(hex.startsWith("0x") || hex.startsWith("0X")){
			hex = hex.substring(2,hex.length());
		}
		if(hex.length() %2 != 0){
			hex = "0"+hex;
		}
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
					+ Character.digit(hex.charAt(i+1), 16));
		}
		return data;
	}


	/**
	 * 把字节数组转换成16进制字符串
	 *
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		return bytesToHexString(bArray,false);
	}

	/**
	 * 把字节数组转换成16进制字符串
	 *
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexTrimString(byte[] bArray) {
		return bytesToHexString(bArray,true);
	}

	/**
	 * 把字节数组转换成16进制字符串
	 *
	 * @return
	 */
	public static final String bytesToHexString(byte[] bytes, boolean trim) {
		if(bytes == null){
			return "";
		}
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		String hexStr = new String(hexChars);
		if(trim){
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < hexStr.length(); ) {
				sb.append(hexStr.substring(i,i+2)).append(" ");
				i += 2;
			}
			return sb.toString().toLowerCase();
		}
		return hexStr.toLowerCase();
	}

	/**
	 * 把字节数组转换为对象
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Object bytesToObject(byte[] bytes) throws IOException,
            ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return o;
	}

	/**
	 * 把可序列化对象转换成字节数组
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static final byte[] objectToBytes(Serializable s) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream ot = new ObjectOutputStream(out);
		ot.writeObject(s);
		ot.flush();
		ot.close();
		return out.toByteArray();
	}

	public static final String objectToHexString(Serializable s)
			throws IOException {
		return bytesToHexString(objectToBytes(s));
	}

	public static final Object hexStringToObject(String hex)
			throws IOException, ClassNotFoundException {
		return bytesToObject(hexStringToByte(hex));
	}

	/**
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @输入参数: BCD码
	 * @输出结果: 10进制串
	 */
	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	/**
	 * @函数功能: 10进制串转为BCD码
	 * @输入参数: 10进制串
	 * @输出结果: BCD码
	 */
	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	public static String BCD2ASC(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			int h = ((bytes[i] & 0xf0) >>> 4);
			int l = (bytes[i] & 0x0f);
			temp.append(hexArray[h]).append(hexArray[l]);
		}
		return temp.toString();
	}

	/**
	 * 两字符数组异或
	 * @author wanggh
	 * 
	 */
	public static byte[] byteArrXor(byte[] arr1, byte[] arr2, int len){
		byte[] dest = new byte[len];
		
		if((arr1.length < len) || (arr2.length < len)){
			return null;
		}
		
		for(int i = 0;i < len;i++){
			dest[i] = (byte)(arr1[i] ^ arr2[i]);
		}
		
		return dest;
	}
	

	/**
	 * MD5加密字符串，返回加密后的16进制字符串
	 * 
	 * @param origin
	 * @return
	 */
	public static String MD5EncodeToHex(String origin) {
		return bytesToHexString(MD5Encode(origin));
	}

	/**
	 * MD5加密字符串，返回加密后的字节数组
	 * 
	 * @param origin
	 * @return
	 */
	public static byte[] MD5Encode(String origin) {
		return MD5Encode(origin.getBytes());
	}

	/**
	 * MD5加密字节数组，返回加密后的字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] MD5Encode(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}

	}
}
