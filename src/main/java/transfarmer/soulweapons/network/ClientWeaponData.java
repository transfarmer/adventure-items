package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponHelper;
import transfarmer.soulweapons.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.weapon.SoulWeaponType.NONE;

public class ClientWeaponData implements IMessage {
    private SoulWeaponType weaponType = NONE;
    private int[][] data = new int[3][4];
    private float[][] attributes = new float[3][5];

    public ClientWeaponData() {}

    public ClientWeaponData(final SoulWeaponType weaponType, final int[][] data, final float[][] attributes) {
        this.weaponType = weaponType;

        if (attributes[0].length == 0 || attributes[1].length == 0 || attributes[2].length == 0
            || data[0].length == 0 || data[1].length == 0 || data[2].length == 0) return;

        this.data = data;
        this.attributes = attributes;
    }

    public void fromBytes(ByteBuf buffer) {
        this.weaponType = SoulWeaponType.getType(buffer.readInt());

        SoulWeaponHelper.forEachDatumAndAttribute((Integer weaponIndex, Integer valueIndex) ->
                this.data[weaponIndex][valueIndex] = buffer.readInt(),
            (Integer weaponIndex, Integer valueIndex) ->
                this.attributes[weaponIndex][valueIndex] = buffer.readFloat());
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.weaponType.index);

        SoulWeaponHelper.forEachDatumAndAttribute((Integer weaponIndex, Integer valueIndex) ->
                buffer.writeInt(this.data[weaponIndex][valueIndex]),
            (Integer weaponIndex, Integer valueIndex) ->
                buffer.writeFloat(this.attributes[weaponIndex][valueIndex]));
    }

    public static final class Handler implements IMessageHandler<ClientWeaponData, IMessage> {
        @SideOnly(CLIENT)
        public IMessage onMessage(ClientWeaponData message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                ISoulWeapon instance = player.getCapability(CAPABILITY, null);

                instance.setCurrentType(message.weaponType);
                instance.setData(message.data);
                instance.setAttributes(message.attributes);
            });

            return null;
        }
    }
}
