package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.network.common.ComponentPacket;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.StatisticType;

public class C2SAttribute extends ComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        super.accept(context, buffer);

        this.component.addAttribute(this.item, StatisticType.get(buffer.readString()), buffer.readInt());
        this.component.sync();
        this.component.refresh();
    }
}
