package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponHelper;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.network.client.CWeaponType;
import transfarmer.util.ItemHelper;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

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
                for (final ItemStack woodenSword : ItemHelper.getWoodenSwords(player)) {
                    player.inventory.deleteStack(woodenSword);
                }
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

            player.inventory.setInventorySlotContents(slot, new ItemStack(instance.getCurrentType().getItem()));

            return new CWeaponType(slot, weaponType);
        }
    }
}
