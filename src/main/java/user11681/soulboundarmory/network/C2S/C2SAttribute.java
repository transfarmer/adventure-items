package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.component.statistics.StatisticType;

public class C2SAttribute extends ItemComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.component.addAttribute(StatisticType.valueOf(buffer.readString()), buffer.readInt());
//        this.component.sync();
        this.component.refresh();
    }
}
