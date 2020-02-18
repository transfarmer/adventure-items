package transfarmer.soulboundarmory.network.weapon.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.ATTRIBUTES;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.SPENT_ATTRIBUTE_POINTS;

public class CWeaponResetAttributes implements IMessage {
    private int index;

    public CWeaponResetAttributes() {}

    public CWeaponResetAttributes(final SoulWeaponType type) {
        this.index = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<CWeaponResetAttributes, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponResetAttributes message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulWeapon capability = Minecraft.getMinecraft().player.getCapability(CAPABILITY, null);
                final SoulWeaponType type = SoulWeaponType.getType(message.index);

                capability.addDatum(capability.getDatum(SPENT_ATTRIBUTE_POINTS, type), ATTRIBUTE_POINTS, type);
                capability.setDatum(0, SPENT_ATTRIBUTE_POINTS, type);
                capability.setAttributes(new float[ATTRIBUTES], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
