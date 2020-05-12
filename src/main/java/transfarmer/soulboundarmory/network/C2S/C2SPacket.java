package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.PacketByteBuf;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

public abstract class C2SPacket implements PacketConsumer {
    @Override
    public void accept(final PacketContext context, final PacketByteBuf buffer) {
        this.accept(context, (ExtendedPacketBuffer) buffer);
    }

    protected abstract void accept(final PacketContext context, final ExtendedPacketBuffer buffer);
}
