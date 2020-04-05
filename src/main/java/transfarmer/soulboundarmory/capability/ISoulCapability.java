package transfarmer.soulboundarmory.capability;

import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import transfarmer.soulboundarmory.item.*;
import transfarmer.soulboundarmory.statistics.*;

import java.util.*;

public interface ISoulCapability {
    EntityPlayer getPlayer();

    void setPlayer(EntityPlayer player);

    void setStatistics(int[][] data, float[][] attributes, int[][] enchantments);

    int[][] getData();

    float[][] getAttributes();

    int[][] getEnchantments();

    void setData(int[][] data);

    void setAttributes(float[][] attributes);

    void setEnchantments(int[][] enchantments);

    SoulType getType(ItemStack itemStack);

    SoulType getCurrentType();

    void setCurrentType(SoulType type);

    void setCurrentType(int index);

    int getItemAmount();

    int getDatumAmount();

    int getAttributeAmount();

    int getEnchantmentAmount();

    int getDatum(SoulDatum datum, SoulType type);

    void setDatum(int amount, SoulDatum datum, SoulType type);

    boolean addDatum(int amount, SoulDatum datum, SoulType type);

    float getAttribute(SoulAttribute attribute, SoulType type, boolean total, boolean effective);

    float getAttribute(SoulAttribute attribute, SoulType type, boolean total);

    float getAttribute(SoulAttribute attribute, SoulType type);

    void setAttribute(float value, SoulAttribute attribute, SoulType type);

    void addAttribute(int amount, SoulAttribute attribute, SoulType type);

    void setData(int[] data, SoulType type);

    void setAttributes(float[] attributes, SoulType type);

    int getEnchantment(SoulEnchantment enchantment, SoulType type);

    void setEnchantments(int[] enchantments, SoulType type);

    void addEnchantment(int amount, SoulEnchantment enchantment, SoulType type);

    int getNextLevelXP(SoulType type);

    boolean canLevelUp(SoulType type);

    int getCurrentTab();

    void setCurrentTab(int currentTab);

    int getBoundSlot();

    void bindSlot(int boundSlot);

    void unbindSlot();

    Map<SoulEnchantment, Integer> getEnchantments(SoulType type);

    AttributeModifier[] getAttributeModifiers(SoulType type);

    ItemStack getItemStack(SoulType type);

    ItemStack getItemStack(ItemStack itemStack);

    List<String> getTooltip(SoulType type);

    List<Item> getConsumableItems();

    Class<? extends ISoulItem> getBaseItemClass();

    boolean hasSoulItem();

    ItemStack getEquippedItemStack();

    void update();
}
