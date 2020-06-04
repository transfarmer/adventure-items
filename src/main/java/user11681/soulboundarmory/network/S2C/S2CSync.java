package user11681.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;

public class S2CSync extends ItemComponentPacket {
    public S2CSync() {
        super(new Identifier("client_sync"));
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.storage.fromTag(buffer.readCompoundTag());
//        this.component.sync();
    }
}
