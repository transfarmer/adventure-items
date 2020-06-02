package user11681.soulboundarmory.network.common;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.PacketByteBuf;

public abstract class Packet implements PacketConsumer {
    @Override
    public void accept(final PacketContext context, final PacketByteBuf buffer) {
        context.getTaskQueue().execute(() -> this.accept(context, (ExtendedPacketBuffer) buffer));
    }

    protected abstract void accept(final PacketContext context, final ExtendedPacketBuffer buffer);
}
