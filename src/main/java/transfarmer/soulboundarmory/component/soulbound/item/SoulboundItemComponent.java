package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.component.Component;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import nerdhub.cardinal.components.api.util.ItemComponent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.farmerlib.item.ItemModifiers;
import transfarmer.farmerlib.item.ItemUtil;
import transfarmer.farmerlib.util.IndexedMap;
import transfarmer.reachentityattributes.ReachEntityAttributes;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SoulboundTab;
import transfarmer.soulboundarmory.component.soulbound.common.IPlayerSoulboundComponent;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.SoulboundItem;
import transfarmer.soulboundarmory.network.Packets;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillContainer;
import transfarmer.soulboundarmory.skill.Skills;
import transfarmer.soulboundarmory.statistics.Category;
import transfarmer.soulboundarmory.statistics.EnchantmentStorage;
import transfarmer.soulboundarmory.statistics.SkillStorage;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.StatisticType;
import transfarmer.soulboundarmory.statistics.Statistics;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static transfarmer.soulboundarmory.Main.COMPONENTS;
import static transfarmer.soulboundarmory.MainClient.CLIENT;
import static transfarmer.soulboundarmory.statistics.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.Category.ENCHANTMENT;
import static transfarmer.soulboundarmory.statistics.Category.SKILL;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.REACH;
import static transfarmer.soulboundarmory.statistics.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.XP;

public abstract class SoulboundItemComponent<C extends Component> implements ISoulboundItemComponent<C> {
    protected final ItemStack itemStack;
    protected final PlayerEntity player;
    protected final IPlayerSoulboundComponent parent;
    protected final boolean isClient;

    protected EnchantmentStorage enchantments;
    protected SkillStorage skillStorage;
    protected Statistics statistics;
    protected boolean unlocked;
    protected int boundSlot;
    protected int currentTab;

    public SoulboundItemComponent(final ItemStack itemStack, final PlayerEntity player) {
        this.itemStack = itemStack;
        this.player = player;
        this.parent = COMPONENTS.get(ComponentProvider.fromEntity(player));
        this.isClient = player.world.isClient;

        REGISTRY.add(this);
    }

    @Override
    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public IPlayerSoulboundComponent getParent() {
        return this.parent;
    }

    @Override
    public boolean isComponentEqual(final Component other) {
        return other instanceof ItemComponent && other.toTag(new CompoundTag()).equals(this.toTag(new CompoundTag()));
    }

    @Override
    public Item getItem() {
        return this.itemStack.getItem();
    }

    @Override
    public ItemStack getValidEquippedStack() {
        for (final ItemStack itemStack : this.player.getItemsHand()) {
            final Item item = itemStack.getItem();

            if (item == this.getItem() || item == this.getConsumableItem()) {
                return itemStack;
            }
        }

        return null;
    }

    @Override
    public boolean isUnlocked() {
        return this.unlocked;
    }

    @Override
    public void setUnlocked(final boolean unlocked) {
        this.unlocked = true;
    }

    @Override
    public int size(final Category category) {
        return this.statistics.size(category);
    }

    @Override
    public Statistic getStatistic(final StatisticType statistic) {
        return this.statistics.get(statistic);
    }

    @Override
    public Statistic getStatistic(final Category category, final StatisticType statistic) {
        return this.statistics.get(category, statistic);
    }

    @Override
    public int getDatum(final StatisticType statistic) {
        return this.statistics.get(statistic).intValue();
    }

    @Override
    public void setDatum(final StatisticType datum, final int value) {
        this.statistics.set(datum, value);
    }

    @Override
    public boolean addDatum(final StatisticType datum, final int amount) {
        if (datum == XP) {
            final int xp = this.statistics.add(XP, amount).intValue();

            if (xp >= this.getNextLevelXP() && this.canLevelUp()) {
                final int nextLevelXP = this.getNextLevelXP();

                this.addDatum(LEVEL, 1);
                this.addDatum(XP, -nextLevelXP);

                return true;
            } else if (xp < 0) {
                final int currentLevelXP = this.getLevelXP(this.getDatum(LEVEL) - 1);

                this.addDatum(LEVEL, -1);
                this.addDatum(XP, currentLevelXP);

                return false;
            }
        } else if (datum == LEVEL) {
            final int sign = (int) Math.signum(amount);

            for (int i = 0; i < Math.abs(amount); i++) {
                this.onLevelup(sign);
            }
        } else {
            this.statistics.add(datum, amount);
        }

        return false;
    }

