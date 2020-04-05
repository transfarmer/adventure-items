package transfarmer.soulboundarmory.network.client.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulToolDatum.TOOL_DATA;

public class CToolData implements IMessage {
    private int toolIndex;
    private int currentTab;
    private int boundSlot;
    private int[][] data;
    private float[][] attributes;
    private int[][] enchantments;
    private int[] lengths = new int[4];

    public CToolData() {
        this.data = new int[SoulToolType.getAmount()][TOOL_DATA.getAmount()];
        this.attributes = new float[SoulToolType.getAmount()][SoulToolAttribute.getAmount()];
        this.enchantments = new int[SoulToolType.getAmount()][SoulToolEnchantment.getAmount()];
    }

    public CToolData(final SoulType type, final int currentTab, final int boundSlot, final int[][] data, final float[][] attributes, final int[][] enchantments) {
        if (type == null) {
            this.toolIndex = -1;
            this.currentTab = 0;
            this.boundSlot = -1;
        } else {
            this.toolIndex = MathHelper.clamp(type.getIndex(), 0, SoulToolType.getAmount() - 1);
            this.currentTab = MathHelper.clamp(currentTab, 0, 2);
            this.boundSlot = boundSlot;
        }

        this.data = data;
        this.attributes = attributes;
        this.enchantments = enchantments;

        this.lengths = new int[]{data.length, data[0].length, attributes[0].length, enchantments[0].length};
    }

    @SideOnly(CLIENT)
    public void fromBytes(ByteBuf buffer) {
        this.toolIndex = buffer.readInt();
        this.currentTab = buffer.readInt();
        this.boundSlot = buffer.readInt();

        for (int index = 0; index < this.lengths.length; index++) {
            this.lengths[index] = buffer.readInt();
        }

        SoulItemHelper.forEach(this.lengths[0], this.lengths[1], this.lengths[2], this.lengths[3],
                (final Integer itemIndex, final Integer valueIndex) -> CToolData.this.data[itemIndex][valueIndex] = buffer.readInt(),
                (final Integer itemIndex, final Integer valueIndex) -> CToolData.this.attributes[itemIndex][valueIndex] = buffer.readFloat(),
                (final Integer itemIndex, final Integer valueIndex) -> CToolData.this.enchantments[itemIndex][valueIndex] = buffer.readInt()
        );
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.toolIndex);
        buffer.writeInt(this.currentTab);
        buffer.writeInt(this.boundSlot);

        for (final int length : this.lengths) {
            buffer.writeInt(length);
        }

        SoulItemHelper.forEach(this.lengths[0], this.lengths[1], this.lengths[2], this.lengths[3],
                (final Integer itemIndex, final Integer valueIndex) -> buffer.writeInt(this.data[itemIndex][valueIndex]),
                (final Integer itemIndex, final Integer valueIndex) -> buffer.writeFloat(this.attributes[itemIndex][valueIndex]),
                (final Integer itemIndex, final Integer valueIndex) -> buffer.writeInt(this.enchantments[itemIndex][valueIndex])
        );
    }

    public static final class Handler implements IMessageHandler<CToolData, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolData message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulCapability capability = SoulToolProvider.get(Minecraft.getMinecraft().player);

                capability.setCurrentType(message.toolIndex);
                capability.setCurrentTab(message.currentTab);
                capability.bindSlot(message.boundSlot);
                capability.setStatistics(message.data, message.attributes, message.enchantments);
            });

            return null;
        }
    }
}
