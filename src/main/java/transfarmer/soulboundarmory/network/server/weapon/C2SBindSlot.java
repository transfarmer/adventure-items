package transfarmer.soulboundarmory.network.server.weapon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.ICapability;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

public class C2SBindSlot implements IExtendedMessage {
    private String capability;
    private int slot;

    public C2SBindSlot() {
    }

    public C2SBindSlot(final ICapabilityType capability, final int slot) {
        this.capability = capability.toString();
        this.slot = slot;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.slot = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeInt(this.slot);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SBindSlot> {
        @Override
        public IExtendedMessage onMessage(final C2SBindSlot message, final MessageContext context) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final EntityPlayer player = context.getServerHandler().player;
                final ICapability capability = player.getCapability(ICapabilityType.get(message.capability).getCapability(), null);
                final NonNullList<ItemStack> inventory = player.inventory.mainInventory;

                if (capability.getBoundSlot() == message.slot) {
                    capability.unbindSlot();
                } else {
                    if (inventory.get(message.slot).isEmpty()) {
                        for (final ItemStack itemStack : inventory) {
                            if (capability.getItemType(itemStack) == capability.getItemType()) {
                                inventory.set(capability.getBoundSlot(), ItemStack.EMPTY);
                                player.inventory.setInventorySlotContents(message.slot, itemStack);
                            }
                        }
                    }

                    capability.bindSlot(message.slot);
                }

                capability.sync();
            });

            return null;
        }
    }
}
