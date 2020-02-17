package transfarmer.soularsenal.capability.tool;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soularsenal.data.tool.SoulToolAttribute;
import transfarmer.soularsenal.data.tool.SoulToolDatum;
import transfarmer.soularsenal.data.tool.SoulToolEnchantment;
import transfarmer.soularsenal.data.tool.SoulToolType;

import static transfarmer.soularsenal.capability.tool.SoulToolHelper.*;

public class SoulToolStorage implements IStorage<ISoulTool> {
    @Override
    public NBTBase writeNBT(Capability<ISoulTool> capability, ISoulTool instance, EnumFacing facing) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("soultools.capability.index", instance.getCurrentType() == null ? -1 : instance.getCurrentType().index);
        tag.setInteger("soultools.capability.tab", instance.getCurrentTab());
        tag.setInteger("soultools.capability.boundSlot", instance.getBoundSlot());
        final int[][] data = instance.getData();
        final float[][] attributes = instance.getAttributes();
        final int[][] enchantments = instance.getEnchantments();

        SoulToolHelper.forEach(
            (Integer toolIndex, Integer valueIndex) ->
                tag.setInteger(String.format("soultools.datum.%s.%s",
                    SoulToolType.getType(toolIndex),
                    SoulToolDatum.getName(valueIndex)),
                    data[toolIndex][valueIndex]),
            (Integer toolIndex, Integer valueIndex) ->
                tag.setFloat(String.format("soultools.attribute.%s.%s",
                    SoulToolType.getType(toolIndex),
                    SoulToolAttribute.getName(valueIndex)),
                    attributes[toolIndex][valueIndex]),
            (Integer toolIndex, Integer valueIndex) ->
                tag.setInteger(String.format("soultools.enchantment.%s.%s",
                    SoulToolType.getType(toolIndex),
                    SoulToolEnchantment.getName(valueIndex)),
                    enchantments[toolIndex][valueIndex])
        );

        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulTool> capability, ISoulTool instance, EnumFacing facing, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setCurrentType(tag.getInteger("soultools.capability.index"));
        instance.setCurrentTab(tag.getInteger("soultools.capability.tab"));
        instance.setBoundSlot(tag.getInteger("soultools.capability.boundSlot"));
        final int[][] data = new int[SOUL_TOOLS][DATA];
        final float[][] attributes = new float[SOUL_TOOLS][ATTRIBUTES];
        final int[][] enchantments = new int[SOUL_TOOLS][ENCHANTMENTS];

        SoulToolHelper.forEach(
            (Integer toolIndex, Integer valueIndex) ->
                data[toolIndex][valueIndex] = tag.getInteger(String.format("soultools.datum.%s.%s",
                    SoulToolType.getType(toolIndex),
                    SoulToolDatum.getName(valueIndex)
                )),
            (Integer toolIndex, Integer valueIndex) ->
                attributes[toolIndex][valueIndex] = tag.getFloat(String.format("soultools.attribute.%s.%s",
                        SoulToolType.getType(toolIndex),
                        SoulToolAttribute.getName(valueIndex)
                )),
            (Integer toolIndex, Integer valueIndex) ->
                enchantments[toolIndex][valueIndex] = tag.getInteger(String.format("soultools.enchantment.%s.%s",
                        SoulToolType.getType(toolIndex),
                        SoulToolEnchantment.getName(valueIndex)
                ))
        );

        instance.setStatistics(data, attributes, enchantments);
    }
}
