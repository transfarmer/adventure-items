package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.S2CToolDatum;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulToolDatum.TOOL_DATA;

public class C2SToolDatum implements IMessage {
    private int value;
    private int datumIndex;
    private int typeIndex;

    public C2SToolDatum() {}

    public C2SToolDatum(final int value, final SoulDatum datum, final SoulType type) {
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

    public static final class Handler implements IMessageHandler<C2SToolDatum, IMessage> {
        @Override
        public IMessage onMessage(C2SToolDatum message, MessageContext context) {
            final EntityPlayer player = Minecraft.getMinecraft().player;
            final ISoulCapability instance = SoulToolProvider.get(player);
            final SoulDatum datum = TOOL_DATA.get(message.datumIndex);
            final SoulType type = SoulToolType.get(message.typeIndex);

            instance.addDatum(message.value, datum, type);

            return new S2CToolDatum(message.value, datum, type);
        }
    }
}