package transfarmer.soulboundarmory.component.soulbound.common;

import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import transfarmer.farmerlib.util.IndexedMap;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillLevelable;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import java.util.List;
import java.util.Map;

public interface ISoulboundComponent extends EntitySyncedComponent {
    PlayerEntity getPlayer();

    void initPlayer(PlayerEntity player);

    void reset(IItem item, ICategory category);

    void reset(IItem item);

    void reset();

    void resetEnchantments(IItem item);

    void resetSkills(IItem item);

    ICapabilityType getType();

    IItem getItemType(int index);

    IItem getItemType(ItemStack itemStack);

    IItem getItemType(Item item);

    IItem getItemType(String item);

    IItem getItemType();

    void setItemType(IItem type);

    void setItemType(int index);

    Item getItem();

    Item getItem(IItem item);

    int getIndex(IItem item);

    int getIndex();

    boolean isUnlocked(IItem item);

    boolean isUnlocked(int index);

    void setUnlocked(IItem item, boolean unlocked);

    int size(ICategory category);

    int size(IItem item, ICategory category);

    Statistic getStatistic(IItem type, IStatistic statistic);

    Statistic getStatistic(IItem item, ICategory category, IStatistic statistic);

    int getDatum(IStatistic statistic);

    int getDatum(IItem type, IStatistic datum);

    void setDatum(IItem type, IStatistic datum, int amount);

    boolean addDatum(IStatistic statistic, int amount);

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

    Map<String, EntityAttributeModifier> getEntityAttributeModifiers(IItem type);

    ItemStack getItemStack(IItem type);

    ItemStack getItemStack(ItemStack itemStack);

    List<String> getTooltip(IItem item);

    Item getConsumableItem(IItem item);

    List<Item> getConsumableItems();

    boolean canConsume(Item item, IItem type);

    boolean canConsume(Item item, int index);

    boolean canUnlock(IItem item);

    boolean canUnlock(int index);

    void refresh();

    void openGUI();

    void openGUI(int tab);

    List<ScreenTab> getTabs();

    int getEnchantment(IItem type, Enchantment enchantment);

    IndexedMap<Enchantment, Integer> getEnchantments();

    IndexedMap<Enchantment, Integer> getEnchantments(IItem type);

    void addEnchantment(IItem type, Enchantment enchantment, int amount);

    List<Skill> getSkills();

    List<Skill> getSkills(IItem type);

    Skill getSkill(String skill);

    Skill getSkill(IItem item, String skill);

    Skill getSkill(Skill skill);

    Skill getSkill(IItem item, Skill skill);

    SkillLevelable getSkillLevelable(IItem item, String skill);

    boolean hasSkill(IItem item, String skill);

    boolean hasSkill(IItem item, Skill skill);

    boolean hasSkill(IItem item, SkillLevelable skill, int level);

    void upgradeSkill(IItem item, Skill skill);

    Class<? extends ItemSoulbound> getBaseItemClass();

    boolean hasSoulboundItem();

    ItemStack getEquippedItemStack();

    void onTick();

    CompoundTag toCompoundTagClient();

    void sync();
}
