package user11681.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;

public class S2CRefresh extends ItemComponentPacket {
    public S2CRefresh() {
        super(new Identifier(Main.MOD_ID, "client_refresh"));
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        storage.refresh();
    }
}
