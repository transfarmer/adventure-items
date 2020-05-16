package transfarmer.soulboundarmory.component.soulbound.item;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import nerdhub.cardinal.components.api.component.Component;
import nerdhub.cardinal.components.api.util.ItemComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.farmerlib.item.ItemModifiers;
import transfarmer.farmerlib.util.IndexedMap;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.network.Packets;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.statistics.Category;
import transfarmer.soulboundarmory.statistics.EnchantmentStorage;
import transfarmer.soulboundarmory.statistics.IItem;
import transfarmer.soulboundarmory.statistics.SkillStorage;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.StatisticType;
import transfarmer.soulboundarmory.statistics.Statistics;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static transfarmer.soulboundarmory.statistics.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.Category.ENCHANTMENT;
import static transfarmer.soulboundarmory.statistics.Category.SKILL;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static transfarmer.soulboundarmory.statistics.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.REACH;
import static transfarmer.soulboundarmory.statistics.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.XP;

public abstract class SoulboundItemComponent<C extends SoulboundItemComponent<?>> implements ISoulboundItemComponent<C> {
    protected final ItemStack itemStack;

    protected EnchantmentStorage enchantments;
    protected SkillStorage skillStorage;
    protected Statistics statistics;
    protected boolean unlocked;

    public SoulboundItemComponent(final ItemStack itemStack) {
        this.itemStack = itemStack;
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
    public Statistic getStatistic(final IItem item, final StatisticType statistic) {
        return this.statistics.get(item, statistic);
    }

    @Override
    public Statistic getStatistic(final IItem item, final Category category, final StatisticType statistic) {
        return this.statistics.get(item, category, statistic);
    }

    @Override
    public int getDatum(final StatisticType statistic) {
        return this.getDatum(this.item, statistic);
    }

    @Override
    public int getDatum(final IItem type, final StatisticType datum) {
        return this.statistics.get(type, datum).intValue();
    }

    @Override
    public void setDatum(final IItem type, final StatisticType datum, final int value) {
        this.statistics.set(type, datum, value);
    }

    @Override
    public boolean addDatum(final StatisticType statistic, final int amount) {
        return this.addDatum(this.item, statistic, amount);
    }

    @Override
    public boolean addDatum(final IItem item, final StatisticType datum, final int amount) {
        if (datum == XP) {
            final int xp = this.statistics.add(item, XP, amount).intValue();

            if (xp >= this.getNextLevelXP(item) && this.canLevelUp(item)) {
                final int nextLevelXP = this.getNextLevelXP(item);

                this.addDatum(item, LEVEL, 1);
                this.addDatum(item, XP, -nextLevelXP);

                return true;
            } else if (xp < 0) {
                final int currentLevelXP = this.getLevelXP(item, this.getDatum(item, LEVEL) - 1);

                this.addDatum(item, LEVEL, -1);
                this.addDatum(item, XP, currentLevelXP);

                return false;
            }
        } else if (datum == LEVEL) {
            final int sign = (int) Math.signum(amount);

            for (int i = 0; i < Math.abs(amount); i++) {
                this.onLevel(item, sign);
            }
        } else {
            this.statistics.add(item, datum, amount);
        }

        return false;
    }

    @Override
    public double getAttribute(final StatisticType statistic) {
        return this.statistics.get(statistic).doubleValue();
    }

    @Override
    public void setAttribute(final StatisticType statistic, final double value) {
        this.statistics.set(statistic, value);
    }

    @Override
    public int getLevelXP(final int level) {
        return this.canLevelUp()
                ? MainConfig.instance().getInitialWeaponXP() + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    @Override
    public List<Skill> getSkills() {
        final List<Skill> skills = new ArrayList<>(this.skillStorage.values());

        skills.sort(Comparator.comparingInt(Skill::getTier));

        return skills;
    }

    @Override
    public Skill getSkill(final Skill skill) {
        return this.getSkill(skill.getIdentifier());
    }

    @Override
    public Skill getSkill(final Identifier identifier) {
        return (Skill) this.getSkill(identifier);
    }

    @Override
    public Skill getSkill(final String skill) {
        return this.getSkill(this.item, skill);
    }

    @Override
    public Skill getSkill(final IItem item, final String skill) {
        return this.skills.get(item, skill);
    }

    @Override
    public boolean hasSkill(final IItem item, final String skill) {
        return this.skills.contains(item, skill);
    }

    @Override
    public boolean hasSkill(final IItem item, final Skill skill) {
        return this.skills.contains(item, skill);
    }

    @Override
    public boolean hasSkill(final IItem item, final Skill skill, final int level) {
        return this.skills.contains(item, skill, level);
    }

    @SuppressWarnings("VariableUseSideOnly")
    @Override
    public void upgradeSkill(final IItem item, final Skill skill) {
        if (this.isClient) {
            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SKILL, new ExtendedPacketBuffer(this, item).writeString(skill.toString()));
        } else {
            final int points = this.getDatum(SKILL_POINTS);
            final int cost = skill.getCost();

            if (skill.canBeLearned(points)) {
                skill.learn();

                this.addDatum(SKILL_POINTS, -cost);
            } else if (skill instanceof Skill) {
                final Skill levelable = (Skill) skill;

                if (levelable.canBeUpgraded(points)) {
                    levelable.upgrade();

                    this.addDatum(SKILL_POINTS, -cost);
                }
            }
        }
    }

    @Override
    public double getAttributeRelative(final StatisticType statistic) {
        if (statistic == ATTACK_SPEED) {
            return this.getAttribute(ATTACK_SPEED) - 4;
        }

        if (statistic == ATTACK_DAMAGE) {
            return this.getAttribute(ATTACK_DAMAGE) - 1;
        }

        if (statistic == REACH) {
            return this.getAttribute(REACH) - 3;
        }

        return this.getStatistic(statistic).doubleValue();
    }

    @Override
    public double getAttributeTotal(final StatisticType statistic) {
        if (statistic == ATTACK_DAMAGE) {
            double attackDamage = this.getAttribute(ATTACK_DAMAGE);

            for (final Enchantment enchantment : this.enchantments) {
                attackDamage += enchantment.getAttackDamage(this.getEnchantment(enchantment), EntityGroup.DEFAULT);
            }

            return attackDamage;
        }

        return this.getAttribute(statistic);
    }


    @Override
    public void addAttribute(final StatisticType attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (sign > 0 && this.getDatum(ATTRIBUTE_POINTS) > 0 || sign < 0 && this.getDatum(SPENT_ATTRIBUTE_POINTS) > 0) {
                this.addDatum(ATTRIBUTE_POINTS, -sign);
                this.addDatum(SPENT_ATTRIBUTE_POINTS, sign);

                final double change = sign * this.getIncrease(attribute);

                if ((attribute.equals(CRITICAL_STRIKE_PROBABILITY) && this.getAttribute(CRITICAL_STRIKE_PROBABILITY) + change >= 1)) {
                    this.setAttribute(attribute, 1);

                    return;
                }

                final Statistic statistic = this.statistics.get(attribute);

                if (this.getAttribute(attribute) + change <= statistic.min()) {
                    this.setAttribute(attribute, statistic.min());

                    return;
                }

                this.statistics.add(attribute, change);
            }
        }
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
        return !this.unlocked && this.canConsume(this.getEquippedItemStack().getItem());
    }

    @Override
    public boolean canConsume(final Item item) {
        return this.getConsumableItem() == item;
    }

    @Override
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

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {
        this.statistics.fromTag(tag.getCompound("statistics"));
        this.enchantments.fromTag(tag.getCompound("enchantments"));
        this.skillStorage.fromTag(tag.getCompound("skills"));
        this.unlocked = tag.getBoolean("unlocked");
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        tag.put("statistics", this.statistics.toTag(new CompoundTag()));
        tag.put("enchantments", this.enchantments.toTag(new CompoundTag()));
        tag.put("skills", this.skillStorage.toTag());
        tag.putBoolean("unlocked", this.unlocked);

        return tag;
    }
}
