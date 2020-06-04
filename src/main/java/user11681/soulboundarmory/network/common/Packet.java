package user11681.soulboundarmory.network.common;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import user11681.usersmanual.registry.AbstractRegistryEntry;

public abstract class Packet extends AbstractRegistryEntry implements PacketConsumer {
    public Packet(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public void accept(final PacketContext context, final PacketByteBuf buffer) {
        context.getTaskQueue().execute(() -> this.accept(context, (ExtendedPacketBuffer) buffer));
    }

    protected abstract void accept(final PacketContext context, final ExtendedPacketBuffer buffer);
}
