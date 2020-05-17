package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.network.common.ItemComponentPacket;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.StatisticType;

public class C2SAttribute extends ItemComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.component.addAttribute(StatisticType.valueOf(buffer.readString()), buffer.readInt());
//        this.component.sync();
        this.component.refresh();
    }
}
