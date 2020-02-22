package transfarmer.soulboundarmory.network.client.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolDatum implements IMessage {
    private int value;
    private int datumIndex;
    private int typeIndex;

    public CToolDatum() {}

    public CToolDatum(final int value, final SoulDatum datum, final SoulType type) {
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

    public static final class Handler implements IMessageHandler<CToolDatum, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(CToolDatum message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer player = Minecraft.getMinecraft().player;
                final ISoulCapability instance = SoulToolProvider.get(player);

                instance.addDatum(message.value, SoulToolDatum.get(message.datumIndex), SoulToolType.get(message.typeIndex));
            });

            return null;
        }
    }
}
