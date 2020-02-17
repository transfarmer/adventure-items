package transfarmer.soularsenal.network.weapon.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soularsenal.capability.weapon.ISoulWeapon;
import transfarmer.soularsenal.data.weapon.SoulWeaponEnchantment;
import transfarmer.soularsenal.data.weapon.SoulWeaponType;
import transfarmer.soularsenal.client.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soularsenal.capability.weapon.SoulWeaponProvider.CAPABILITY;

public class CWeaponSpendEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int weaponIndex;

    public CWeaponSpendEnchantmentPoints() {}

    public CWeaponSpendEnchantmentPoints(final int amount, final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.index;
        this.weaponIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.enchantmentIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.enchantmentIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<CWeaponSpendEnchantmentPoints, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponSpendEnchantmentPoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final SoulWeaponEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(message.amount, enchantment, weaponType);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
