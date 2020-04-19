package transfarmer.soulboundarmory.network.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CWeaponSpendEnchantmentPoints implements IExtendedMessage {
    private int amount;
    private String enchantment;
    private String item;

    public S2CWeaponSpendEnchantmentPoints() {
    }

    public S2CWeaponSpendEnchantmentPoints(final int amount, final Enchantment enchantment, final IItem item) {
        this.amount = amount;
        this.enchantment = enchantment.getName();
        this.item = item.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.amount = buffer.readInt();
        this.enchantment = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.amount);
        buffer.writeString(this.enchantment);
        buffer.writeString(this.item);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CWeaponSpendEnchantmentPoints> {
        @SideOnly(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CWeaponSpendEnchantmentPoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final Enchantment enchantment = Enchantment.getEnchantmentByLocation(message.enchantment);
            final IItem weaponType = IItem.get(message.item);
            final IWeapon instance = WeaponProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(weaponType, enchantment, message.amount);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
