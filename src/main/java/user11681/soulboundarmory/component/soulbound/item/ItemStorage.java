package user11681.soulboundarmory.component.soulbound.item;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.client.gui.screen.tab.SoulboundTab;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponentBase;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.component.statistics.EnchantmentStorage;
import user11681.soulboundarmory.component.statistics.SkillStorage;
import user11681.soulboundarmory.component.statistics.Statistic;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.component.statistics.Statistics;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Registries;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.usersmanual.client.gui.screen.ScreenTab;
import user11681.usersmanual.collections.ArrayMap;
import user11681.usersmanual.item.ItemUtil;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static user11681.soulboundarmory.MainClient.CLIENT;
import static user11681.soulboundarmory.component.statistics.Category.ATTRIBUTE;
import static user11681.soulboundarmory.component.statistics.Category.DATUM;
import static user11681.soulboundarmory.component.statistics.Category.ENCHANTMENT;
import static user11681.soulboundarmory.component.statistics.Category.SKILL;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_SPEED;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static user11681.soulboundarmory.component.statistics.StatisticType.ENCHANTMENT_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.EXPERIENCE;
import static user11681.soulboundarmory.component.statistics.StatisticType.LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.REACH;
import static user11681.soulboundarmory.component.statistics.StatisticType.SKILL_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;

public abstract class ItemStorage<T> implements NbtSerializable {
    protected static final NumberFormat FORMAT = DecimalFormat.getInstance();

    protected final SoulboundComponentBase component;
    protected final boolean isClient;
    protected final Item item;

    protected EnchantmentStorage enchantments;
    protected SkillStorage skillStorage;
    protected Statistics statistics;
    protected boolean unlocked;
    protected int boundSlot;
    protected int currentTab;

    public ItemStorage(final SoulboundComponentBase component, final Item item) {
        this.component = component;
        this.isClient = component.getEntity().world.isClient;
        this.item = item;
    }

    public static ItemStorage<?> get(final Entity entity, final Item item) {
        for (final SoulboundComponentBase component : Components.getComponents(entity)) {
            for (final ItemStorage<?> storage : component.getStorages().values()) {
                if (storage.getPlayer() == entity && storage.getItem() == item) {
                    return storage;
                }
            }
        }

        return null;
    }

    public static ItemStorage<?> get(final Entity entity, final StorageType<?> type) {
        for (final ComponentType<? extends SoulboundComponentBase> component : Components.SOULBOUND_COMPONENTS) {
            for (final ItemStorage<?> storage : component.get(entity).getStorages().values()) {
                if (storage.getType() == type) {
                    return storage;
                }
            }
        }

        return null;
    }

    public PlayerEntity getPlayer() {
        return this.component.getEntity();
    }

    public SoulboundComponentBase getComponent() {
        return this.component;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStack getValidEquippedStack() {
        for (final ItemStack itemStack : this.getPlayer().getItemsHand()) {
            final Item item = itemStack.getItem();

            if (item == this.getItem() || item == this.getConsumableItem()) {
                return itemStack;
            }
        }

        return null;
    }

    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void setUnlocked(final boolean unlocked) {
        this.unlocked = true;
    }

    public int size(final Category category) {
        return this.statistics.size(category);
    }

    public Statistic getStatistic(final StatisticType statistic) {
        return this.statistics.get(statistic);
    }

    public Statistic getStatistic(final Category category, final StatisticType statistic) {
        return this.statistics.get(category, statistic);
    }

    public int getDatum(final StatisticType statistic) {
        return this.statistics.get(statistic).intValue();
    }

    public void setDatum(final StatisticType datum, final int value) {
        this.statistics.put(datum, value);
    }

    public double getAttributeTotal(StatisticType statistic) {
        if (statistic == ATTACK_DAMAGE) {
            double attackDamage = this.getAttribute(ATTACK_DAMAGE);

            for (final Enchantment enchantment : this.enchantments) {
                attackDamage += enchantment.getAttackDamage(this.getEnchantment(enchantment), EntityGroup.DEFAULT);
            }

            return attackDamage;
        }

        return this.getAttribute(statistic);
    }

    public double getAttributeRelative(final StatisticType attribute) {
        if (attribute == ATTACK_SPEED) {
            return this.getAttribute(ATTACK_SPEED) - 4;
        }

        if (attribute == ATTACK_DAMAGE) {
            return this.getAttribute(ATTACK_DAMAGE) - 1;
        }

        if (attribute == REACH) {
            return this.getAttribute(REACH) - 3;
        }

        return this.getAttribute(attribute);
    }

    public double getAttribute(final StatisticType statistic) {
        return this.statistics.get(statistic).doubleValue();
    }

    public void incrementPoints(final StatisticType statistic, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (sign > 0 && this.getDatum(ATTRIBUTE_POINTS) > 0 || sign < 0 && this.getDatum(SPENT_ATTRIBUTE_POINTS) > 0) {
                this.incrementStatistic(ATTRIBUTE_POINTS, -sign);
                this.incrementStatistic(SPENT_ATTRIBUTE_POINTS, sign);

                final Statistic instance = this.getStatistic(statistic);
                final double change = sign * this.getIncrease(statistic);

                if (instance.doubleValue() + change <= instance.getMin()) {
                    instance.setValue(instance.getMin());
                } else if (instance.doubleValue() + change >= instance.getMax()) {
                    instance.setValue(instance.getMax());
                } else {
                    instance.add(change);
                }
            }
        }
    }

