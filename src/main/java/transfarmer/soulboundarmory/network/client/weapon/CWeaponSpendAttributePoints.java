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
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponSpendAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public CWeaponSpendAttributePoints() {}

    public CWeaponSpendAttributePoints(final int amount, final SoulAttribute attribute, final SoulType type) {
        this.amount = amount;
        this.attributeIndex = attribute.getIndex();
        this.weaponIndex = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.attributeIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.attributeIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<CWeaponSpendAttributePoints, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponSpendAttributePoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final SoulType type = SoulWeaponType.get(message.weaponIndex);
            final SoulAttribute attribute = SoulWeaponAttribute.get(message.attributeIndex);
            final ISoulWeapon instance = SoulWeaponProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(message.amount, attribute, type);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
