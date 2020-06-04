package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SItemType extends ItemComponentPacket {
    public C2SItemType() {
        super(new Identifier(Main.MOD_ID, "server_item_type"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final PlayerEntity player = context.getPlayer();

        player.inventory.removeOne(this.storage.getValidEquippedStack());

        if (!this.storage.isItemEquipped()) {
            this.storage.setCurrentTab(0);
        }

        SoulboundItemUtil.addItemStack(this.storage.getItemStack(), player);
//        this.component.sync();
    }
}
