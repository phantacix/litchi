//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;


/**
 * 16进制工具类
 * @author 0x737263
 *
 */
public class HexUtils {
	static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	public static byte[] intToBytes2(int n) {
		byte[] b = new byte[4];

		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (n >> (24 - i * 8));

		}
		return b;
	}
	
	public static String hex(int value) {
		byte[] bytes = intToBytes2(value);
		return hex(bytes);
	}
	
	public static String hex(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return "";
		char[] result = new char[bytes.length * 2];

		for (int i = 0; i < bytes.length; i++) {
			result[i * 2] = HEX_DIGITS[(bytes[i] >> 4) & 0xf];
			result[i * 2 + 1] = HEX_DIGITS[bytes[i] & 0xf];
		}
		return new String(result);
	}
	
	public static byte[] decodeHex(String hex) {
		if (hex == null)
			throw new IllegalArgumentException("hex == null");
		if (hex.length() % 2 != 0)
			throw new IllegalArgumentException("Unexpected hex string: " + hex);

		byte[] result = new byte[hex.length() / 2];
		for (int i = 0; i < result.length; i++) {
			int d1 = decodeHexDigit(hex.charAt(i * 2)) << 4;
			int d2 = decodeHexDigit(hex.charAt(i * 2 + 1));
			result[i] = (byte) (d1 + d2);
		}
		return result;
	}
	
    private static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9')
            return c - '0';
        if (c >= 'a' && c <= 'f')
            return c - 'a' + 10;
        if (c >= 'A' && c <= 'F')
            return c - 'A' + 10;
        throw new IllegalArgumentException("Unexpected hex digit: " + c);
    }

	public static String byte2Hex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		String stmp = "";
		for (int n = 0; (bytes != null) && (n < bytes.length); ++n) {
			stmp = Integer.toHexString(bytes[n] & 0xFF);
			if (stmp.length() == 1)
				builder.append("0").append(stmp);
			else {
				builder.append(stmp);
			}
		}
		return builder.toString().toLowerCase();
	}
	
}