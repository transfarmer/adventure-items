package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.registry.Registries;

public class C2SAttribute extends ItemComponentPacket {
    public C2SAttribute() {
        super(new Identifier(Main.MOD_ID, "server_attribute"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.storage.incrementPoints(Registries.STATISTIC.get(buffer.readString()), buffer.readInt());
//        this.component.sync();
        this.storage.refresh();
    }
}
