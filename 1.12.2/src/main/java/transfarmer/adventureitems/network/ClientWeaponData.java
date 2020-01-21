package transfarmer.adventureitems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.adventureitems.capability.ISoulWeapon;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponData implements IMessage {
    private final int[][] attributes;
    private int currentTypeIndex;

    public ClientWeaponData(int[][] attributes) {
        if (attributes[0].length == 0 || attributes[1].length == 0 || attributes[2].length == 0) {
            this.attributes = new int[3][8];
            return;
        }

        this.attributes = attributes;
    }

    public void fromBytes(ByteBuf buffer) {
        this.currentTypeIndex = buffer.readInt();

        for (int weaponTypeIndex = 0; weaponTypeIndex <= 2; weaponTypeIndex++) {
            for (int valueIndex = 0; valueIndex <= 7; valueIndex++) {
                this.attributes[weaponTypeIndex][valueIndex] = buffer.readInt();
            }
        }
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.currentTypeIndex);

        for (int[] type : this.attributes) {
            for (int value : type) {
                buffer.writeInt(value);
            }
        }
    }

    public static class Handler implements IMessageHandler<ClientWeaponData, IMessage> {
        @SideOnly(CLIENT)
        public IMessage onMessage(ClientWeaponData message, MessageContext context) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);
            instance.setCurrentTypeIndex(message.currentTypeIndex);
            instance.setAttributes(message.attributes);

            return null;
        }
    }
}
