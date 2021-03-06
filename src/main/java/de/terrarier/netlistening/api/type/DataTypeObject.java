package de.terrarier.netlistening.api.type;

import de.terrarier.netlistening.Application;
import de.terrarier.netlistening.api.serialization.SerializationProvider;
import de.terrarier.netlistening.api.serialization.SerializationUtil;
import de.terrarier.netlistening.internals.CancelReadingSignal;
import de.terrarier.netlistening.utils.ByteBufUtilExtension;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0
 * @author Terrarier2111
 */
public final class DataTypeObject extends DataType<Object> {
	
	protected DataTypeObject() {
		super((byte) 0x8, 4, true);
	}

	@Override
	protected Object read(@NotNull Application application, @NotNull Channel channel, @NotNull ByteBuf buffer)
			throws CancelReadingSignal {
		final int length = buffer.readInt();

		if(length == 0) {
			return SerializationProvider.SERIALIZATION_ERROR;
		}
		checkReadable(buffer, length, true);

		final byte[] bytes = ByteBufUtilExtension.readBytes(buffer, length);
		final Object deserialized = SerializationUtil.deserialize(application, bytes);

		if(deserialized == null) {
			return SerializationProvider.SERIALIZATION_ERROR;
		}

		return deserialized;
	}

	@Override
	public void write(@NotNull Application application, @NotNull ByteBuf buffer, @NotNull Object data) {
		final byte[] serialized = SerializationUtil.serialize(application, data);
		if(serialized == null) {
			buffer.writeInt(0);
			return;
		}
		ByteBufUtilExtension.writeBytes(buffer, serialized, application.getBuffer());
	}

}
