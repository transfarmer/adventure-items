package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SBindSlot extends ItemComponentPacket {
    public C2SBindSlot() {
        super(new Identifier(Main.MOD_ID, "server_bind_slot"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final int slot = buffer.readInt();

        if (this.storage.getBoundSlot() == slot) {
            this.storage.unbindSlot();
        } else {
            this.storage.bindSlot(slot);
        }

//        this.component.sync();
        this.storage.refresh();
    }
}
