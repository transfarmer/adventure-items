package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponResetAttributes;

public class SWeaponResetAttributes implements IMessage {
    private int index;

    public SWeaponResetAttributes() {}

    public SWeaponResetAttributes(final IType type) {
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

    public static final class Handler implements IMessageHandler<SWeaponResetAttributes, IMessage> {
        @Override
        public IMessage onMessage(final SWeaponResetAttributes message, final MessageContext context) {
            final ISoulCapability capability = SoulItemHelper.getCapability(context.getServerHandler().player, (Item) null);
            final IType type = SoulWeaponType.getType(message.index);

            capability.addDatum(capability.getDatum(SoulDatum.SPENT_ATTRIBUTE_POINTS, type), SoulDatum.ATTRIBUTE_POINTS, type);
            capability.setDatum(0, SoulDatum.SPENT_ATTRIBUTE_POINTS, type);
            capability.setAttributes(new float[capability.getAttributeAmount()], type);

            return new CWeaponResetAttributes(type);
        }
    }
}
