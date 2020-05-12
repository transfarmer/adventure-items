package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CItemType extends S2CSoulbound {
    public S2CItemType() {}

    public S2CItemType(final ISoulboundComponent capability, final IItem item) {
        super(capability, item);
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
                    final ISoulboundComponent capability = message.capability;
                    final IItem item = message.item;
                    final PlayerEntity player = message.player;

                    player.inventory.deleteStack(capability.getEquippedItemStack());
                    capability.setItemType(item);

                    if (capability.hasSoulboundItem()) {
                        SoulboundItemUtil.removeSoulboundItems(player, capability.getBaseItemClass());
                    } else {
                        capability.setCurrentTab(0);
                    }

                    SoulboundItemUtil.addItemStack(capability.getItemStack(item), player);
                    capability.refresh();
                }
            });

            return null;
        }
    }
}
