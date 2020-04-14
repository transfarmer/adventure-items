package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponResetAttributes;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

public class C2SWeaponResetAttributes implements IMessage {
    private int index;

    public C2SWeaponResetAttributes() {}

    public C2SWeaponResetAttributes(final SoulType type) {
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

    public static final class Handler implements IMessageHandler<C2SWeaponResetAttributes, IMessage> {
        @Override
        public IMessage onMessage(final C2SWeaponResetAttributes message, final MessageContext context) {
            final ISoulCapability capability = SoulItemHelper.getCapability(context.getServerHandler().player, (Item) null);
            final SoulType type = SoulWeaponType.get(message.index);

            capability.addDatum(capability.getDatum(SoulDatum.DATA.spentAttributePoints, type), SoulDatum.DATA.attributePoints, type);
            capability.setDatum(0, SoulDatum.DATA.spentAttributePoints, type);
            capability.setAttributes(new float[capability.getAttributeAmount()], type);

            return new S2CWeaponResetAttributes(type);
        }
    }
}
