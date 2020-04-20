package transfarmer.soulboundarmory.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.ICapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
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
            final EntityPlayerMP player = context.getServerHandler().player;
            final ICapability capability = player.getCapability(ICapabilityType.get(message.capability).getCapability(), null);

            capability.setItemType(item);

            if (!capability.hasSoulItem()) {
                capability.setCurrentTab(0);
                player.inventory.deleteStack(player.getHeldItemMainhand());
                Main.CHANNEL.sendToServer(new C2STab(capability.getType(), capability.getCurrentTab()));
            }

            SoulItemHelper.addItemStack(capability.getItemStack(item), player);
            capability.sync();

            return null;
        }
    }
}
