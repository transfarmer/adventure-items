package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.data.IType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponData implements IMessage {
    private int weaponIndex;
    private int currentTab;
    private int cooldown;
    private int boundSlot;
    private int[][] data;
    private float[][] attributes;
    private int[][] enchantments;

    public CWeaponData() {}

    public CWeaponData(final IType type, final int currentTab, final int cooldown, final int boundSlot,
                       final int[][] data, final float[][] attributes, final int[][] enchantments) {
        if (type == null) {
            this.weaponIndex = -1;
        } else {
            this.weaponIndex = type.getIndex();
        }

        this.currentTab = currentTab;
        this.cooldown = cooldown;
        this.boundSlot = boundSlot;

        if (attributes[0].length == 0 || attributes[1].length == 0 || attributes[2].length == 0
            || data[0].length == 0 || data[1].length == 0 || data[2].length == 0
            || enchantments[0].length == 0 || enchantments[1].length == 0 || enchantments[2].length == 0) return;

        this.data = data;
        this.attributes = attributes;
        this.enchantments = enchantments;
    }

    public void fromBytes(ByteBuf buffer) {
        this.weaponIndex = buffer.readInt();
        this.currentTab = buffer.readInt();
        this.cooldown = buffer.readInt();
        this.boundSlot = buffer.readInt();

        SoulItemHelper.forEach(SoulWeaponProvider.get(Minecraft.getMinecraft().player),
            (Integer weaponIndex, Integer valueIndex) -> this.data[weaponIndex][valueIndex] = buffer.readInt(),
            (Integer weaponIndex, Integer valueIndex) -> this.attributes[weaponIndex][valueIndex] = buffer.readFloat(),
            (Integer weaponIndex, Integer valueIndex) -> this.enchantments[weaponIndex][valueIndex] = buffer.readInt()
        );
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.weaponIndex);
        buffer.writeInt(this.currentTab);
        buffer.writeInt(this.cooldown);
        buffer.writeInt(this.boundSlot);

        SoulItemHelper.forEach(SoulWeaponProvider.get(Minecraft.getMinecraft().player),
            (Integer weaponIndex, Integer valueIndex) -> buffer.writeInt(this.data[weaponIndex][valueIndex]),
            (Integer weaponIndex, Integer valueIndex) -> buffer.writeFloat(this.attributes[weaponIndex][valueIndex]),
            (Integer weaponIndex, Integer valueIndex) -> buffer.writeInt(this.enchantments[weaponIndex][valueIndex])
        );
    }

    public static final class Handler implements IMessageHandler<CWeaponData, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(CWeaponData message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulWeapon instance = SoulWeaponProvider.get(Minecraft.getMinecraft().player);

                instance.setCurrentType(message.weaponIndex);
                instance.setCurrentTab(message.currentTab);
                instance.setAttackCooldwn(message.cooldown);
                instance.bindSlot(message.boundSlot);
                instance.setStatistics(message.data, message.attributes, message.enchantments);
            });

            return null;
        }
    }
}
