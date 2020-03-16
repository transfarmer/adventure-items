package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponDatum;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulWeaponDatum.WEAPON_DATA;

public class SWeaponDatum implements IMessage {
    private int value;
    private int datumIndex;
    private int typeIndex;

    public SWeaponDatum() {}

    public SWeaponDatum(final int value, final SoulDatum datum, final SoulType type) {
        this.value = value;
        this.datumIndex = datum.getIndex();
        this.typeIndex = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.value = buffer.readInt();
        this.datumIndex = buffer.readInt();
        this.typeIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.value);
        buffer.writeInt(this.datumIndex);
        buffer.writeInt(this.typeIndex);
    }

    public static final class Handler implements IMessageHandler<SWeaponDatum, IMessage> {
        @Override
        public IMessage onMessage(SWeaponDatum message, MessageContext context) {
            final EntityPlayer player = Minecraft.getMinecraft().player;
            final ISoulWeapon instance = SoulWeaponProvider.get(player);
            final SoulDatum datum = WEAPON_DATA.get(message.datumIndex);
            final SoulType type = SoulWeaponType.get(message.typeIndex);

            instance.addDatum(message.value, datum, type);

            return new CWeaponDatum(message.value, datum, type);
        }
    }
}
