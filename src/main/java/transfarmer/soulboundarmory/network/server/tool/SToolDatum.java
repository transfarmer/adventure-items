package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.CToolDatum;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

public class SToolDatum implements IMessage {
    private int value;
    private int datumIndex;
    private int typeIndex;

    public SToolDatum() {}

    public SToolDatum(final int value, final SoulDatum datum, final IType type) {
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

    public static final class Handler implements IMessageHandler<SToolDatum, IMessage> {
        @Override
        public IMessage onMessage(SToolDatum message, MessageContext context) {
            final EntityPlayer player = Minecraft.getMinecraft().player;
            final ISoulTool instance = SoulToolProvider.get(player);
            final SoulDatum datum = SoulToolDatum.getDatum(message.datumIndex);
            final IType type = SoulToolType.getType(message.typeIndex);

            instance.addDatum(message.value, datum, type);

            return new CToolDatum(message.value, datum, type);
        }
    }
}
