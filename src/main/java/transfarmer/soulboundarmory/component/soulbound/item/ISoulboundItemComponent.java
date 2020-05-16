package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.util.ItemComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import transfarmer.farmerlib.util.IndexedMap;
import transfarmer.soulboundarmory.item.SoulboundItem;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.statistics.Category;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.util.List;
import java.util.Map;

public interface ISoulboundItemComponent<C extends SoulboundItemComponent<?>> extends ItemComponent<C> {
    ItemStack getItemStack();

    Item getItem();

    Map<String, EntityAttributeModifier> getModifiers();

    List<String> getTooltip();

    Item getConsumableItem();

    boolean canConsume(Item item);

    boolean canUnlock();

    boolean isUnlocked();

    void setUnlocked(boolean unlocked);

    int size(Category category);

    Statistic getStatistic(StatisticType statistic);

    Statistic getStatistic(Category category, StatisticType statistic);

    int getDatum(StatisticType statistic);

    void setDatum(StatisticType datum, int amount);

    boolean addDatum(StatisticType statistic, int amount);

    double getAttributeRelative(StatisticType attribute);

    double getAttributeTotal(StatisticType statistic);

    double getAttribute(StatisticType attribute);

    void setAttribute(StatisticType attribute, double value);

    void addAttribute(StatisticType attribute, int amount);

    double getIncrease(StatisticType statistic);

    int getNextLevelXP();

    int getLevelXP(int level);

    boolean canLevelUp();

    int onLevel(int sign);

    int getEnchantment(Enchantment enchantment);

    IndexedMap<Enchantment, Integer> getEnchantments();

    void addEnchantment(Enchantment enchantment, int amount);

    List<Skill> getSkills();

    Skill getSkill(Identifier identifier);

    Skill getSkill(Skill skill);

    boolean hasSkill(Identifier identifier);

    boolean hasSkill(Skill skill);

    boolean hasSkill(Skill skill, int level);

    void upgradeSkill(Skill skill);

    void reset(Category category);

    void reset();

    Class<? extends SoulboundItem> getBaseItemClass();

    PlayerEntity getPlayer();
}
