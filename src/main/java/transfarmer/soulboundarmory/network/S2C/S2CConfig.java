package transfarmer.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.Packet;

import static transfarmer.soulboundarmory.MainClient.CLIENT;

public class S2CConfig extends Packet {
    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        CLIENT.execute(() -> MainConfig.instance());
    }
}
