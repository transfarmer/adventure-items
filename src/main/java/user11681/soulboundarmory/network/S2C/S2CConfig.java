package user11681.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.Packet;

import static user11681.soulboundarmory.MainClient.CLIENT;

public class S2CConfig extends Packet {
    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        CLIENT.execute(() -> Configuration.instance());
    }
}
