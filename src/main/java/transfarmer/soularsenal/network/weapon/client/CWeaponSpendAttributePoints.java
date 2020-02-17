package transfarmer.soularsenal.network.weapon.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soularsenal.capability.weapon.ISoulWeapon;
import transfarmer.soularsenal.data.weapon.SoulWeaponAttribute;
import transfarmer.soularsenal.data.weapon.SoulWeaponType;
import transfarmer.soularsenal.client.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soularsenal.capability.weapon.SoulWeaponProvider.CAPABILITY;

public class CWeaponSpendAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public CWeaponSpendAttributePoints() {}

    public CWeaponSpendAttributePoints(final int amount, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        this.amount = amount;
        this.attributeIndex = attribute.index;
        this.weaponIndex = type.index;
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
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(message.amount, attribute, weaponType);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}