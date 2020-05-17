package transfarmer.soulboundarmory.network.common;

import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.component.Component;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.PacketByteBuf;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

public abstract class ItemComponentPacket extends Packet {
    protected ISoulboundItemComponent<? extends Component> component;

    @Override
    public void accept(final PacketContext context, final PacketByteBuf buffer) {
        context.getTaskQueue().execute(() -> {
            this.component = (ISoulboundItemComponent<? extends Component>) ComponentRegistry.INSTANCE.get(buffer.readIdentifier()).get(ComponentProvider.fromEntity(context.getPlayer()));
            this.accept(context, (ExtendedPacketBuffer) buffer);
        });
    }
}
