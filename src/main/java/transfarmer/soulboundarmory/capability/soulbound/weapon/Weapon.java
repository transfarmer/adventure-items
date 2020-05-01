package transfarmer.soulboundarmory.capability.soulbound.weapon;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.entity.EntityDatumProvider;
import transfarmer.soulboundarmory.capability.entity.IEntityData;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundBase;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabEnchantments;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSkills;
import transfarmer.soulboundarmory.client.gui.screen.weapon.GuiTabWeaponAttributes;
import transfarmer.soulboundarmory.client.gui.screen.weapon.GuiTabWeaponSelection;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.item.SoulboundWeapon;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.common.SkillLeeching;
import transfarmer.soulboundarmory.skill.dagger.SkillReturn;
import transfarmer.soulboundarmory.skill.dagger.SkillShadowClone;
import transfarmer.soulboundarmory.skill.dagger.SkillSneakReturn;
import transfarmer.soulboundarmory.skill.dagger.SkillThrowing;
import transfarmer.soulboundarmory.skill.greatsword.SkillFreezing;
import transfarmer.soulboundarmory.skill.greatsword.SkillLeaping;
import transfarmer.soulboundarmory.skill.staff.SkillEndermanacle;
import transfarmer.soulboundarmory.skill.staff.SkillFireball;
import transfarmer.soulboundarmory.skill.staff.SkillHealing;
import transfarmer.soulboundarmory.skill.staff.SkillPenetration;
import transfarmer.soulboundarmory.skill.staff.SkillVulnerability;
import transfarmer.soulboundarmory.skill.sword.SkillSummonLightning;
import transfarmer.soulboundarmory.statistics.Skills;
import transfarmer.soulboundarmory.statistics.SoulboundEnchantments;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.Statistics;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.NBTUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.init.Enchantments.SHARPNESS;
import static net.minecraft.init.Enchantments.UNBREAKING;
import static net.minecraft.init.Enchantments.VANISHING_CURSE;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;
import static transfarmer.soulboundarmory.enchantment.Enchantments.IMPACT;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_DAGGER;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_GREATSWORD;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_STAFF;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.CapabilityType.WEAPON;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.GREATSWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.STAFF;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.CRITICAL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public class Weapon extends SoulboundBase implements IWeaponCapability {
    private NBTTagCompound cannotFreeze;
    private double leapForce;
    private int attackCooldown;
    private int fireballCooldown;
    private int leapDuration;
    private int lightningCooldown;
    private int spell;

    public Weapon() {
        super(WEAPON, new IItem[]{DAGGER, SWORD, GREATSWORD, STAFF}, new Item[]{SOULBOUND_DAGGER, SOULBOUND_SWORD, SOULBOUND_GREATSWORD, SOULBOUND_STAFF});

        final List<IItem> itemTypes = this.itemTypes.keyList();

        this.cannotFreeze = new NBTTagCompound();
        this.statistics = new Statistics(itemTypes,
                new ICategory[]{DATUM, ATTRIBUTE},
                new IStatistic[][]{
                        {XP, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS},
                        {ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL, KNOCKBACK_ATTRIBUTE, EFFICIENCY_ATTRIBUTE, REACH_DISTANCE}
                }, new double[][][]{
                {{0, 0, 0, 0, 0, 0, 0}, {2, 1, 0, 0, 0, 2}},
                {{0, 0, 0, 0, 0, 0, 0}, {1.6, 2, 0, 0, 0, 3}},
                {{0, 0, 0, 0, 0, 0, 0}, {0.8, 3, 0, 0, 0, 6}},
                {{0, 0, 0, 0, 0, 0, 0}, {0.48, 6, 0, 0, 0, 3}}
        });
        this.enchantments = new SoulboundEnchantments(itemTypes, this.items, (final Enchantment enchantment, final IItem item) -> {
            final String name = enchantment.getName().toLowerCase();

            return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment) &&
                    (enchantment == IMPACT || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skills = new Skills(itemTypes,
                new Skill[]{new SkillLeeching(), new SkillThrowing(), new SkillShadowClone(), new SkillReturn(), new SkillSneakReturn()},
                new Skill[]{new SkillLeeching(), new SkillSummonLightning()},
                new Skill[]{new SkillLeeching(), new SkillLeaping(), new SkillFreezing()},
                new Skill[]{new SkillHealing(), new SkillFireball(), new SkillVulnerability(), new SkillPenetration(), new SkillEndermanacle()}
        );
    }

    @Override
    public double getAttributeRelative(final IItem item, final IStatistic statistic) {
        if (statistic == ATTACK_SPEED) {
            return this.getAttribute(item, ATTACK_SPEED) - 4;
        }

        if (statistic == ATTACK_DAMAGE) {
            return this.getAttribute(item, ATTACK_DAMAGE) - 1;
        }

        if (statistic == REACH_DISTANCE) {
            return this.getAttribute(item, REACH_DISTANCE) - 3;
        }

        return this.getStatistic(item, statistic).doubleValue();
    }

    @Override
    public double getAttributeTotal(final IItem item, final IStatistic statistic) {
        if (statistic == ATTACK_DAMAGE) {
            final double attackDamage = this.getAttribute(item, ATTACK_DAMAGE);
            final int level = item == STAFF ? this.getEnchantment(item, IMPACT) : this.getEnchantment(item, SHARPNESS);

            return level > 0
                    ? attackDamage + 1 + (level - 1) / 2F
                    : attackDamage;
        }

        return this.getAttribute(item, statistic);
    }


    @Override
    public void addAttribute(final IItem item, final IStatistic attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (sign > 0 && this.getDatum(item, ATTRIBUTE_POINTS) > 0 || sign < 0 && this.getDatum(item, SPENT_ATTRIBUTE_POINTS) > 0) {
                this.addDatum(item, ATTRIBUTE_POINTS, -sign);
                this.addDatum(item, SPENT_ATTRIBUTE_POINTS, sign);

                final double change = sign * this.getIncrease(item, attribute);

                if ((attribute.equals(CRITICAL) && this.getAttribute(item, CRITICAL) + change >= 1)) {
                    this.setAttribute(item, attribute, 1);

                    return;
                }

                final Statistic statistic = this.statistics.get(item, attribute);

                if (this.getAttribute(item, attribute) + change <= statistic.min()) {
                    this.setAttribute(item, attribute, statistic.min());

                    return;
                }

                this.statistics.add(item, attribute, change);
            }
        }
    }

    @Override
    public double getIncrease(final IItem item, final IStatistic statistic) {
        if (item == DAGGER) {
            return statistic == ATTACK_SPEED
                    ? 0.04
                    : statistic == ATTACK_DAMAGE
                    ? 0.075
                    : statistic == CRITICAL
                    ? 0.02
                    : statistic == KNOCKBACK_ATTRIBUTE
                    ? 0.2
                    : statistic == EFFICIENCY_ATTRIBUTE
                    ? 0.15
                    : 0;
        }

        if (item == SWORD) {
            return statistic == ATTACK_SPEED
                    ? 0.03
                    : statistic == ATTACK_DAMAGE
                    ? 0.125
                    : statistic == CRITICAL
                    ? 0.015
                    : statistic == KNOCKBACK_ATTRIBUTE
                    ? 0.35
                    : statistic == EFFICIENCY_ATTRIBUTE
                    ? 0.2
                    : 0;
        }

        if (item == GREATSWORD) {
            return statistic == ATTACK_SPEED
                    ? 0.01
                    : statistic == ATTACK_DAMAGE
                    ? 0.15
                    : statistic == CRITICAL
                    ? 0.01
                    : statistic == KNOCKBACK_ATTRIBUTE
                    ? 0.6
                    : statistic == EFFICIENCY_ATTRIBUTE
                    ? 0.3
                    : 0;
        }

        if (item == STAFF) {
            return statistic == ATTACK_SPEED
                    ? 0.08
                    : statistic == ATTACK_DAMAGE
                    ? 0.2
                    : statistic == CRITICAL
                    ? 0.04
                    : statistic == KNOCKBACK_ATTRIBUTE
                    ? 0.1
                    : statistic == EFFICIENCY_ATTRIBUTE
                    ? 0.1
                    : 0;
        }

        return 0;
    }

    @Override
    public Map<String, AttributeModifier> getAttributeModifiers(final IItem type) {
        return CollectionUtil.hashMap(super.getAttributeModifiers(type),
                new String[]{
                        SharedMonsterAttributes.ATTACK_SPEED.getName(),
                        SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                },
                new AttributeModifier(SoulboundItemUtil.ATTACK_SPEED_UUID, "generic.attackSpeed", this.getAttributeRelative(type, ATTACK_SPEED), ADD),
                new AttributeModifier(SoulboundItemUtil.ATTACK_DAMAGE_UUID, "generic.attackDamage", this.getAttributeRelative(type, ATTACK_DAMAGE), ADD)
        );
    }

    @Override
    @SideOnly(CLIENT)
    public List<String> getTooltip(final IItem item) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(7);

        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, FORMAT.format(this.getAttribute(item, ATTACK_SPEED)), Mappings.ATTACK_SPEED_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, FORMAT.format(this.getAttributeTotal(item, ATTACK_DAMAGE)), Mappings.ATTACK_DAMAGE_NAME));

        tooltip.add("");
        tooltip.add("");

        if (this.getAttribute(item, CRITICAL) > 0) {
            tooltip.add(String.format(" %s%s%% %s", Mappings.CRITICAL_FORMAT, FORMAT.format(this.getAttribute(item, CRITICAL) * 100), Mappings.CRITICAL_NAME));
        }
        if (this.getAttribute(item, KNOCKBACK_ATTRIBUTE) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, FORMAT.format(this.getAttribute(item, KNOCKBACK_ATTRIBUTE)), Mappings.KNOCKBACK_ATTRIBUTE_NAME));
        }
        if (this.getAttribute(item, EFFICIENCY_ATTRIBUTE) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.WEAPON_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(item, EFFICIENCY_ATTRIBUTE)), Mappings.EFFICIENCY_NAME));
        }

        return tooltip;
    }

    @Override
    public Item getConsumableItem(final IItem item) {
        return item == STAFF ? null : Items.WOODEN_SWORD;
    }

    @Override
    public List<GuiTab> getTabs() {
        List<GuiTab> tabs = new ArrayList<>();
        tabs = CollectionUtil.arrayList(new GuiTabWeaponSelection(tabs), new GuiTabWeaponAttributes(tabs), new GuiTabEnchantments(WEAPONS, tabs), new GuiTabSkills(WEAPONS, tabs));

        return tabs;
    }

    @Override
    public int getLevelXP(final IItem type, final int level) {
        return this.canLevelUp(type)
                ? MainConfig.instance().getInitialWeaponXP() + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    @Override
    public void setAttackCooldown(final int ticks) {
        this.attackCooldown = ticks;
    }

    @Override
    public void resetCooldown(final IItem type) {
        this.attackCooldown = this.getAttackCooldown(type);
    }

    @Override
    public int getAttackCooldown() {
        return this.attackCooldown;
    }

    @Override
    public int getAttackCooldown(final IItem type) {
        return (int) Math.round(20 / this.getAttribute(type, ATTACK_SPEED));
    }

    @Override
    public double getAttackRatio(final IItem type) {
        return 1 - (double) this.getAttackCooldown() / this.getAttackCooldown(type);
    }

    @Override
    public int getLightningCooldown() {
        return this.lightningCooldown;
    }

    @Override
    public void setLightningCooldown(final int ticks) {
        this.lightningCooldown = ticks;
    }

    @Override
    public void resetLightningCooldown() {
        if (!this.getPlayer().isCreative()) {
            this.lightningCooldown = (int) Math.round(96 / this.getAttribute(this.item, ATTACK_SPEED));
        }
    }

    @Override
    public double getLeapForce() {
        return this.leapForce;
    }

    @Override
    public void setLeapForce(final double force) {
        this.resetLeapForce();
        this.leapForce = force;
    }

    @Override
    public void resetLeapForce() {
        this.leapForce = 0;
        this.leapDuration = 0;

        NBTUtil.clear(this.cannotFreeze);
    }

    @Override
    public int getLeapDuration() {
        return leapDuration;
    }

    @Override
    public void setLeapDuration(final int ticks) {
        this.leapDuration = ticks;
    }

    @Override
    public int getFireballCooldown() {
        return this.fireballCooldown;
    }

    @Override
    public void setFireballCooldown(final int ticks) {
        this.fireballCooldown = ticks;
    }

    @Override
    public void resetFireballCooldown() {
        this.fireballCooldown = (int) Math.round(20 / this.getAttribute(STAFF, ATTACK_SPEED));
    }

    @Override
    public int getSpell() {
        return this.spell;
    }

    @Override
    public void setSpell(final int spell) {
        this.spell = spell;
    }

    @Override
    public void cycleSpells(final int spells) {
        this.spell = Math.abs((this.spell + spells) % 2);

        this.sync();
    }

    @Override
    public void freeze(final Entity entity, final int ticks, final double damage) {
        final IEntityData capability = EntityDatumProvider.get(entity);
        final UUID id = entity.getUniqueID();
        final String key = id.toString();

        if (!this.cannotFreeze.hasKey(key) && !entity.isDead && capability != null) {
            capability.freeze(this.getPlayer(), ticks, (float) damage);

            this.cannotFreeze.setUniqueId(key, id);
        }
    }

    @Override
    public Class<? extends ItemSoulbound> getBaseItemClass() {
        return SoulboundWeapon.class;
    }

    @Override
    public void onTick() {
        super.onTick();

        if (!this.isRemote) {
            if (this.getAttackCooldown() > 0) {
                this.attackCooldown--;
            }

            if (this.fireballCooldown > 0) {
                this.fireballCooldown--;
            }

            if (this.getItemType() != null && this.getLightningCooldown() > 0) {
                this.lightningCooldown--;
            }

            if (this.getLeapDuration() > 0) {
                if (--this.leapDuration == 0) {
                    this.resetLeapForce();
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = super.serializeNBT();

        tag.setInteger("attackCooldown", this.getAttackCooldown());
        tag.setInteger("leapDuration", this.getLeapDuration());
        tag.setDouble("leapForce", this.getLeapForce());
        tag.setInteger("lightningCooldown", this.getLightningCooldown());
        tag.setInteger("spell", this.spell);
        tag.setTag("cannotFreeze", this.cannotFreeze);

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        super.deserializeNBT(tag);

        this.setAttackCooldown(tag.getInteger("attackCooldown"));
        this.setLeapDuration(tag.getInteger("leapDuration"));
        this.setLeapForce(tag.getDouble("leapForce"));
        this.setLightningCooldown(tag.getInteger("lightningCooldown"));
        this.setSpell(tag.getInteger("spell"));
        this.cannotFreeze = tag.getCompoundTag("cannotFreeze");
    }

    @Override
    public NBTTagCompound serializeNBTClient() {
        final NBTTagCompound tag = super.serializeNBTClient();

        tag.setInteger("spell", this.spell);

        return tag;
    }
}
