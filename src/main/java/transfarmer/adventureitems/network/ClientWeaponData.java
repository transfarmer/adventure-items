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
    private int currentTypeIndex;
    private final int[][] ATTRIBUTES;

    public ClientWeaponData() {
        this.ATTRIBUTES = new int[3][8];
        this.currentTypeIndex = -1;
    }

    public ClientWeaponData(final int CURRENT_TYPE_INDEX, final int[][] ATTRIBUTES) {
        this.currentTypeIndex = CURRENT_TYPE_INDEX;

        if (ATTRIBUTES[0].length == 0 || ATTRIBUTES[1].length == 0 || ATTRIBUTES[2].length == 0) {
            this.ATTRIBUTES = new int[3][8];
            return;
        }

        this.ATTRIBUTES = ATTRIBUTES;
    }

    public void fromBytes(ByteBuf buffer) {
        this.currentTypeIndex = buffer.readInt();

        for (int weaponTypeIndex = 0; weaponTypeIndex <= 2; weaponTypeIndex++) {
            for (int valueIndex = 0; valueIndex <= 7; valueIndex++) {
                this.ATTRIBUTES[weaponTypeIndex][valueIndex] = buffer.readInt();
            }
        }
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.currentTypeIndex);

        for (int weaponTypeIndex = 0; weaponTypeIndex <= 2; weaponTypeIndex++) {
            for (int valueIndex = 0; valueIndex <= 7; valueIndex++) {
                buffer.writeInt(this.ATTRIBUTES[weaponTypeIndex][valueIndex]);
            }
        }
    }

    public static class Handler implements IMessageHandler<ClientWeaponData, IMessage> {
        @SideOnly(CLIENT)
        public IMessage onMessage(ClientWeaponData message, MessageContext context) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);
            instance.setCurrentTypeIndex(message.currentTypeIndex);
            instance.setAttributes(message.ATTRIBUTES);

            return null;
        }
    }
}
