package user11681.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;

public class S2CItemType extends ItemComponentPacket {
    public S2CItemType() {
        super(new Identifier(Main.MOD_ID, "client_item_type"));
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final PlayerEntity player = context.getPlayer();

        player.inventory.removeOne(storage.getValidEquippedStack());

        if (!storage.isItemEquipped()) {
            storage.setCurrentTab(0);
        }

        SoulboundItemUtil.addItemStack(storage.getItemStack(), player);
        storage.refresh();
    }
}
