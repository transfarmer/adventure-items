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
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponResetEnchantments implements IMessage {
    private int index;

    public CWeaponResetEnchantments() {}

    public CWeaponResetEnchantments(final SoulType type) {
        this.index = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<CWeaponResetEnchantments, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponResetEnchantments message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulWeapon capability = SoulWeaponProvider.get(Minecraft.getMinecraft().player);
                final SoulType type = SoulWeaponType.get(message.index);

                capability.addDatum(capability.getDatum(SoulDatum.DATA.spentEnchantmentPoints, type), SoulDatum.DATA.enchantmentPoints, type);
                capability.setDatum(0, SoulDatum.DATA.spentEnchantmentPoints, type);
                capability.setEnchantments(new int[capability.getEnchantmentAmount()], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