    public boolean incrementStatistic(final StatisticType statistic, final int amount) {
        boolean leveledUp = false;

        if (statistic == EXPERIENCE) {
            final int xp = this.statistics.add(EXPERIENCE, amount).intValue();

            if (xp >= this.getNextLevelXP() && this.canLevelUp()) {
                final int nextLevelXP = this.getNextLevelXP();

                this.incrementStatistic(LEVEL, 1);
                this.incrementStatistic(EXPERIENCE, -nextLevelXP);

                leveledUp = true;
            }

            if (xp < 0) {
                final int currentLevelXP = this.getLevelXP(this.getDatum(LEVEL) - 1);

                this.incrementStatistic(LEVEL, -1);
                this.incrementStatistic(EXPERIENCE, currentLevelXP);
            }
        } else if (statistic == LEVEL) {
            final int sign = (int) Math.signum(amount);

            for (int i = 0; i < Math.abs(amount); i++) {
                this.onLevelup(sign);
            }
        } else {
            this.statistics.add(statistic, amount);
        }

        this.sync();

        return leveledUp;
    }

    public void setAttribute(final StatisticType statistic, final double value) {
        this.statistics.put(statistic, value);
    }

    public boolean canLevelUp() {
        return this.getDatum(LEVEL) < Configuration.instance().maxLevel || Configuration.instance().maxLevel < 0;
    }

    public int onLevelup(final int sign) {
        final int level = this.statistics.add(LEVEL, sign).intValue();

        if (level % Configuration.instance().levelsPerEnchantment == 0) {
            this.incrementStatistic(ENCHANTMENT_POINTS, sign);
        }

        if (level % Configuration.instance().levelsPerSkillPoint == 0) {
            this.incrementStatistic(SKILL_POINTS, sign);
        }

        this.incrementStatistic(ATTRIBUTE_POINTS, sign);

        return level;
    }

    public List<SkillContainer> getSkills() {
        final List<SkillContainer> skills = new ArrayList<>(this.skillStorage.values());

        skills.sort(Comparator.comparingInt(SkillContainer::getTier));

        return skills;
    }

    public SkillContainer getSkill(final Identifier identifier) {
        return this.getSkill(Registries.SKILL.get(identifier));
    }

    public SkillContainer getSkill(final Skill skill) {
        return this.skillStorage.get(skill);
    }

    public boolean hasSkill(final Identifier identifier) {
        return this.hasSkill(Registries.SKILL.get(identifier));
    }

    public boolean hasSkill(final Skill skill) {
        return this.skillStorage.contains(skill);
    }

    public boolean hasSkill(final Skill skill, final int level) {
        return this.skillStorage.contains(skill, level);
    }

    public void upgradeSkill(final SkillContainer skill) {
//        if (this.isClient) {
//            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SKILL, new ExtendedPacketBuffer(this, item).writeString(skill.toString()));
//        } else {
        final int points = this.getDatum(SKILL_POINTS);
        final int cost = skill.getCost();

        if (skill.canBeLearned(points)) {
            skill.learn();

            this.incrementStatistic(SKILL_POINTS, -cost);
        } else if (skill.canBeUpgraded(points)) {
            skill.upgrade();

            this.incrementStatistic(SKILL_POINTS, -cost);
        }
//        }
    }

    public int getNextLevelXP() {
        return this.getLevelXP(this.getDatum(LEVEL));
    }

    public abstract Map<String, EntityAttributeModifier> getModifiers();

    public int getEnchantment(final Enchantment enchantment) {
        return this.getEnchantments().getOrDefault(enchantment, -1);
    }

    public ArrayMap<Enchantment, Integer> getEnchantments() {
        return this.enchantments.get();
    }

    public void addEnchantment(final Enchantment enchantment, final int value) {
        final int current = this.getEnchantment(enchantment);
        final int change = Math.max(0, current + value) - current;

        this.statistics.add(ENCHANTMENT_POINTS, -change);
        this.statistics.add(SPENT_ENCHANTMENT_POINTS, change);

        this.enchantments.add(enchantment, change);
    }

    public void reset() {
        this.statistics.reset();
        this.enchantments.reset();
        this.skillStorage.reset();
    }

    public void reset(final Category category) {
        this.statistics.reset(category);

        if (category == DATUM) {
            this.statistics.reset(DATUM);
        } else if (category == ATTRIBUTE) {
            this.incrementStatistic(ATTRIBUTE_POINTS, this.getDatum(SPENT_ATTRIBUTE_POINTS));
            this.setDatum(SPENT_ATTRIBUTE_POINTS, 0);
        } else if (category == ENCHANTMENT) {
            this.enchantments.reset();

            this.incrementStatistic(ENCHANTMENT_POINTS, this.getDatum(SPENT_ENCHANTMENT_POINTS));
            this.setDatum(SPENT_ENCHANTMENT_POINTS, 0);
        } else if (category == SKILL) {
            this.skillStorage.reset();
        }
    }

