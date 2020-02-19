package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.data.IEnchantment;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponSpendEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int weaponIndex;

    public CWeaponSpendEnchantmentPoints() {}

    public CWeaponSpendEnchantmentPoints(final int amount, final IEnchantment enchantment, final IType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.getIndex();
        this.weaponIndex = type.getIndex();
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
            final IEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex);
            final IType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = SoulWeaponProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(message.amount, enchantment, weaponType);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