    @Override
    public double getAttribute(final StatisticType statistic) {
        return this.statistics.get(statistic).doubleValue();
    }


    @Override
    public double getAttributeRelative(final StatisticType attribute) {
        if (attribute == REACH) {
            return this.getAttribute(REACH) - 3;
        }

        return this.getStatistic(attribute).doubleValue();
    }

    @Override
    public void setAttribute(final StatisticType statistic, final double value) {
        this.statistics.set(statistic, value);
    }

    @Override
    public boolean canLevelUp() {
        return this.getDatum(LEVEL) < MainConfig.instance().maxLevel || MainConfig.instance().maxLevel < 0;
    }

    @Override
    public int onLevelup(final int sign) {
        final int level = this.statistics.add(LEVEL, sign).intValue();

        if (level % MainConfig.instance().levelsPerEnchantment == 0) {
            this.addDatum(ENCHANTMENT_POINTS, sign);
        }

        if (level % MainConfig.instance().levelsPerSkill == 0) {
            this.addDatum(SKILL_POINTS, sign);
        }

        this.addDatum(ATTRIBUTE_POINTS, sign);

        return level;
    }

    @Override
    public List<SkillContainer> getSkills() {
        final List<SkillContainer> skills = new ArrayList<>(this.skillStorage.values());

        skills.sort(Comparator.comparingInt(SkillContainer::getTier));

        return skills;
    }

    @Override
    public SkillContainer getSkill(final Identifier identifier) {
        return this.getSkill(Skills.get(identifier));
    }

    @Override
    public SkillContainer getSkill(final Skill skill) {
        return this.skillStorage.get(skill);
    }

    @Override
    public boolean hasSkill(final Identifier identifier) {
        return this.hasSkill(Skills.get(identifier));
    }

    @Override
    public boolean hasSkill(final Skill skill) {
        return this.skillStorage.contains(skill);
    }

    @Override
    public boolean hasSkill(final Skill skill, final int level) {
        return this.skillStorage.contains(skill, level);
    }

    @Override
    public void upgradeSkill(final SkillContainer skill) {
//        if (this.isClient) {
//            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SKILL, new ExtendedPacketBuffer(this, item).writeString(skill.toString()));
//        } else {
        final int points = this.getDatum(SKILL_POINTS);
        final int cost = skill.getCost();

        if (skill.canBeLearned(points)) {
            skill.learn();

            this.addDatum(SKILL_POINTS, -cost);
        } else if (skill.canBeUpgraded(points)) {
            skill.upgrade();

            this.addDatum(SKILL_POINTS, -cost);
        }
//        }
    }

    @Override
    public int getNextLevelXP() {
        return this.getLevelXP(this.getDatum(LEVEL));
    }

    @Override
    public Map<String, EntityAttributeModifier> getModifiers() {
        return CollectionUtil.hashMap(
                new String[]{
                        EntityAttributes.ATTACK_SPEED.getId(),
                        EntityAttributes.ATTACK_DAMAGE.getId(),
                        ReachEntityAttributes.ATTACK_RANGE.getId(),
                        ReachEntityAttributes.REACH.getId()
                },
                new EntityAttributeModifier(ItemModifiers.ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(ATTACK_SPEED), ADDITION),
                new EntityAttributeModifier(ItemModifiers.ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(ATTACK_DAMAGE), ADDITION),
                new EntityAttributeModifier(Main.ATTACK_RANGE_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(StatisticType.ATTACK_RANGE), ADDITION),
                new EntityAttributeModifier(Main.REACH_MODIFIER_UUID, "Tool modifier", this.getAttributeRelative(StatisticType.REACH), ADDITION)
        );
    }

    @Override
    public int getEnchantment(final Enchantment enchantment) {
        return this.getEnchantments().getOrDefault(enchantment, -1);
    }

    @Override
    public IndexedMap<Enchantment, Integer> getEnchantments() {
        return this.enchantments.get();
    }

