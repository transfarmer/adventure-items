package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.network.common.ComponentPacket;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SItemType extends ComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        super.accept(context, buffer);

        final PlayerEntity player = context.getPlayer();

        player.inventory.removeOne(this.component.getEquippedItemStack());
        this.component.setItemType(item);

        if (this.component.hasSoulboundItem()) {
            SoulboundItemUtil.removeSoulboundItems(player, this.component.getBaseItemClass());
        } else {
            this.component.setCurrentTab(0);
        }

        SoulboundItemUtil.addItemStack(this.component.getItemStack(item), player);
        this.component.sync();
    }
}
