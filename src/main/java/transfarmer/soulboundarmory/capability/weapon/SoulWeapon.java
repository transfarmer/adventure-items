package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.BaseSoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponData;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulWeaponDatum.WEAPON_DATA;
import static transfarmer.soulboundarmory.statistics.SoulEnchantment.SOUL_SHARPNESS;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;

public class SoulWeapon extends BaseSoulCapability implements ISoulWeapon {
    private double charging;
    private int attackCooldown;
    private int lightningCooldown;
    private int daggerCharge;

    public SoulWeapon() {
        super(WEAPON_DATA);
        this.currentTab = 1;
        this.boundSlot = -1;
        this.attackCooldown = 0;
        this.lightningCooldown = 60;
    }

    @Override
    public void setStatistics(final int[][] data, final float[][] attributes, final int[][] enchantments) {
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
    public void setAttributes(final float[] attributes, final SoulType type) {
        this.attributes[type.getIndex()] = attributes;
    }

    @Override
    public void setEnchantments(final int[][] enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public SoulType getType(final ItemStack itemStack) {
        return SoulWeaponType.get(itemStack);
    }

    @Override
    public void setEnchantments(final int[] enchantments, final SoulType type) {
        this.enchantments[type.getIndex()] = enchantments;
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
    public SoulType getCurrentType() {
        return this.currentType;
    }

    @Override
    public float getAttribute(final SoulAttribute attribute, final SoulType type, final boolean total, final boolean effective) {
        if (total) {
            if (attribute.equals(ATTACK_SPEED)) {
                float attackSpeed = this.getAttribute(ATTACK_SPEED, type) + type.getSoulItem().getAttackSpeed();

                return effective ? attackSpeed + 4 : attackSpeed;
            } else if (attribute.equals(ATTACK_DAMAGE)) {
                float attackDamage = 1 + this.getAttribute(ATTACK_DAMAGE, type) + type.getSoulItem().getDamage();

                return effective && this.getEnchantment(SOUL_SHARPNESS, type) > 0
                        ? attackDamage + 1 + (this.getEnchantment(SOUL_SHARPNESS, type) - 1) / 2F : attackDamage;
            }
        }

        return this.attributes[type.getIndex()][attribute.getIndex()];
    }

    @Override
    public float getAttribute(final SoulAttribute attribute, final SoulType type, final boolean total) {
        return this.getAttribute(attribute, type, total, false);
    }

    @Override
    public float getAttribute(final SoulAttribute attribute, final SoulType type) {
        return this.getAttribute(attribute, type, false, false);
    }

    @Override
    public void setAttribute(final float value, final SoulAttribute attribute, final SoulType type) {
        this.attributes[type.getIndex()][attribute.getIndex()] = value;
    }

    @Override
    public void addAttribute(final int amount, final SoulAttribute attribute, final SoulType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(-sign, this.datum.attributePoints, type);
            this.addDatum(sign, this.datum.spentAttributePoints, type);

            if ((attribute.equals(CRITICAL) && this.getAttribute(CRITICAL, type) + sign * CRITICAL.getIncrease(type) >= 100)) {
                this.setAttribute(100, attribute, type);
                return;
            } else if (this.attributes[type.getIndex()][attribute.getIndex()] + sign * attribute.getIncrease(type) > 0.0001) {
                this.attributes[type.getIndex()][attribute.getIndex()] += sign * attribute.getIncrease(type);
            } else {
                this.attributes[type.getIndex()][attribute.getIndex()] = 0;
                return;
            }
        }
    }

    @Override
    public void setData(final int[] data, final SoulType type) {
        this.data[type.getIndex()] = data;
    }


    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return getItemStack(SoulWeaponType.get(itemStack));
    }

    @Override
    public ItemStack getItemStack(final SoulType type) {
        final ItemStack itemStack = new ItemStack(type.getItem());
        final AttributeModifier[] attributeModifiers = this.getAttributeModifiers(type);
        final Map<SoulEnchantment, Integer> enchantments = this.getEnchantments(type);

        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), attributeModifiers[0], MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), attributeModifiers[1], MAINHAND);
        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), attributeModifiers[2], MAINHAND);

        enchantments.forEach((final SoulEnchantment enchantment, final Integer level) -> itemStack.addEnchantment(enchantment.getEnchantment(), level));

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(final SoulType type) {
        return new AttributeModifier[]{
                new AttributeModifier(SoulItemHelper.ATTACK_SPEED_UUID, "generic.attackSpeed", this.getAttribute(ATTACK_SPEED, type, true), ADD),
                new AttributeModifier(SoulItemHelper.ATTACK_DAMAGE_UUID, "generic.attackDamage", this.getAttribute(ATTACK_DAMAGE, type, true), ADD),
                new AttributeModifier(SoulItemHelper.REACH_DISTANCE_UUID, "generic.reachDistance", this.currentType.getSoulItem().getReachDistance(), ADD),
        };
    }

    @Override
    public Map<SoulEnchantment, Integer> getEnchantments(final SoulType type) {
        final Map<SoulEnchantment, Integer> enchantments = new LinkedHashMap<>();

        for (final SoulEnchantment enchantment : SoulWeaponEnchantment.get()) {
            final int level = this.getEnchantment(enchantment, type);

            if (level > 0) {
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }

    @SideOnly(CLIENT)
    @Override
    public List<String> getTooltip(final SoulType type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(7);

        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, FORMAT.format(this.getAttribute(ATTACK_SPEED, type, true, true)), Mappings.ATTACK_SPEED_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, FORMAT.format(this.getAttribute(ATTACK_DAMAGE, type, true, true)), Mappings.ATTACK_DAMAGE_NAME));

        tooltip.add("");
        tooltip.add("");

        if (this.getAttribute(CRITICAL, type) > 0) {
            tooltip.add(String.format(" %s%s%% %s", Mappings.CRITICAL_FORMAT, FORMAT.format(this.getAttribute(CRITICAL, type)), Mappings.CRITICAL_NAME));
        }
        if (this.getAttribute(KNOCKBACK_ATTRIBUTE, type) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, FORMAT.format(this.getAttribute(KNOCKBACK_ATTRIBUTE, type)), Mappings.KNOCKBACK_ATTRIBUTE_NAME));
        }
        if (this.getAttribute(EFFICIENCY_ATTRIBUTE, type) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.WEAPON_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(EFFICIENCY_ATTRIBUTE, type)), Mappings.EFFICIENCY_NAME));
        }

        return tooltip;
    }

    @Override
    public List<Item> getConsumableItems() {
        return Arrays.asList(Items.WOODEN_SWORD);
    }

    @Override
    public int getLevelXP(final SoulType type, final int level) {
        return this.canLevelUp(type)
                ? MainConfig.instance().getInitialWeaponXP() + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    @Override
    public int getDatum(final SoulDatum datum, final SoulType type) {
        return this.data[type.getIndex()][datum.getIndex()];
    }

    @Override
    public void setDatum(final int value, final SoulDatum datum, final SoulType type) {
        this.data[type.getIndex()][datum.getIndex()] = value;
    }

    @Override
    public int getEnchantment(final SoulEnchantment enchantment, final SoulType type) {
        return this.enchantments[type.getIndex()][enchantment.getIndex()];
    }

    @Override
    public void addEnchantment(final int amount, final SoulEnchantment enchantment, final SoulType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (this.getEnchantment(enchantment, type) + sign >= 0) {
                this.addDatum(-sign, this.datum.enchantmentPoints, type);
                this.addDatum(sign, this.datum.spentEnchantmentPoints, type);

                this.enchantments[type.getIndex()][enchantment.getIndex()] += sign;
            } else {
                return;
            }
        }
    }

    @Override
    public void setCurrentType(final SoulType type) {
        this.currentType = type;
    }

    @Override
    public void setCurrentType(final int index) {
        this.currentType = SoulWeaponType.get(index);
    }

    @Override
    public void setAttackCooldown(final int ticks) {
        this.attackCooldown = ticks;
    }

    @Override
    public void resetCooldown(final SoulType type) {
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
    public int getCooldown(final SoulType type) {
        return Math.round(20 / this.getAttribute(ATTACK_SPEED, type, true, true));
    }

    @Override
    public float getAttackRatio(final SoulType type) {
        return 1 - (float) this.getCooldown() / this.getCooldown(type);
    }

    @Override
    public int getItemAmount() {
        return SoulWeaponType.getAmount();
    }

    @Override
    public int getAttributeAmount() {
        return SoulWeaponAttribute.getAmount();
    }

    @Override
    public int getEnchantmentAmount() {
        return SoulWeaponEnchantment.getAmount();
    }

    @Override
    public int getLightningCooldown() {
        return lightningCooldown;
    }

    @Override
    public void resetLightningCooldown() {
        if (!this.player.isCreative()) {
            this.lightningCooldown = Math.round(96 / this.getAttribute(ATTACK_SPEED, this.currentType, true, true));
        }
    }

    @Override
    public void decrementLightningCooldown() {
        this.lightningCooldown--;
    }

    @Override
    public double getCharging() {
        return this.charging;
    }

    @Override
    public void setCharging(final double charging) {
        this.charging = charging;
    }

    @Override
    public int getDaggerCharge() {
        return this.daggerCharge;
    }

    @Override
    public void incrementDaggerCharge() {
        this.daggerCharge++;
    }

    @Override
    public void resetDaggerCharge() {
        this.daggerCharge = 0;
    }

    @Override
    public Class<? extends ISoulItem> getBaseItemClass() {
        return ItemSoulWeapon.class;
    }

    @Override
    public void update() {
        super.update();

        if (this.getCooldown() > 0) {
            this.decrementCooldown();
        }

        if (this.getCurrentType() != null && this.getLightningCooldown() > 0) {
            this.decrementLightningCooldown();
        }
    }

    @Override
    public void sync() {
        if (!this.player.world.isRemote) {
            Main.CHANNEL.sendTo(
                    new CWeaponData(this.currentType, this.currentTab, this.attackCooldown, this.boundSlot, this.data, this.attributes, this.enchantments),
                    (EntityPlayerMP) this.player
            );
        }
    }
}
