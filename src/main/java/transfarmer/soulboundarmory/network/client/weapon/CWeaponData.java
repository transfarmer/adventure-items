package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponData implements IMessage {
    private EntityPlayer sender;
    private int weaponIndex;
    private int currentTab;
    private int cooldown;
    private int boundSlot;
    private int[][] data;
    private float[][] attributes;
    private int[][] enchantments;

    public CWeaponData() {
        this.data = new int[SoulWeaponType.getAmount()][SoulWeaponDatum.getAmount()];
        this.attributes = new float[SoulWeaponType.getAmount()][SoulWeaponAttribute.getAmount()];
        this.enchantments = new int[SoulWeaponType.getAmount()][SoulWeaponEnchantment.getAmount()];
    }

    public CWeaponData(final EntityPlayer sender, final IType type, final int currentTab, final int cooldown, final int boundSlot,
                       final int[][] data, final float[][] attributes, final int[][] enchantments) {
        final ISoulCapability capability = SoulToolProvider.get(sender);

        this.sender = sender;

        if (type == null) {
            this.weaponIndex = -1;
            this.currentTab = -1;
            this.boundSlot = -1;
            this.cooldown = 0;
        } else {
            this.weaponIndex = MathHelper.clamp(type.getIndex(), 0, capability.getItemAmount());
            this.currentTab = MathHelper.clamp(currentTab, 0, 3);
            this.boundSlot = boundSlot;
            this.cooldown = cooldown;
        }

        if (SoulItemHelper.areEmpty(SoulWeaponProvider.get(sender), data, attributes, enchantments)) {
            this.data = new int[capability.getItemAmount()][capability.getDatumAmount()];
            this.attributes = new float[capability.getItemAmount()][capability.getAttributeAmount()];
            this.enchantments = new int[capability.getItemAmount()][capability.getEnchantmentAmount()];
        } else {
            this.data = data;
            this.attributes = attributes;
            this.enchantments = enchantments;
        }
    }

    public void fromBytes(final ByteBuf buffer) {
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

    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.weaponIndex);
        buffer.writeInt(this.currentTab);
        buffer.writeInt(this.cooldown);
        buffer.writeInt(this.boundSlot);

        SoulItemHelper.forEach(SoulWeaponProvider.get(this.sender),
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
                instance.setAttackCooldown(message.cooldown);
                instance.bindSlot(message.boundSlot);
                instance.setStatistics(message.data, message.attributes, message.enchantments);
            });

            return null;
        }
    }
}
