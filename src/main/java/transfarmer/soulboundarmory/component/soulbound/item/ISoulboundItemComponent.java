package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.component.Component;
import nerdhub.cardinal.components.api.util.ItemComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import transfarmer.farmerlib.util.IndexedMap;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.component.soulbound.common.PlayerSoulboundComponent;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillContainer;
import transfarmer.soulboundarmory.statistics.Category;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ISoulboundItemComponent<C extends Component> extends ItemComponent<C> {
    List<SoulboundItemComponent<? extends Component>> REGISTRY = new ArrayList<>();

    static ISoulboundItemComponent<? extends Component> get(final ItemStack itemStack) {
        for (final ISoulboundItemComponent<? extends Component> component : REGISTRY) {
            if (component.getItemStack() == itemStack) {
                return component;
            }
        }

        return null;
    }

    static ISoulboundItemComponent<? extends Component> get(final Entity entity, final Item item) {
        for (final ISoulboundItemComponent<? extends Component> component : REGISTRY) {
            if (component.getItem() == item && component.getPlayer() == entity) {
                return component;
            }
        }

        return null;
    }

    static boolean isSlotBound(final int slot) {
        for (final ISoulboundItemComponent<? extends Component> component : REGISTRY) {
            if (slot == component.getBoundSlot()) {
                return true;
            }
        }

        return false;
    }

    ItemStack getItemStack();

    PlayerSoulboundComponent getParent();

    Item getItem();

    PlayerEntity getPlayer();

    Map<String, EntityAttributeModifier> getModifiers();

    List<String> getTooltip();

    Item getConsumableItem();

    ItemStack getValidEquippedStack();

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

    int onLevelup(int sign);

    int getEnchantment(Enchantment enchantment);

    IndexedMap<Enchantment, Integer> getEnchantments();

    void addEnchantment(Enchantment enchantment, int amount);

    List<SkillContainer> getSkills();

    SkillContainer getSkill(Identifier identifier);

    SkillContainer getSkill(Skill skill);

    boolean hasSkill(Identifier identifier);

    boolean hasSkill(Skill skill);

    boolean hasSkill(Skill skill, int level);

    void upgradeSkill(SkillContainer skill);

    void reset(Category category);

    void reset();

    int getCurrentTab();

    void setCurrentTab(int currentTab);

    int getBoundSlot();

    void bindSlot(int boundSlot);

    void unbindSlot();

    void refresh();

    void openGUI();

    void openGUI(int tab);

    List<ScreenTab> getTabs();

    boolean isItemEquipped();

    void tick();

    CompoundTag toClientTag();
}
