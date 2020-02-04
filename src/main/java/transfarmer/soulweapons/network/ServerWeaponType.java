package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ServerWeaponType implements IMessage {
    private int currentWeaponIndex;

    public ServerWeaponType() {
        this.currentWeaponIndex = -1;
    }

    public ServerWeaponType(final SoulWeaponType weaponType) {
        this.currentWeaponIndex = weaponType.index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.currentWeaponIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(currentWeaponIndex);
    }

    public static final class Handler implements IMessageHandler<ServerWeaponType, IMessage> {
        public IMessage onMessage(ServerWeaponType message, MessageContext context) {
            final SoulWeaponType WEAPON_TYPE = SoulWeaponType.getType(message.currentWeaponIndex);
            EntityPlayerMP player = context.getServerHandler().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);

            instance.setCurrentType(WEAPON_TYPE);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(instance.getCurrentType().getItem()));

            return new ClientWeaponType(WEAPON_TYPE);
        }
    }
}
