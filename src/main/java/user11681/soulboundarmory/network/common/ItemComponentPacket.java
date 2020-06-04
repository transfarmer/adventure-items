package user11681.soulboundarmory.network.common;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.registry.Registries;

public abstract class ItemComponentPacket extends Packet {
    protected ItemStorage<?> storage;

    public ItemComponentPacket(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public void accept(final PacketContext context, final PacketByteBuf buffer) {
        final ExtendedPacketBuffer extendedBuffer = new ExtendedPacketBuffer(buffer.copy());
        this.storage = ItemStorage.get(context.getPlayer(), Registries.STORAGE_TYPE.get(extendedBuffer.readIdentifier()));

        context.getTaskQueue().execute(() -> {
            this.accept(context, extendedBuffer);
        });
    }
}
