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
import transfarmer.soulweapons.data.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponData implements IMessage {
    private SoulWeaponType weaponType;
    private int currentTab;
    private int[][] data = new int[3][5];
    private float[][] attributes = new float[3][5];
    private int[][] enchantments = new int[3][7];

    public ClientWeaponData() {}

    public ClientWeaponData(final SoulWeaponType weaponType, final int currentTab, final int[][] data, final float[][] attributes, final int[][] enchantments) {
        this.weaponType = weaponType;
        this.currentTab = currentTab;

        if (attributes[0].length == 0 || attributes[1].length == 0 || attributes[2].length == 0
            || data[0].length == 0 || data[1].length == 0 || data[2].length == 0
            || enchantments[0].length == 0 || enchantments[1].length == 0 || enchantments[2].length == 0) return;

        this.data = data;
        this.attributes = attributes;
        this.enchantments = enchantments;
    }

    public void fromBytes(ByteBuf buffer) {
        this.weaponType = SoulWeaponType.getType(buffer.readInt());
        this.currentTab = buffer.readInt();

        SoulWeaponHelper.forEach(
            (Integer weaponIndex, Integer valueIndex) -> this.data[weaponIndex][valueIndex] = buffer.readInt(),
            (Integer weaponIndex, Integer valueIndex) -> this.attributes[weaponIndex][valueIndex] = buffer.readFloat(),
            (Integer weaponIndex, Integer valueIndex) -> this.enchantments[weaponIndex][valueIndex] = buffer.readInt()
        );
    }

    public void toBytes(ByteBuf buffer) {
        if (this.weaponType != null) {
            buffer.writeInt(this.weaponType.index);
        } else {
            buffer.writeInt(-1);
        }

        buffer.writeInt(this.currentTab);

        SoulWeaponHelper.forEach(
            (Integer weaponIndex, Integer valueIndex) -> buffer.writeInt(this.data[weaponIndex][valueIndex]),
            (Integer weaponIndex, Integer valueIndex) -> buffer.writeFloat(this.attributes[weaponIndex][valueIndex]),
            (Integer weaponIndex, Integer valueIndex) -> buffer.writeInt(this.enchantments[weaponIndex][valueIndex])
        );
    }

    public static final class Handler implements IMessageHandler<ClientWeaponData, IMessage> {
        @SideOnly(CLIENT)
        public IMessage onMessage(ClientWeaponData message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                ISoulWeapon instance = player.getCapability(CAPABILITY, null);

                instance.setCurrentType(message.weaponType);
                instance.setCurrentTab(message.currentTab);
                instance.set(message.data, message.attributes, message.enchantments);
            });

            return null;
        }
    }
}
