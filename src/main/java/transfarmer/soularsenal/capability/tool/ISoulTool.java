package transfarmer.soularsenal.capability.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import transfarmer.soularsenal.data.tool.SoulToolAttribute;
import transfarmer.soularsenal.data.tool.SoulToolDatum;
import transfarmer.soularsenal.data.tool.SoulToolEnchantment;
import transfarmer.soularsenal.data.tool.SoulToolType;

import java.util.SortedMap;

public interface ISoulTool {
    void setStatistics(int[][] data, float[][] attributes, int[][] enchantments);

    int[][] getData();
    float[][] getAttributes();
    int[][] getEnchantments();

    int getDatum(SoulToolDatum datum, SoulToolType type);
    boolean addDatum(int amount, SoulToolDatum datum, SoulToolType type);
    void setDatum(int amount, SoulToolDatum datum, SoulToolType type);

    float getAttribute(SoulToolAttribute attribute, SoulToolType type);
    void setAttributes(float[] attributes, SoulToolType type);
    void addAttribute(int amount, SoulToolAttribute attribute, SoulToolType toolType);

    int getEnchantment(SoulToolEnchantment enchantment, SoulToolType type);
    void setEnchantments(int[] enchantments, SoulToolType type);
    void addEnchantment(int amount, SoulToolEnchantment enchantment, SoulToolType toolType);

    SoulToolType getCurrentType();
    void setCurrentType(SoulToolType type);
    void setCurrentType(int index);

    float getEffectiveEfficiency(SoulToolType type);

    ItemStack getItemStack(SoulToolType type);
    ItemStack getItemStack(ItemStack itemStack);

    AttributeModifier[] getAttributeModifiers(SoulToolType type);

    SortedMap<SoulToolEnchantment, Integer> getEnchantments(SoulToolType type);

    String[] getTooltip(SoulToolType type);

    int getBoundSlot();
    void setBoundSlot(int slot);
    void unbindSlot();

    int getCurrentTab();
    void setCurrentTab(int tab);

    int getNextLevelXP(SoulToolType type);

    float getEffectiveReachDistance(SoulToolType type);
}
