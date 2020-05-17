package transfarmer.soulboundarmory.network.common;

import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.statistics.IItem;

public abstract class ComponentPacket extends Packet {
    protected ISoulboundItemComponent component;
    protected IItem item;

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.component = ComponentProvider.fromEntity(context.getPlayer()).getComponent(IComponentType.get(buffer.readIdentifier()));
        this.item = IItem.get(buffer.readString());
    }
}
