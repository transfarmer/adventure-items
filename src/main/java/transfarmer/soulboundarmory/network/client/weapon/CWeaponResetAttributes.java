package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponResetAttributes implements IMessage {
    private int index;

    public CWeaponResetAttributes() {}

    public CWeaponResetAttributes(final IType type) {
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

    public static final class Handler implements IMessageHandler<CWeaponResetAttributes, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponResetAttributes message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulCapability capability = SoulItemHelper.getCapability(Minecraft.getMinecraft().player, null);
                final IType type = SoulWeaponType.getType(message.index);

                capability.addDatum(capability.getDatum(capability.getEnumSpentAttributePoints(), type), capability.getEnumAttributePoints(), type);
                capability.setDatum(0, capability.getEnumSpentAttributePoints(), type);
                capability.setAttributes(new float[capability.getAttributeAmount()], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
