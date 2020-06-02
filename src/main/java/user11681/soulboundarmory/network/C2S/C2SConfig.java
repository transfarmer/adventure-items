package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import user11681.soulboundarmory.component.config.IConfigComponent;
import user11681.soulboundarmory.network.common.Packet;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SConfig extends Packet {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        IConfigComponent.get(context.getPlayer()).setAddToOffhand(buffer.readBoolean());
    }
}
