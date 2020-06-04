package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;

public class C2SSync extends ItemComponentPacket {
    public C2SSync() {
        super(new Identifier(Main.MOD_ID, "server_sync"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        context.getTaskQueue().execute(() -> {
            final CompoundTag tag = buffer.readCompoundTag();

            if (tag.contains("tab")) {
                storage.setCurrentTab(tag.getInt("tab"));
            }

            if (storage instanceof StaffStorage && tag.contains("spell")) {
                ((StaffStorage) storage).setSpell((tag.getInt("spell")));
            }
        });
    }
}
