package transfarmer.soulboundarmory.network.server.weapon;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponSpendEnchantmentPoints;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public class C2SWeaponEnchantmentPoints implements IExtendedMessage {
    private int amount;
    private String enchantment;
    private String item;

    public C2SWeaponEnchantmentPoints() {
    }

    public C2SWeaponEnchantmentPoints(final IItem item, final Enchantment enchantment, final int amount) {
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

    public static final class Handler implements IExtendedMessageHandler<C2SWeaponEnchantmentPoints> {
        @Override
        public IExtendedMessage onMessage(final C2SWeaponEnchantmentPoints message, final MessageContext context) {
            final IWeapon instance = WeaponProvider.get(context.getServerHandler().player);
            final Enchantment enchantment = Enchantment.getEnchantmentByLocation(message.enchantment);
            final IItem weaponType = instance.getItemType(message.item);

            instance.addEnchantment(weaponType, enchantment, message.amount);

            return new S2CWeaponSpendEnchantmentPoints(message.amount, enchantment, weaponType);
        }
    }
}