    public boolean canUnlock() {
        return !this.unlocked && ItemUtil.isItemEquipped(this.getPlayer(), this.getItem());
    }

    public boolean canConsume(final Item item) {
        return this.getConsumableItem() == item;
    }

    public int getBoundSlot() {
        return this.boundSlot;
    }

    public void bindSlot(final int boundSlot) {
        this.boundSlot = boundSlot;
    }

    public void unbindSlot() {
        this.boundSlot = -1;
    }

    public int getCurrentTab() {
        return this.currentTab;
    }

    public void setCurrentTab(final int tab) {
        this.currentTab = tab;

        if (this.isClient) {
            this.sync();
        }
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void refresh() {
        if (this.isClient) {
            if (CLIENT.currentScreen instanceof SoulboundTab) {
                final List<Item> handItems = ItemUtil.getHandItems(this.getPlayer());

                if (handItems.contains(this.getItem())) {
                    this.openGUI();
                } else if (handItems.contains(this.getConsumableItem())) {
                    this.openGUI(0);
                }
            }
        } else {
            Main.PACKET_REGISTRY.sendToPlayer(this.getPlayer(), Packets.S2C_REFRESH, new ExtendedPacketBuffer(this));
        }
    }

    public void openGUI() {
        this.openGUI(ItemUtil.getEquippedItemStack(this.getPlayer().inventory, SoulboundItem.class) == null ? 0 : this.currentTab);
    }

    @SuppressWarnings({"LocalVariableDeclarationSideOnly", "VariableUseSideOnly", "MethodCallSideOnly"})
    public void openGUI(final int tab) {
        if (this.isClient) {
            final Screen currentScreen = CLIENT.currentScreen;

            if (currentScreen instanceof SoulboundTab && this.currentTab == tab) {
                ((SoulboundTab) currentScreen).refresh();
            } else {
                final List<ScreenTab> tabs = this.getTabs();

                CLIENT.openScreen(tabs.get(MathHelper.clamp(tab, 0, tabs.size() - 1)));
            }
        } else {
            Main.PACKET_REGISTRY.sendToPlayer(this.getPlayer(), Packets.S2C_OPEN_GUI, new ExtendedPacketBuffer(this).writeInt(tab));
        }
    }

    public boolean isItemEquipped() {
        return ItemUtil.isItemEquipped(this.getPlayer(), this.getItem());
    }

    public boolean isAnyItemEquipped() {
        return this.getValidEquippedStack() != null;
    }

    public ItemStack getItemStack() {
        final ItemStack itemStack = new ItemStack(this.getItem());
        final Map<String, EntityAttributeModifier> attributeModifiers = this.getModifiers();
        final Map<Enchantment, Integer> enchantments = this.getEnchantments();

        for (final String name : attributeModifiers.keySet()) {
            itemStack.addAttributeModifier(name, attributeModifiers.get(name), MAINHAND);
        }

        for (final Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            final Integer level = entry.getValue();

            if (level > 0) {
                itemStack.addEnchantment(entry.getKey(), level);
            }
        }

        return itemStack;
    }

    protected String formatStatistic(final StatisticType statistic) {
        final double value = this.getStatistic(statistic).doubleValue();

        return FORMAT.format(statistic == CRITICAL_STRIKE_PROBABILITY ? value * 100 : value);
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {
        this.statistics.fromTag(tag.getCompound("statistics"));
        this.enchantments.fromTag(tag.getCompound("enchantments"));
        this.skillStorage.fromTag(tag.getCompound("skills"));
        this.bindSlot(tag.getInt("slot"));
        this.unlocked = tag.getBoolean("unlocked");
        this.setCurrentTab(tag.getInt("tab"));
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        tag.put("statistics", this.statistics.toTag(new CompoundTag()));
        tag.put("enchantments", this.enchantments.toTag(new CompoundTag()));
        tag.put("skills", this.skillStorage.toTag(new CompoundTag()));
        tag.putBoolean("unlocked", this.unlocked);
        tag.putInt("slot", this.getBoundSlot());
        tag.putInt("tab", this.getCurrentTab());

        return tag;
    }

    public CompoundTag toClientTag() {
        final CompoundTag tag = new CompoundTag();

        tag.putInt("tab", this.currentTab);

        return tag;
    }

    public void tick() {
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void sync() {
        if (!this.isClient) {
            Main.PACKET_REGISTRY.sendToPlayer(this.getPlayer(), Packets.S2C_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toTag(new CompoundTag())));
        } else {
            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toClientTag()));
        }
    }

    public abstract Text getName();

    public abstract ArrayMap<Statistic, Text> getScreenAttributes();

    public abstract List<Text> getTooltip();

    public abstract Item getConsumableItem();

    public abstract double getIncrease(StatisticType statistic);

    public abstract int getLevelXP(int level);

    public abstract List<ScreenTab> getTabs();

    public abstract StorageType<T> getType();

    public abstract Class<? extends SoulboundItem> getBaseItemClass();
}
