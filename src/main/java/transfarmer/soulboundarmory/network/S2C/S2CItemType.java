package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CItemType extends S2CSoulbound {
    public S2CItemType() {}

    public S2CItemType(final ISoulboundComponent component, final IItem item) {
        super(component, item);
    }

    @Override
    @Environment(CLIENT)
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        super.fromBytes(buffer);
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        super.toBytes(buffer);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CItemType> {
        @Environment(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CItemType message, final MessageContext context) {
            CLIENT.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    final ISoulboundComponent component = component;
                    final IItem item = item;
                    final PlayerEntity player = player;

                    player.inventory.deleteStack(component.getEquippedItemStack());
                    component.setItemType(item);

                    if (component.hasSoulboundItem()) {
                        SoulboundItemUtil.removeSoulboundItems(player, component.getBaseItemClass());
                    } else {
                        component.setCurrentTab(0);
                    }

                    SoulboundItemUtil.addItemStack(component.getItemStack(item), player);
                    component.refresh();
                }
            });

            return null;
        }
    }
}
