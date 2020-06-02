package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SBindSlot extends ItemComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final int slot = buffer.readInt();

        if (this.component.getBoundSlot() == slot) {
            this.component.unbindSlot();
        } else {
            this.component.bindSlot(slot);
        }

//        this.component.sync();
        this.component.refresh();
    }
}
