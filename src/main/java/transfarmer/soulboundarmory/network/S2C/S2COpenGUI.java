package transfarmer.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.client.gui.screen.common.SoulboundTab;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.ItemComponentPacket;

import static transfarmer.soulboundarmory.MainClient.CLIENT;

public class S2COpenGUI extends ItemComponentPacket {
    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        if (CLIENT.currentScreen instanceof SoulboundTab) {
            this.component.openGUI(buffer.readInt());
        }
    }
}
