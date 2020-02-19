package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.data.IDatum;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponDatum implements IMessage {
    private int value;
    private int datumIndex;
    private int typeIndex;

    public CWeaponDatum() {}

    public CWeaponDatum(final int value, final IDatum datum, final IType type) {
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

    public static final class Handler implements IMessageHandler<CWeaponDatum, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(CWeaponDatum message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer player = Minecraft.getMinecraft().player;
                final ISoulWeapon instance = SoulWeaponProvider.get(player);

                instance.addDatum(message.value, SoulWeaponDatum.getDatum(message.datumIndex), SoulWeaponType.getType(message.typeIndex));
            });

            return null;
        }
    }
}
