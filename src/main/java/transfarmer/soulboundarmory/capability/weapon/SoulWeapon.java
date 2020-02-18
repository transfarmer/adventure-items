package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.i18n.Mappings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.*;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponAttribute.*;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.*;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment.SHARPNESS;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.GREATSWORD;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.SWORD;

public class SoulWeapon implements ISoulWeapon {
    private SoulWeaponType currentType;
    private int currentTab = 0;
    private int attackCooldwn = 0;
    private int lightningCooldown = 60;
    private int boundSlot = -1;
    private int[][] data = new int[3][DATA];
    private float[][] attributes = new float[3][ATTRIBUTES];
    private int[][] enchantments = new int[3][ENCHANTMENTS];

    @Override
    public void set(final int[][] data, final float[][] attributes, final int[][] enchantments) {
        this.data = data;
        this.attributes = attributes;
        this.enchantments = enchantments;
    }

    @Override
    public void setData(final int[][] data) {
        this.data = data;
    }

    @Override
    public void setAttributes(final float[][] attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setAttributes(final float[] attributes, final SoulWeaponType type) {
        this.attributes[type.index] = attributes;
    }

    @Override
    public void setEnchantments(final int[][] enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public void setEnchantments(final int[] enchantments, final SoulWeaponType type) {
        this.enchantments[type.index] = enchantments;
    }

    @Override
    public int[][] getData() {
        return this.data;
    }

    @Override
    public float[][] getAttributes() {
        return this.attributes;
    }

    @Override
    public int[][] getEnchantments() {
        return this.enchantments;
    }

    @Override
    public float getAttribute(final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        return this.attributes[type.index][attribute.index];
    }

    @Override
    public void setAttribute(final float value, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        this.attributes[type.index][attribute.index] = value;
    }

    @Override
    public void addAttribute(final int amount, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(-sign, ATTRIBUTE_POINTS, type);
            this.addDatum(sign, SPENT_ATTRIBUTE_POINTS, type);

            if ((attribute == CRITICAL && this.getAttribute(CRITICAL, type) + sign * CRITICAL.getIncrease(type) >= 100)) {
                this.setAttribute(100, attribute, type);
                return;
            } else if (this.attributes[type.index][attribute.index] + sign * attribute.getIncrease(type) > 0.0001) {
                this.attributes[type.index][attribute.index] += sign * attribute.getIncrease(type);
            } else {
                this.attributes[type.index][attribute.index] = 0;
                return;
            }
        }
    }

    @Override
    public float getAttackSpeed(final SoulWeaponType type) {
        return this.attributes[type.index][ATTACK_SPEED.index] + type.item.getAttackSpeed();
    }

    @Override
    public float getEffectiveAttackSpeed(final SoulWeaponType type) {
        return this.getAttackSpeed(type) + 4;
    }

    @Override
    public float getAttackDamage(final SoulWeaponType type) {
        return this.attributes[type.index][ATTACK_DAMAGE.index] + type.item.getAttackDamage();
    }

    @Override
    public float getEffectiveAttackDamage(final SoulWeaponType type) {
        float attackDamage = this.getAttackDamage(type);

        if (this.getEnchantment(SHARPNESS, type) > 0) {
            attackDamage += 1 + (this.getEnchantment(SHARPNESS, type) - 1) / 2F;
        }

        return attackDamage;
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return getItemStack(SoulWeaponType.getType(itemStack));
    }

    @Override
    public ItemStack getItemStack(final SoulWeaponType type) {
        final ItemStack itemStack = new ItemStack(type.item);
        final AttributeModifier[] attributeModifiers = getAttributeModifiers(type);
        final SortedMap<SoulWeaponEnchantment, Integer> enchantments = this.getEnchantments(type);

        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), attributeModifiers[0], MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), attributeModifiers[1], MAINHAND);

        if (type == GREATSWORD) {
            itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", 3, ADD), MAINHAND);
        } else if (type == SWORD) {
            itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", 1.5, ADD), MAINHAND);
        }

        enchantments.forEach((final SoulWeaponEnchantment enchantment, final Integer level) -> itemStack.addEnchantment(enchantment.enchantment, level));

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(final SoulWeaponType type) {
        return new AttributeModifier[]{
            new AttributeModifier(ATTACK_SPEED_UUID, "generic.attackSpeed", this.getAttackSpeed(type), ADD),
            new AttributeModifier(ATTACK_DAMAGE_UUID, "generic.attackDamage", this.getAttackDamage(type), ADD)
        };
    }

    @Override
    public SortedMap<SoulWeaponEnchantment, Integer> getEnchantments(final SoulWeaponType type) {
        final SortedMap<SoulWeaponEnchantment, Integer> enchantments = new TreeMap<>();

        for (final SoulWeaponEnchantment enchantment : SoulWeaponEnchantment.getEnchantments()) {
            final int level = this.getEnchantment(enchantment, type);

            if (level > 0) {
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }

    @SideOnly(CLIENT)
    @Override
    public String[] getTooltip(final SoulWeaponType type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(7);
        final Map<SoulWeaponEnchantment, Integer> enchantments = this.getEnchantments(type);

        float attackDamage = this.getAttackDamage(type) + 1;

        if (enchantments.containsKey(SHARPNESS)) {
            attackDamage += 1 + (enchantments.get(SHARPNESS) - 1) / 2F;
        }

        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, FORMAT.format(this.getAttackSpeed(type) + 4), Mappings.ATTACK_SPEED_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, FORMAT.format(attackDamage), Mappings.ATTACK_DAMAGE_NAME));

        tooltip.add("");
        tooltip.add("");

        if (this.getAttribute(CRITICAL, type) > 0) {
            tooltip.add(String.format(" %s%s%% %s", Mappings.CRITICAL_FORMAT, FORMAT.format(this.getAttribute(CRITICAL, type)), Mappings.CRITICAL_NAME));
        } if (this.getAttribute(KNOCKBACK_ATTRIBUTE, type) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, FORMAT.format(this.getAttribute(KNOCKBACK_ATTRIBUTE, type)), Mappings.KNOCKBACK_ATTRIBUTE_NAME));
        } if (this.getAttribute(EFFICIENCY, type) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.WEAPON_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(EFFICIENCY, type)), Mappings.EFFICIENCY_NAME));
        }

        return tooltip.toArray(new String[0]);
    }

    @Override
    public int getNextLevelXP(final SoulWeaponType type) {
        return this.getDatum(LEVEL, type) >= Configuration.maxLevel ?
            1 : Configuration.initialWeaponXP + 4 * (int) Math.round(Math.pow(this.getDatum(LEVEL, type), 1.5));
    }

    @Override
    public int getDatum(SoulWeaponDatum datum, SoulWeaponType type) {
        return this.data[type.index][datum.index];
    }

    @Override
    public void setDatum(final int value, final SoulWeaponDatum datum, final SoulWeaponType type) {
        this.data[type.index][datum.index] = value;
    }

    @Override
    public boolean addDatum(int amount, SoulWeaponDatum datum, SoulWeaponType type) {
        switch (datum) {
            case XP:
                this.data[type.index][XP.index] += amount;

                if (this.getDatum(XP, type) >= this.getNextLevelXP(type) && this.getDatum(LEVEL, type) < Configuration.maxLevel) {
                    this.addDatum(-this.getNextLevelXP(type), XP, type);
                    this.addDatum(1, LEVEL, type);
                    return true;
                }

                break;
            case LEVEL:
                final int level = ++this.data[type.index][LEVEL.index];
                if (level % (Configuration.levelsPerEnchantment) == 0) {
                    this.addDatum(1, ENCHANTMENT_POINTS, type);
                }

                if (level % (Configuration.levelsPerSkill) == 0 && this.getDatum(SKILLS, type) < SoulWeaponHelper.getMaxSkills(type)) {
                    this.addDatum(1, SKILLS, type);
                }

                this.addDatum(1, ATTRIBUTE_POINTS, type);
                break;
            default:
                this.data[type.index][datum.index] += amount;
        }

        return false;
    }

    @Override
    public int getEnchantment(final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        return this.enchantments[type.index][enchantment.index];
    }

    @Override
    public void addEnchantment(final int amount, final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (this.getEnchantment(enchantment, type) + sign >= 0) {
                this.addDatum(-sign, ENCHANTMENT_POINTS, type);
                this.addDatum(sign, SPENT_ENCHANTMENT_POINTS, type);

                this.enchantments[type.index][enchantment.index] += sign;
            } else {
                return;
            }
        }
    }

    @Override
    public void setCurrentTab(final int tab) {
        this.currentTab = tab;
    }

    @Override
    public int getCurrentTab() {
        return this.currentTab;
    }

    @Override
    public SoulWeaponType getCurrentType() {
        return this.currentType;
    }

    @Override
    public void setCurrentType(final SoulWeaponType type) {
        this.currentType = type;
    }

    @Override
    public void setCurrentType(final int index) {
        this.currentType = SoulWeaponType.getType(index);
    }

    @Override
    public void setAttackCooldwn(final int ticks) {
        this.attackCooldwn = ticks;
    }

    @Override
    public void resetCooldown(final SoulWeaponType type) {
        this.attackCooldwn = this.getCooldown(type);
    }

    @Override
    public void addCooldown(final int ticks) {
        this.attackCooldwn += ticks;
    }

    @Override
    public int getAttackCooldwn() {
        return this.attackCooldwn;
    }

    @Override
    public int getCooldown(final SoulWeaponType type) {
        return Math.round(20 / (4 + this.getAttackSpeed(type)));
    }

    @Override
    public float getAttackRatio(final SoulWeaponType type) {
        return 1 - (float) this.getAttackCooldwn() / this.getCooldown(type);
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public void setBoundSlot(final int boundSlot) {
        this.boundSlot = boundSlot;
    }

    @Override
    public void unbindSlot() {
        this.boundSlot = -1;
    }

    @Override
    public int getLightningCooldown() {
        return lightningCooldown;
    }

    @Override
    public void resetLightningCooldown() {
        this.lightningCooldown = Math.round(96 / this.getEffectiveAttackSpeed(this.currentType));
    }

    @Override
    public void decrementLightningCooldown() {
        this.lightningCooldown--;
    }
}
