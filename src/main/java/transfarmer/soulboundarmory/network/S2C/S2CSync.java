package transfarmer.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.ItemComponentPacket;

public class S2CSync extends ItemComponentPacket {
    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.component.fromTag(buffer.readCompoundTag());
//        this.component.sync();
    }
}
