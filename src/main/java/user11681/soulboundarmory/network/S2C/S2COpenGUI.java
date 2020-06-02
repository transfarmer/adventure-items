package user11681.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import user11681.soulboundarmory.client.gui.screen.common.SoulboundTab;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;

import static user11681.soulboundarmory.MainClient.CLIENT;

public class S2COpenGUI extends ItemComponentPacket {
    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        if (CLIENT.currentScreen instanceof SoulboundTab) {
            this.component.openGUI(buffer.readInt());
        }
    }
}
