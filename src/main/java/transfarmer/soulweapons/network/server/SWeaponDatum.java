package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponDatum;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.network.client.CWeaponDatum;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class SWeaponDatum implements IMessage {
    private int value;
    private int datumIndex;
    private int typeIndex;

    public SWeaponDatum() {}

    public SWeaponDatum(final int value, final SoulWeaponDatum datum, final SoulWeaponType type) {
        this.value = value;
        this.datumIndex = datum.index;
        this.typeIndex = type.index;
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
            final ISoulWeapon instance = player.getCapability(CAPABILITY, null);
            final SoulWeaponDatum datum = SoulWeaponDatum.getDatum(message.datumIndex);
            final SoulWeaponType type = SoulWeaponType.getType(message.typeIndex);

            instance.addDatum(message.value, datum, type);

            return new CWeaponDatum(message.value, datum, type);
        }
    }
}
