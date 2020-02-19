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
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.IType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolData implements IMessage {
    private int toolIndex;
    private int currentTab;
    private int boundSlot;
    private int[][] data;
    private float[][] attributes;
    private int[][] enchantments;

    public CToolData() {}

    public CToolData(final IType type, final int currentTab, final int boundSlot,
                     final int[][] data, final float[][] attributes, final int[][] enchantments) {
        final ISoulCapability capability = SoulToolProvider.get(Minecraft.getMinecraft().player);

        if (type == null || capability == null) {
            this.toolIndex = -1;
            this.currentTab = -1;
            this.boundSlot = -1;
        } else {
            this.toolIndex = MathHelper.clamp(type.getIndex(), 0, capability.getItemAmount());
            this.currentTab = MathHelper.clamp(currentTab, 0, 2);
            this.boundSlot = boundSlot;
        }

        if (SoulToolHelper.areEmpty(data, attributes, enchantments)) {
            this.data = new int[capability.getItemAmount()][capability.getDatumAmount()];
            this.attributes = new float[capability.getItemAmount()][capability.getAttributeAmount()];
            this.enchantments = new int[capability.getItemAmount()][capability.getEnchantmentAmount()];
        } else {
            this.data = data;
            this.attributes = attributes;
        }

        this.enchantments = enchantments;
    }

    public void fromBytes(ByteBuf buffer) {
        this.toolIndex = buffer.readInt();
        this.currentTab = buffer.readInt();
        this.boundSlot = buffer.readInt();

        SoulItemHelper.forEach(SoulToolProvider.get(Minecraft.getMinecraft().player),
                (final Integer toolIndex, final Integer valueIndex) -> this.data[toolIndex][valueIndex] = buffer.readInt(),
                (final Integer toolIndex, final Integer valueIndex) -> this.attributes[toolIndex][valueIndex] = buffer.readFloat(),
                (final Integer toolIndex, final Integer valueIndex) -> this.enchantments[toolIndex][valueIndex] = buffer.readInt()
        );
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.toolIndex);
        buffer.writeInt(this.currentTab);
        buffer.writeInt(this.boundSlot);

        SoulItemHelper.forEach(SoulToolProvider.get(Minecraft.getMinecraft().player),
                (final Integer toolIndex, final Integer valueIndex) -> buffer.writeInt(this.data[toolIndex][valueIndex]),
                (final Integer toolIndex, final Integer valueIndex) -> buffer.writeFloat(this.attributes[toolIndex][valueIndex]),
                (final Integer toolIndex, final Integer valueIndex) -> buffer.writeInt(this.enchantments[toolIndex][valueIndex])
        );
    }

    public static final class Handler implements IMessageHandler<CToolData, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolData message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulTool capability = SoulToolProvider.get(Minecraft.getMinecraft().player);

                capability.setCurrentType(message.toolIndex);
                capability.setCurrentTab(message.currentTab);
                capability.bindSlot(message.boundSlot);
                capability.setStatistics(message.data, message.attributes, message.enchantments);
            });

            return null;
        }
    }
}
