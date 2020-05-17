package transfarmer.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.ItemComponentPacket;

public class S2CItemType extends ItemComponentPacket {
    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final PlayerEntity player = context.getPlayer();

        player.inventory.removeOne(component.getValidEquippedStack());

        if (!component.isItemEquipped()) {
            component.setCurrentTab(0);
        }

        SoulboundItemUtil.addItemStack(component.getItemStack(), player);
        component.refresh();
    }
}
