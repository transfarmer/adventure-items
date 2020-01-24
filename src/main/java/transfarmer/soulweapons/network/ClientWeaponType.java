package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.WeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponType implements IMessage {
    private int currentTypeIndex;

    public ClientWeaponType() {
        this.currentTypeIndex = 3;
    }

    public ClientWeaponType(final WeaponType WEAPON_TYPE) {
        this.currentTypeIndex = WEAPON_TYPE.getIndex();
    }

    public void fromBytes(ByteBuf buffer) {
        currentTypeIndex = buffer.readInt();
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(currentTypeIndex);
    }

    public static class Handler implements IMessageHandler<ClientWeaponType, IMessage> {
        @SideOnly(CLIENT)
        public IMessage onMessage(ClientWeaponType message, MessageContext context) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            player.getCapability(CAPABILITY, null).setCurrentType(message.currentTypeIndex);
            player.inventory.setInventorySlotContents(player.inventory.currentItem,
                    new ItemStack(player.getCapability(CAPABILITY, null).getItem()));

            return null;
        }
    }
}
