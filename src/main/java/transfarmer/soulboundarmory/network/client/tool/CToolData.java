package transfarmer.soulboundarmory.network.client.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import java.util.UUID;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolData implements IMessage {
    private String senderUUID;
    private int toolIndex;
    private int currentTab;
    private int boundSlot;
    private int[][] data;
    private float[][] attributes;
    private int[][] enchantments;

    public CToolData() {
        this.data = new int[SoulToolType.getAmount()][SoulToolDatum.getAmount()];
        this.attributes = new float[SoulToolType.getAmount()][SoulToolAttribute.getAmount()];
        this.enchantments = new int[SoulToolType.getAmount()][SoulToolEnchantment.getAmount()];
    }

    public CToolData(final EntityPlayer sender, final IType type, final int currentTab, final int boundSlot,
                     final int[][] data, final float[][] attributes, final int[][] enchantments) {
        final ISoulCapability capability = SoulToolProvider.get(sender);

        this.senderUUID = sender.getUniqueID().toString();

        if (type == null) {
            this.toolIndex = -1;
            this.currentTab = 0;
            this.boundSlot = -1;
        } else {
            this.toolIndex = MathHelper.clamp(type.getIndex(), 0, capability.getItemAmount());
            this.currentTab = MathHelper.clamp(currentTab, 0, 2);
            this.boundSlot = boundSlot;
        }

        if (SoulItemHelper.areEmpty(SoulToolProvider.get(sender), data, attributes, enchantments)) {
            this.data = new int[capability.getItemAmount()][capability.getDatumAmount()];
            this.attributes = new float[capability.getItemAmount()][capability.getAttributeAmount()];
            this.enchantments = new int[capability.getItemAmount()][capability.getEnchantmentAmount()];
        } else {
            this.data = data;
            this.attributes = attributes;
            this.enchantments = enchantments;
        }
    }

    @SideOnly(CLIENT)
    public void fromBytes(ByteBuf buffer) {
        this.toolIndex = buffer.readInt();
        this.currentTab = buffer.readInt();
        this.boundSlot = buffer.readInt();

        if (Minecraft.getMinecraft().player != null) {
            SoulItemHelper.forEach(SoulToolProvider.get(Minecraft.getMinecraft().player),
                    (final Integer itemIndex, final Integer valueIndex) -> CToolData.this.data[itemIndex][valueIndex] = buffer.readInt(),
                    (final Integer itemIndex, final Integer valueIndex) -> CToolData.this.attributes[itemIndex][valueIndex] = buffer.readFloat(),
                    (final Integer itemIndex, final Integer valueIndex) -> CToolData.this.enchantments[itemIndex][valueIndex] = buffer.readInt()
            );
        }
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.toolIndex);
        buffer.writeInt(this.currentTab);
        buffer.writeInt(this.boundSlot);

        SoulItemHelper.forEach(SoulToolProvider.get(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(this.senderUUID))),
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