    @Override
    public void addEnchantment(final Enchantment enchantment, final int value) {
        final int current = this.getEnchantment(enchantment);
        final int change = Math.max(0, current + value) - current;

        this.statistics.add(ENCHANTMENT_POINTS, -change);
        this.statistics.add(SPENT_ENCHANTMENT_POINTS, change);

        this.enchantments.add(enchantment, change);
    }

    @Override
    public void reset() {
        this.statistics.reset();
        this.enchantments.reset();
        this.skillStorage.reset();
    }

    @Override
    public void reset(final Category category) {
        this.statistics.reset(category);

        if (category == DATUM) {
            this.statistics.reset(DATUM);
        } else if (category == ATTRIBUTE) {
            this.addDatum(ATTRIBUTE_POINTS, this.getDatum(SPENT_ATTRIBUTE_POINTS));
            this.setDatum(SPENT_ATTRIBUTE_POINTS, 0);
        } else if (category == ENCHANTMENT) {
            this.enchantments.reset();

            this.addDatum(ENCHANTMENT_POINTS, this.getDatum(SPENT_ENCHANTMENT_POINTS));
            this.setDatum(SPENT_ENCHANTMENT_POINTS, 0);
        } else if (category == SKILL) {
            this.skillStorage.reset();
        }
    }

    @Override
    public boolean canUnlock() {
        return !this.unlocked && ItemUtil.isItemEquipped(this.player, this.getItem());
    }

    @Override
    public boolean canConsume(final Item item) {
        return this.getConsumableItem() == item;
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public void bindSlot(final int boundSlot) {
        this.boundSlot = boundSlot;
    }

    @Override
    public void unbindSlot() {
        this.boundSlot = -1;
    }

    @Override
    public int getCurrentTab() {
        return this.currentTab;
    }

    @Override
    public void setCurrentTab(final int tab) {
        this.currentTab = tab;

//        if (this.isClient) {
//            this.sync();
//        }
    }

    @SuppressWarnings("VariableUseSideOnly")
    @Override
    public void refresh() {
        if (this.isClient) {
            if (CLIENT.currentScreen instanceof SoulboundTab) {
                final List<Item> handItems = ItemUtil.getHandItems(this.player);

                if (handItems.contains(this.getItem())) {
                    this.openGUI();
                } else if (handItems.contains(this.getConsumableItem())) {
                    this.openGUI(0);
                }
            }
        } else {
            Main.PACKET_REGISTRY.sendToPlayer(this.player, Packets.S2C_REFRESH, new ExtendedPacketBuffer(this));
        }
    }

    @Override
    public void openGUI() {
        this.openGUI(ItemUtil.getEquippedItemStack(this.player.inventory, SoulboundItem.class) == null ? 0 : this.currentTab);
    }

    @SuppressWarnings({"LocalVariableDeclarationSideOnly", "VariableUseSideOnly", "MethodCallSideOnly"})
    @Override
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
            Main.PACKET_REGISTRY.sendToPlayer(this.player, Packets.S2C_OPEN_GUI, new ExtendedPacketBuffer(this).writeInt(tab));
        }
    }

    @Override
    public boolean isItemEquipped() {
        return ItemUtil.isItemEquipped(this.player, this.getItem());
    }

    @Override
    public boolean isAnyItemEquipped() {
        return this.getValidEquippedStack() != null;
    }

    @Override
    public ItemStack getItemStack() {
//        final ItemStack itemStack = new ItemStack(this.getItem());
//        final Map<String, EntityAttributeModifier> attributeModifiers = this.getModifiers();
//        final Map<Enchantment, Integer> enchantments = this.getEnchantments();
//
//        for (final String name : attributeModifiers.keySet()) {
//            itemStack.addAttributeModifier(name, attributeModifiers.get(name), MAINHAND);
//        }
//
//        for (final Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
//            final Integer level = entry.getValue();
//
//            if (level > 0) {
//                itemStack.addEnchantment(entry.getKey(), level);
//            }
//        }

        return itemStack;
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

    @Override
    public CompoundTag toClientTag() {
        final CompoundTag tag = new CompoundTag();

        tag.putInt("tab", this.currentTab);

        return tag;
    }

    @Override
    public void tick() {}
}
