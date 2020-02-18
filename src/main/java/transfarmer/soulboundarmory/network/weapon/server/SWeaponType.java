package transfarmer.soulboundarmory.network.weapon.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.network.weapon.client.CWeaponType;

import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider.CAPABILITY;

public class SWeaponType implements IMessage {
    private int index;

    public SWeaponType() {
        this.index = -1;
    }

    public SWeaponType(final SoulWeaponType weaponType) {
        this.index = weaponType.index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(index);
    }

    public static final class Handler implements IMessageHandler<SWeaponType, IMessage> {
        @Override
        public IMessage onMessage(SWeaponType message, MessageContext context) {
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.index);
            final EntityPlayerMP player = context.getServerHandler().player;
            final ISoulWeapon instance = player.getCapability(CAPABILITY, null);
            int slot = instance.getBoundSlot();
            instance.setCurrentType(weaponType);

            if (!SoulWeaponHelper.hasSoulWeapon(player)) {
                player.inventory.clearMatchingItems(Items.WOODEN_SWORD, -1, 37, null);
            } else {
                SoulWeaponHelper.removeSoulWeapons(player);
            }

            if (slot >= 0) {
                final ItemStack boundSlotItem = player.inventory.getStackInSlot(slot);

                if (boundSlotItem.getItem() != Items.AIR && !SoulWeaponHelper.isSoulWeapon(boundSlotItem)) {
                    slot = -1;
                }
            }

            if (slot < 0) {
                slot = player.inventory.getFirstEmptyStack();
            }

            player.inventory.setInventorySlotContents(slot, new ItemStack(instance.getCurrentType().item));

            return new CWeaponType(slot, weaponType);
        }
    }
}
