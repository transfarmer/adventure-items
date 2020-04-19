package transfarmer.soulboundarmory.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class ExtendedPacketBuffer extends PacketBuffer {
    public ExtendedPacketBuffer(final ByteBuf wrapped) {
        super(wrapped);
    }

    public String readString() {
        return super.readString(1 << 10);
    }

    @Override
    public NBTTagCompound readCompoundTag() {
        try {
            return super.readCompoundTag();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
