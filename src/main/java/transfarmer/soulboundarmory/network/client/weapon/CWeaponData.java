package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponDatum;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponData implements IMessage {
    private int weaponIndex;
    private int currentTab;
    private int cooldown;
    private int boundSlot;
    private int[][] data;
    private float[][] attributes;
    private int[][] enchantments;
    private int[] lengths = new int[4];

    public CWeaponData() {
        this.data = new int[SoulWeaponType.getAmount()][SoulWeaponDatum.getAmount()];
        this.attributes = new float[SoulWeaponType.getAmount()][SoulWeaponAttribute.getAmount()];
        this.enchantments = new int[SoulWeaponType.getAmount()][SoulWeaponEnchantment.getAmount()];
    }

    public CWeaponData(final IType type, final int currentTab, final int cooldown, final int boundSlot,
                       final int[][] data, final float[][] attributes, final int[][] enchantments) {
        if (type == null) {
            this.weaponIndex = -1;
            this.currentTab = 1;
            this.boundSlot = -1;
            this.cooldown = 0;
        } else {
            this.weaponIndex = MathHelper.clamp(type.getIndex(), 0, SoulWeaponType.getAmount() - 1);
            this.currentTab = MathHelper.clamp(currentTab, 0, 3);
            this.boundSlot = boundSlot;
            this.cooldown = cooldown;
        }

        this.data = data;
        this.attributes = attributes;
        this.enchantments = enchantments;

        this.lengths = new int[]{data.length, data[0].length, attributes[0].length, enchantments[0].length};
    }

    @SideOnly(CLIENT)
    public void fromBytes(final ByteBuf buffer) {
        this.weaponIndex = buffer.readInt();
        this.currentTab = buffer.readInt();
        this.cooldown = buffer.readInt();
        this.boundSlot = buffer.readInt();

        for (int index = 0; index < this.lengths.length; index++) {
            this.lengths[index] = buffer.readInt();
        }

        SoulItemHelper.forEach(this.lengths[0], this.lengths[1], this.lengths[2], this.lengths[3],
                (final Integer itemIndex, final Integer valueIndex) -> this.data[itemIndex][valueIndex] = buffer.readInt(),
                (final Integer itemIndex, final Integer valueIndex) -> this.attributes[itemIndex][valueIndex] = buffer.readFloat(),
                (final Integer itemIndex, final Integer valueIndex) -> this.enchantments[itemIndex][valueIndex] = buffer.readInt()
        );
    }

    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.weaponIndex);
        buffer.writeInt(this.currentTab);
        buffer.writeInt(this.cooldown);
        buffer.writeInt(this.boundSlot);

        for (final int length : this.lengths) {
            buffer.writeInt(length);
        }

        SoulItemHelper.forEach(this.lengths[0], this.lengths[1], this.lengths[2], this.lengths[3],
                (final Integer weaponIndex, final Integer valueIndex) -> buffer.writeInt(this.data[weaponIndex][valueIndex]),
                (final Integer weaponIndex, final Integer valueIndex) -> buffer.writeFloat(this.attributes[weaponIndex][valueIndex]),
                (final Integer weaponIndex, final Integer valueIndex) -> buffer.writeInt(this.enchantments[weaponIndex][valueIndex])
        );
    }

    public static final class Handler implements IMessageHandler<CWeaponData, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponData message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulWeapon instance = SoulWeaponProvider.get(Minecraft.getMinecraft().player);

                instance.setCurrentType(message.weaponIndex);
                instance.setCurrentTab(message.currentTab);
                instance.setAttackCooldown(message.cooldown);
                instance.bindSlot(message.boundSlot);
                instance.setStatistics(message.data, message.attributes, message.enchantments);
            });

            return null;
        }
    }
}
