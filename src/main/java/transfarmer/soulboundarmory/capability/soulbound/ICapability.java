package transfarmer.soulboundarmory.capability.soulbound;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import java.util.List;
import java.util.Map;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public interface ICapability extends INBTSerializable<NBTTagCompound> {
    EntityPlayer getPlayer();

    void initPlayer(EntityPlayer player);

    void reset(IItem item, ICategory category);

    void reset(IItem item);

    void reset();

    ICapabilityType getType();

    IItem getItemType(int index);

    IItem getItemType(ItemStack itemStack);

    IItem getItemType(Item item);

    IItem getItemType(String item);

    IItem getItemType();

    void setItemType(IItem type);

    void setItemType(int index);

    Item getItem();

    Item getItem(IItem currentType);

    int getIndex(IItem type);

    int getIndex();

    int size(ICategory category);

    int size(IItem item, ICategory category);

    Statistic getStatistic(IItem type, IStatistic statistic);

    Statistic getStatistic(IItem item, ICategory category, IStatistic statistic);

    int getDatum(IItem type, IStatistic datum);

    void setDatum(IItem type, IStatistic datum, int amount);

    boolean addDatum(IItem type, IStatistic datum, int amount);

    double getAttributeRelative(IItem type, IStatistic attribute);

    double getAttributeTotal(IItem item, IStatistic statistic);

    double getAttribute(IItem type, IStatistic attribute);

    void setAttribute(IItem type, IStatistic attribute, double value);

    void addAttribute(IItem type, IStatistic attribute, int amount);

    double getIncrease(IItem type, IStatistic statistic);

    int getNextLevelXP(IItem type);

    int getLevelXP(IItem type, int level);

    boolean canLevelUp(IItem type);

    int onLevel(IItem item, int sign);

    int getCurrentTab();

    void setCurrentTab(int currentTab);

    int getBoundSlot();

    void bindSlot(int boundSlot);

    void unbindSlot();

    Map<String, AttributeModifier> getAttributeModifiers(IItem type);

    ItemStack getItemStack(IItem type);

    ItemStack getItemStack(ItemStack itemStack);

    List<String> getTooltip(IItem type);

    List<Item> getConsumableItems();

    @SideOnly(CLIENT)
    void onKeyPress();

    @SideOnly(CLIENT)
    void refresh();

    Class<? extends ISoulboundItem> getBaseItemClass();

    boolean hasSoulItem();

    ItemStack getEquippedItemStack();

    void onTick();

    void sync();
}
