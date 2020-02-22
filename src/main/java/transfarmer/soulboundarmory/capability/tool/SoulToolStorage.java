package transfarmer.soulboundarmory.capability.tool;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

public class SoulToolStorage implements IStorage<ISoulCapability> {
    @Override
    public NBTBase writeNBT(Capability<ISoulCapability> capability, ISoulCapability instance, EnumFacing facing) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("soultools.capability.index", instance.getCurrentType() == null ? -1 : instance.getCurrentType().getIndex());
        tag.setInteger("soultools.capability.tab", instance.getCurrentTab());
        tag.setInteger("soultools.capability.boundSlot", instance.getBoundSlot());
        final int[][] data = instance.getData();
        final float[][] attributes = instance.getAttributes();
        final int[][] enchantments = instance.getEnchantments();

        SoulItemHelper.forEach(instance,
            (Integer toolIndex, Integer valueIndex) ->
                tag.setInteger(String.format("soultools.datum.%s.%s",
                    SoulToolType.get(toolIndex),
                    SoulToolDatum.get(valueIndex)),
                    data[toolIndex][valueIndex]),
            (Integer toolIndex, Integer valueIndex) ->
                tag.setFloat(String.format("soultools.attribute.%s.%s",
                    SoulToolType.get(toolIndex),
                    SoulToolAttribute.get(valueIndex)),
                    attributes[toolIndex][valueIndex]),
            (Integer toolIndex, Integer valueIndex) ->
                tag.setInteger(String.format("soultools.enchantment.%s.%s",
                    SoulToolType.get(toolIndex),
                    SoulToolEnchantment.get(valueIndex)),
                    enchantments[toolIndex][valueIndex])
        );

        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulCapability> capability, ISoulCapability instance, EnumFacing facing, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setCurrentType(tag.getInteger("soultools.capability.index"));
        instance.setCurrentTab(tag.getInteger("soultools.capability.tab"));
        instance.bindSlot(tag.getInteger("soultools.capability.boundSlot"));
        final int[][] data = new int[instance.getItemAmount()][instance.getDatumAmount()];
        final float[][] attributes = new float[instance.getItemAmount()][instance.getAttributeAmount()];
        final int[][] enchantments = new int[instance.getItemAmount()][instance.getEnchantmentAmount()];

        SoulItemHelper.forEach(instance,
            (Integer toolIndex, Integer valueIndex) ->
                data[toolIndex][valueIndex] = tag.getInteger(String.format("soultools.datum.%s.%s",
                    SoulToolType.get(toolIndex),
                    SoulToolDatum.get(valueIndex)
                )),
            (Integer toolIndex, Integer valueIndex) ->
                attributes[toolIndex][valueIndex] = tag.getFloat(String.format("soultools.attribute.%s.%s",
                        SoulToolType.get(toolIndex),
                        SoulToolAttribute.get(valueIndex)
                )),
            (Integer toolIndex, Integer valueIndex) ->
                enchantments[toolIndex][valueIndex] = tag.getInteger(String.format("soultools.enchantment.%s.%s",
                        SoulToolType.get(toolIndex),
                        SoulToolEnchantment.get(valueIndex)
                ))
        );

        instance.setStatistics(data, attributes, enchantments);
    }
}
