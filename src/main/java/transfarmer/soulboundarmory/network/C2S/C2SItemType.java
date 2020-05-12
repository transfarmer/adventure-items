package transfarmer.soulboundarmory.network.C2S;

import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.network.S2C.S2CItemType;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public class C2SItemType implements IExtendedMessage {
    private String capability;
    private String item;

    public C2SItemType() {}

    public C2SItemType(final ICapabilityType capability, final IItem type) {
        this.capability = capability.toString();
        this.item = type.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeString(this.item);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SItemType> {
        @Override
        public IExtendedMessage onMessage(final C2SItemType message, final MessageContext context) {
            final IItem item = IItem.get(message.item);
            final PlayerEntityMP player = context.getServerHandler().player;
            final ICapabilityType type = ICapabilityType.get(message.capability);
            final ISoulboundComponent capability = player.getCapability(type.getCapability(), null);

            context.getServerHandler().player.server.addScheduledTask(() -> {
                player.inventory.deleteStack(capability.getEquippedItemStack());
                capability.setItemType(item);

                if (capability.hasSoulboundItem()) {
                    SoulboundItemUtil.removeSoulboundItems(player, capability.getBaseItemClass());
                } else {
                    capability.setCurrentTab(0);
                }

                SoulboundItemUtil.addItemStack(capability.getItemStack(item), player);
                capability.sync();
            });

            return new S2CItemType(capability, item);
        }
    }
}
