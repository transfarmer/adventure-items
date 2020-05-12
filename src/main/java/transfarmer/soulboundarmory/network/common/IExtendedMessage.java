package transfarmer.soulboundarmory.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IExtendedMessage extends IMessage {
    void fromBytes(ExtendedPacketBuffer buffer);

    void toBytes(ExtendedPacketBuffer buffer);

    @Override
    default void fromBytes(final ByteBuf buffer) {
        this.fromBytes(new ExtendedPacketBuffer(buffer));
    }

    @Override
    default void toBytes(final ByteBuf buffer) {
        this.toBytes(new ExtendedPacketBuffer(buffer));
    }
}
