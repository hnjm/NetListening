package de.terrarier.netlistening.utils;

/**
 * @since 1.0
 * @author Terrarier2111
 */
public final class ConversionUtil {
	
	private ConversionUtil() {}
	
	public static byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
	}

}
