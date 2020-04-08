package transfarmer.soulboundarmory.capability;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.SoulType;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface ISoulCapability {
    void forEach(BiConsumer<Integer, Integer> data, BiConsumer<Integer, Integer> attributes, BiConsumer<Integer, Integer> enchantments);

    EntityPlayer getPlayer();

    void initPlayer(EntityPlayer player);

    void setStatistics(int[][] data, float[][] attributes, int[][] enchantments);

    int[][] getData();

    float[][] getAttributes();

    int[][] getEnchantments();

    void setData(int[][] data);

    void setAttributes(float[][] attributes);

    void setEnchantments(int[][] enchantments);

    void init();

    SoulType getType(int index);

    SoulType getType(ItemStack itemStack);

    SoulType getCurrentType();

    int getIndex(SoulType type);

    int getIndex();

    int getLevelXP(SoulType type, int level);

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

    NBTTagCompound writeNBT();

    void readNBT(NBTTagCompound nbt);

    void sync();
}
