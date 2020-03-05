package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponResetAttributes implements IMessage {
    private int index;

    public CWeaponResetAttributes() {}

    public CWeaponResetAttributes(final SoulType type) {
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
                final ISoulCapability capability = SoulItemHelper.getCapability(Minecraft.getMinecraft().player, (Item) null);
                final SoulType type = SoulWeaponType.get(message.index);

                capability.addDatum(capability.getDatum(SoulDatum.DATA.spentAttributePoints, type), SoulDatum.DATA.attributePoints, type);
                capability.setDatum(0, SoulDatum.DATA.spentAttributePoints, type);
                capability.setAttributes(new float[capability.getAttributeAmount()], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
