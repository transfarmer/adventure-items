package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ServerWeaponType implements IMessage {
    private int currentWeaponIndex;

    public ServerWeaponType() {
        this.currentWeaponIndex = -1;
    }

    public ServerWeaponType(final int CURRENT_TYPE_INDEX) {
        this.currentWeaponIndex = CURRENT_TYPE_INDEX;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.currentWeaponIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(currentWeaponIndex);
    }

    public static class Handler implements IMessageHandler<ServerWeaponType, IMessage> {
        public IMessage onMessage(ServerWeaponType message, MessageContext context) {
            final int CURRENT_TYPE_INDEX = message.currentWeaponIndex;
            EntityPlayerMP player = context.getServerHandler().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);

            instance.setCurrentTypeIndex(CURRENT_TYPE_INDEX);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(instance.getItem()));
            Main.CHANNEL.sendTo(new ClientWeaponType(CURRENT_TYPE_INDEX), player);

            return null;
        }
    }
}