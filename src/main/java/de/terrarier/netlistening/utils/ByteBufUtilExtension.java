package de.terrarier.netlistening.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.EmptyArrays;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @since 1.0
 * @author Terrarier2111
 */
public final class ByteBufUtilExtension {
	
	private static boolean newNettyVersion;
	
	static {
		try {
			ByteBufUtil.class.getDeclaredMethod("getBytes", ByteBuf.class, int.class, int.class);
			newNettyVersion = true;
		}catch(NoSuchMethodException ignored) {
			// Apparently we are using an old version of netty and we have to support
			// getBytes on our own and can't rely on Netty's ByteBufUtil#getBytes method.
		}
	}
	
	private ByteBufUtilExtension() {}
	
	public static byte[] readBytes(@NotNull ByteBuf buffer, int bytes) {
		if(bytes == 0) {
			return EmptyArrays.EMPTY_BYTES;
		}

		final byte[] read = getBytes(buffer, bytes);
		
		buffer.skipBytes(bytes);
		return read;
	}

	public static byte[] readBytes(@NotNull ByteBuf buffer) {
		return readBytes(buffer, buffer.readableBytes());
	}
	
	public static void writeBytes(@NotNull ByteBuf buf, byte[] bytes, int buffer) {
		final int length = bytes.length;
		correctSize(buf, 4 + length, buffer);
		buf.writeInt(length);
		if(length > 0) {
			buf.writeBytes(bytes);
		}
	}
	
	public static void correctSize(@NotNull ByteBuf buf, int bytes, int buffer) {
		final int capacity = buf.capacity();
		final int available = capacity - (buf.writerIndex() + bytes);
		
		if(available < 0) {
			buf.capacity(capacity - available + buffer);
		}
	}

	public static byte[] getBytes(@NotNull ByteBuf buf, int bytes) {
		if(bytes == 0) {
			return EmptyArrays.EMPTY_BYTES;
		}

		return newNettyVersion ? ByteBufUtil.getBytes(buf, buf.readerIndex(), bytes) : getBytes0(buf, bytes);
	}

	public static byte[] getBytes(@NotNull ByteBuf buf) {
		return getBytes(buf, buf.readableBytes());
	}
	
	/**
	 * Copied from netty to allow the usage of an older netty version:
	 * 
	 * @see <a href="https://github.com/netty/netty/blob/4.1/buffer/src/main/java/io/netty/buffer/ByteBufUtil.java">https://github.com/netty/netty/blob/4.1/buffer/src/main/java/io/netty/buffer/ByteBufUtil.java</a>
	 */

	private static byte[] getBytes0(@NotNull ByteBuf buffer, int length) {
		int start = buffer.readerIndex();
        int capacity = buffer.capacity();
        
        if (isOutOfBounds(start, length, capacity))
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" + length
                    + ") <= " + "buf.capacity(" + capacity + ')');

		if (buffer.hasArray()) {
			int baseOffset = buffer.arrayOffset() + start;
			byte[] bytes = buffer.array();
			if (/*copy || */baseOffset != 0 || length != bytes.length) {
				return Arrays.copyOfRange(bytes, baseOffset, baseOffset + length);
			}else {
				return bytes;
			}
			// return buf.array();
		}

		byte[] bytes = new byte[length];
        buffer.getBytes(start, bytes);
        return bytes;
    }
	
	/**
	 * Copied from netty to allow the usage of an older netty version:
	 * 
	 * https://github.com/netty/netty/blob/4.1/common/src/main/java/io/netty/util/internal/MathUtil.java
	 */
	
	private static boolean isOutOfBounds(int index, int length, int capacity) {
        return (index | length | (index + length) | (capacity - (index + length))) < 0;
    }

}
