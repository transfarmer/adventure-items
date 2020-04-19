package transfarmer.soulboundarmory.capability.soulbound.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.BaseEnchantable;
import transfarmer.soulboundarmory.capability.soulbound.ISkillable;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.item.ItemSoulboundWeapon;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.ISkill;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.EntityUtil;
import transfarmer.soulboundarmory.util.NBTUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.init.Enchantments.SHARPNESS;
import static net.minecraft.init.Enchantments.UNBREAKING;
import static net.minecraft.init.Enchantments.VANISHING_CURSE;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_DAGGER;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_GREATSWORD;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.CapabilityType.WEAPON;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.GREATSWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Skill.LEAPING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Skill.RETURN;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Skill.SHADOW_CLONE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Skill.SNEAK_RETURN;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Skill.SUMMON_LIGHTNING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Skill.THROWING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.CRITICAL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILLS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public class Weapon extends BaseEnchantable implements IWeapon, ISkillable {
    private final Set<UUID> cannotFreeze;
    private double leapForce;
    private int attackCooldown;
    private int leapDuration;
    private int lightningCooldown;

    public Weapon() {
        super(WEAPON, new IItem[]{DAGGER, SWORD, GREATSWORD},
                new ICategory[]{DATUM, ATTRIBUTE},
                new IStatistic[][]{
                        {XP, LEVEL, SKILLS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS},
                        {ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL, KNOCKBACK_ATTRIBUTE, EFFICIENCY_ATTRIBUTE, REACH_DISTANCE}
                }, new double[][][]{
                        {{0, 0, 0, 0, 0, 0, 0}, {2, 1, 0, 0, 0, 2}},
                        {{0, 0, 0, 0, 0, 0, 0}, {1.6, 2, 0, 0, 0, 3}},
                        {{0, 0, 0, 0, 0, 0, 0}, {0.8, 3, 0, 0, 0, 6}}
                }, new Item[]{SOULBOUND_DAGGER, SOULBOUND_SWORD, SOULBOUND_GREATSWORD},
                (final Enchantment enchantment) -> {
                    final String name = enchantment.getName().toLowerCase();

                    return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                            && !name.contains("soulbound") && !name.contains("holding") && !name.contains("mending");
                }
        );

        this.currentTab = 1;
        this.boundSlot = -1;
        this.attackCooldown = 0;
        this.lightningCooldown = 60;
        this.cannotFreeze = new HashSet<>();
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
            final double attackDamage = this.getAttribute(this.item, ATTACK_DAMAGE);
            final int level = this.getEnchantment(this.item, SHARPNESS);

            return level > 0
                    ? attackDamage + 1 + (level - 1) / 2F
                    : attackDamage;
        }

        return this.getAttribute(item, statistic);
    }


    @Override
    public void addAttribute(final IItem type, final IStatistic attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(type, ATTRIBUTE_POINTS, -sign);
            this.addDatum(type, SPENT_ATTRIBUTE_POINTS, sign);

            final double change = sign * this.getIncrease(type, attribute);

            if ((attribute.equals(CRITICAL) && this.getAttribute(type, CRITICAL) + change >= 100)) {
                this.setAttribute(type, attribute, 100);

                return;
            }

            final Statistic statistic = this.statistics.get(type, attribute);

            if (this.getAttribute(type, attribute) + change <= statistic.min()) {
                this.setAttribute(type, attribute, statistic.min());

                return;
            }

            this.statistics.add(type, attribute, change);
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

        return 0;
    }

    @Override
    public ISkill[] getSkills(final IItem type) {
        if (type == DAGGER) {
            return new ISkill[]{THROWING, SHADOW_CLONE, RETURN, SNEAK_RETURN};
        }

        if (type == GREATSWORD) {
            return new ISkill[]{LEAPING};
        }

        if (type == SWORD) {
            return new ISkill[]{SUMMON_LIGHTNING};
        }

        return new ISkill[0];
    }

    @Override
    public Map<String, AttributeModifier> getAttributeModifiers(final IItem type) {
        return CollectionUtil.hashMap(super.getAttributeModifiers(type),
                new String[]{
                        SharedMonsterAttributes.ATTACK_SPEED.getName(),
                        SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                },
                new AttributeModifier(SoulItemHelper.ATTACK_SPEED_UUID, "generic.attackSpeed", this.getAttribute(type, ATTACK_SPEED), ADD),
                new AttributeModifier(SoulItemHelper.ATTACK_DAMAGE_UUID, "generic.attackDamage", this.getAttributeRelative(type, ATTACK_DAMAGE), ADD)
        );
    }

    @SideOnly(CLIENT)
    @Override
    public List<String> getTooltip(final IItem type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(7);

        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, FORMAT.format(this.getAttribute(type, ATTACK_SPEED)), Mappings.ATTACK_SPEED_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, FORMAT.format(this.getAttributeTotal(type, ATTACK_DAMAGE)), Mappings.ATTACK_DAMAGE_NAME));

        tooltip.add("");
        tooltip.add("");

        if (this.getAttribute(type, CRITICAL) > 0) {
            tooltip.add(String.format(" %s%s%% %s", Mappings.CRITICAL_FORMAT, FORMAT.format(this.getAttribute(type, CRITICAL) * 100), Mappings.CRITICAL_NAME));
        }
        if (this.getAttribute(type, KNOCKBACK_ATTRIBUTE) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, FORMAT.format(this.getAttribute(type, KNOCKBACK_ATTRIBUTE)), Mappings.KNOCKBACK_ATTRIBUTE_NAME));
        }
        if (this.getAttribute(type, EFFICIENCY_ATTRIBUTE) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.WEAPON_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(type, EFFICIENCY_ATTRIBUTE)), Mappings.EFFICIENCY_NAME));
        }

        return tooltip;
    }

    @Override
    public List<Item> getConsumableItems() {
        return Collections.singletonList(Items.WOODEN_SWORD);
    }

    @Override
    @SideOnly(CLIENT)
    public void onKeyPress() {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final EntityPlayer player = this.getPlayer();
        Item item = player.getHeldItemMainhand().getItem();

        if (item instanceof ItemSoulboundWeapon) {
            minecraft.displayGuiScreen(new SoulWeaponMenu());
        } else if (item == Items.WOODEN_SWORD) {
            minecraft.displayGuiScreen(new SoulWeaponMenu(0));
        } else if ((item = player.getHeldItemOffhand().getItem()) instanceof ItemSoulboundWeapon) {
            minecraft.displayGuiScreen(new SoulWeaponMenu());
        } else if (item == Items.WOODEN_SWORD) {
            minecraft.displayGuiScreen(new SoulWeaponMenu(0));
        }
    }

    @Override
    @SideOnly(CLIENT)
    public void refresh() {
        final Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.currentScreen instanceof SoulWeaponMenu) {
            minecraft.displayGuiScreen(new SoulWeaponMenu());
        }
    }

    @Override
    public int getLevelXP(final IItem type, final int level) {
        return this.canLevelUp(type)
                ? MainConfig.instance().getInitialWeaponXP() + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    @Override
    public int onLevel(final IItem item, final int sign) {
        final int level = super.onLevel(item, sign);

        if (level % MainConfig.instance().getLevelsPerSkill() == 0 && this.getDatum(item, SKILLS) < this.getSkills(item).length) {
            this.addDatum(item, SKILLS, sign);
        }

        return level;
    }

    @Override
    public void setAttackCooldown(final int ticks) {
        this.attackCooldown = ticks;
    }

    @Override
    public void resetCooldown(final IItem type) {
        this.attackCooldown = this.getCooldown(type);
    }

    @Override
    public void decrementCooldown() {
        this.attackCooldown--;
    }

    @Override
    public int getCooldown() {
        return this.attackCooldown;
    }

    @Override
    public int getCooldown(final IItem type) {
        return (int) Math.round(20 / this.getAttribute(type, ATTACK_SPEED));
    }

    @Override
    public double getAttackRatio(final IItem type) {
        return 1 - (double) this.getCooldown() / this.getCooldown(type);
    }

    @Override
    public int getLightningCooldown() {
        return lightningCooldown;
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
    public void decrementLightningCooldown() {
        this.lightningCooldown--;
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

        this.cannotFreeze.clear();
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
    public void freeze(final Entity entity, final int ticks, final double damage) {
        final IFrozen capability = FrozenProvider.get(entity);
        final UUID id = entity.getUniqueID();

        if (!this.cannotFreeze.contains(id) && !entity.isDead && capability != null) {
            capability.freeze(this.getPlayer(), ticks, (float) damage);

            this.cannotFreeze.add(id);
        }
    }

    @Override
    public Class<? extends ISoulboundItem> getBaseItemClass() {
        return ItemSoulboundWeapon.class;
    }

    @Override
    public void onTick() {
        super.onTick();

        if (this.getCooldown() > 0) {
            this.decrementCooldown();
        }

        if (this.getItemType() != null && this.getLightningCooldown() > 0) {
            this.decrementLightningCooldown();
        }

        if (this.getLeapDuration() > 0) {
            if (--this.leapDuration == 0) {
                this.resetLeapForce();
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = super.serializeNBT();
        final NBTTagCompound cannotFreeze = new NBTTagCompound();

        for (final UUID id : this.cannotFreeze) {
            final Entity entity = EntityUtil.getEntity(id);

            if (entity != null && !entity.isDead) {
                cannotFreeze.setUniqueId(id.toString(), id);
            }
        }

        tag.setInteger("attackCooldown", this.getCooldown());
        tag.setInteger("leapDuration", this.getLeapDuration());
        tag.setDouble("leapForce", this.getLeapForce());
        tag.setInteger("lightningCooldown", this.getLightningCooldown());
        tag.setTag("cannotFreeze.set", cannotFreeze);

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        super.deserializeNBT(tag);

        NBTUtil.ifHasKeyTag(tag, "cannotFreeze.set", (final NBTTagCompound cannotFreeze) -> {
            for (final String key : cannotFreeze.getKeySet()) {
                this.cannotFreeze.add(cannotFreeze.getUniqueId(key));
            }
        });

        this.setAttackCooldown(tag.getInteger("attackCooldown"));
        this.setLeapDuration(tag.getInteger("leapDuration"));
        this.setLeapForce(tag.getDouble("leapForce"));
        this.setLightningCooldown(tag.getInteger("lightningCooldown"));
    }
}
