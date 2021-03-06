package de.terrarier.netlistening.internals;

import de.terrarier.netlistening.Application;
import de.terrarier.netlistening.api.encryption.hash.HashUtil;
import de.terrarier.netlistening.api.event.LengthExtensionDetectionEvent;
import de.terrarier.netlistening.api.type.DataType;
import de.terrarier.netlistening.impl.ConnectionImpl;
import de.terrarier.netlistening.network.PacketDataDecoder;
import de.terrarier.netlistening.utils.ByteBufUtilExtension;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @since 1.0
 * @author Terrarier2111
 */
public final class DataTypeHmac extends DataType<Void> {

    public DataTypeHmac() {
        super((byte) 0xF, 6, false);
    }

    @Override
    public Void read0(@NotNull ChannelHandlerContext ctx, @NotNull List<Object> data, @NotNull Application application,
            @NotNull ByteBuf buffer) throws Exception {
        checkReadable(buffer, 6);
        final int size = buffer.readInt();
        checkReadable(buffer, size, true);
        final short hashSize = buffer.readShort();
        final int sumSize = size + hashSize;
        if (buffer.readableBytes() < sumSize) {
            buffer.readerIndex(buffer.readerIndex() - 6);
            throw new CancelReadingSignal(sumSize + 6);
        }
        final byte[] traffic = ByteBufUtilExtension.readBytes(buffer, size);
        final byte[] hash = ByteBufUtilExtension.readBytes(buffer, hashSize);
        final ConnectionImpl connection = (ConnectionImpl) application.getConnection(null);
        final byte[] computedHash = HashUtil.calculateHMAC(traffic, connection.getHmacKey(),
                application.getEncryptionSetting().getHmacSetting().getHashingAlgorithm());
        if(!HashUtil.isHashMatching(hash, computedHash)) {
            final LengthExtensionDetectionEvent event = new LengthExtensionDetectionEvent(hash, computedHash);
            if(event.getResult() == LengthExtensionDetectionEvent.Result.DROP_DATA) {
                return null;
            }
        }
        final PacketDataDecoder decoder = (PacketDataDecoder) ctx.channel().pipeline().get("decoder");
        final ByteBuf dataBuffer = Unpooled.wrappedBuffer(traffic);
        decoder.releaseNext();
        decoder.decode(ctx, dataBuffer, data);
        return null;
    }

    @Override
    protected Void read(@NotNull Application application, @NotNull Channel channel, @NotNull ByteBuf buffer) {
        return null;
    }

    @Override
    protected void write(@NotNull Application application, @NotNull ByteBuf buffer, Void empty) {}

}
