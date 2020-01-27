package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.SoulWeaponType;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.SoulWeaponType.NONE;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponData implements IMessage {
    private SoulWeaponType weaponType = NONE;
    private int[][] attributes = new int[3][8];

    public ClientWeaponData() {}

    public ClientWeaponData(final SoulWeaponType weaponType, final int[][] attributes) {
        this.weaponType = weaponType;

        if (attributes[0].length == 0 || attributes[1].length == 0 || attributes[2].length == 0) return;

        this.attributes = attributes;
    }

    public void fromBytes(ByteBuf buffer) {
        this.weaponType = SoulWeaponType.getType(buffer.readInt());

        for (int weaponTypeIndex = 0; weaponTypeIndex <= 2; weaponTypeIndex++) {
            for (int valueIndex = 0; valueIndex <= 7; valueIndex++) {
                this.attributes[weaponTypeIndex][valueIndex] = buffer.readInt();
            }
        }
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.weaponType.getIndex());

        for (int weaponTypeIndex = 0; weaponTypeIndex <= 2; weaponTypeIndex++) {
            for (int valueIndex = 0; valueIndex <= 7; valueIndex++) {
                buffer.writeInt(this.attributes[weaponTypeIndex][valueIndex]);
            }
        }
    }

    public static final class Handler implements IMessageHandler<ClientWeaponData, IMessage> {
        @SideOnly(CLIENT)
        public IMessage onMessage(ClientWeaponData message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                ISoulWeapon instance = player.getCapability(CAPABILITY, null);

                instance.setCurrentType(message.weaponType);
                instance.setAttributes(message.attributes);
            });
            return null;
        }
    }
}
