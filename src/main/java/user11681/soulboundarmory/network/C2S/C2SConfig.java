package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.config.IConfigComponent;
import user11681.soulboundarmory.network.common.Packet;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SConfig extends Packet {
    public C2SConfig() {
        super(new Identifier(Main.MOD_ID, "server_config"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        IConfigComponent.get(context.getPlayer()).setAddToOffhand(buffer.readBoolean());
    }
}
