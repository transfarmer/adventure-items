package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.component.config.IConfigComponent;
import transfarmer.soulboundarmory.network.common.Packet;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SConfig extends Packet {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        IConfigComponent.get(context.getPlayer()).setAddToOffhand(buffer.readBoolean());
    }
}
