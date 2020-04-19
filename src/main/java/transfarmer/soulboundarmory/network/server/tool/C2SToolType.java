package transfarmer.soulboundarmory.network.server.tool;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public class C2SToolType implements IExtendedMessage {
    private String item;

    public C2SToolType() {}

    public C2SToolType(final IItem type) {
        this.item = type.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.item);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SToolType> {
        @Override
        public IExtendedMessage onMessage(final C2SToolType message, final MessageContext context) {
            final IItem item = IItem.get(message.item);
            final EntityPlayerMP player = context.getServerHandler().player;
            final IItemCapability capability = ToolProvider.get(player);
            capability.setItemType(item);

            if (!capability.hasSoulItem()) {
                player.inventory.deleteStack(player.getHeldItemMainhand());
            }

            SoulItemHelper.addItemStack(new ItemStack(capability.getItem()), player);

            return null;
        }
    }
}
