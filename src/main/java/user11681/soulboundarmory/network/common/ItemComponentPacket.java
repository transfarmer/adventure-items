package user11681.soulboundarmory.network.common;

import nerdhub.cardinal.components.api.ComponentRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.PacketByteBuf;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;

public abstract class ItemComponentPacket extends Packet {
    protected PickStorage component;

    @Override
    public void accept(final PacketContext context, final PacketByteBuf buffer) {
        context.getTaskQueue().execute(() -> {
            this.component = (PickStorage) ComponentRegistry.INSTANCE.get(buffer.readIdentifier()).get(context.getPlayer());
            this.accept(context, (ExtendedPacketBuffer) buffer);
        });
    }
}
