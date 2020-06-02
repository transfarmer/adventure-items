package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SItemType extends ItemComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final PlayerEntity player = context.getPlayer();

        player.inventory.removeOne(this.component.getValidEquippedStack());

        if (!this.component.isItemEquipped()) {
            this.component.setCurrentTab(0);
        }

        SoulboundItemUtil.addItemStack(this.component.getItemStack(), player);
//        this.component.sync();
    }
}
