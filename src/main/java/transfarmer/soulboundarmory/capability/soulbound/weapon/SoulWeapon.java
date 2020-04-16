package transfarmer.soulboundarmory.capability.soulbound.weapon;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.BaseSoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.client.S2CSync;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.v2.statistics.Statistics;
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
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.GREATSWORD;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.SWORD;

public class SoulWeapon extends BaseSoulCapability implements ISoulWeapon {
    private float leapForce;
    private int attackCooldown;
    private int leapDuration;
    private int lightningCooldown;

    public SoulWeapon() {
        super(WEAPON_DATA, new Statistics(new String[]{"pick"},
                new String[]{"datum, attribute, enchantment"},
                new String[][]{
                        {"xp", "level", "skills", "attributePoints", "enchantmentPoints", "spentAttributePoints", "spentEnchantmentPoints"},
                        {"attackSpeed", "attackDamage", "critical", "knockbackAttribute", "efficiencyAttribute"},
                        {"sharpness", "sweepingEdge", "looting", "fireAspect", "knockbackEnchantment", "smite", "baneOfArthropods"}},
                new double[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0.8, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 1.6, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 2  , 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                }
        ));

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
    public SoulType getType(final int index) {
        switch (index) {
            case 0:
                return GREATSWORD;
            case 1:
                return SWORD;
            case 2:
                return DAGGER;
            default:
                return null;
        }
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
    public int getIndex(final SoulType type) {
        if (type != null) {
            return type.equals(GREATSWORD)
                    ? 0
                    : type.equals(SWORD)
                    ? 1
                    : type.equals(DAGGER)
                    ? 2
                    : -1;
        }

        return -1;
    }

    @Override
    public int getIndex() {
        return this.getIndex(this.currentType);
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
    public void setLightningCooldown(final int ticks) {
        this.lightningCooldown = ticks;
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
    public float getLeapForce() {
        return this.leapForce;
    }

    @Override
    public void setLeapForce(final float force) {
        this.leapForce = force;
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
    public Class<? extends ISoulItem> getBaseItemClass() {
        return ItemSoulWeapon.class;
    }

    @Override
    public void onTick() {
        super.onTick();

        if (this.getCooldown() > 0) {
            this.decrementCooldown();
        }

        if (this.getCurrentType() != null && this.getLightningCooldown() > 0) {
            this.decrementLightningCooldown();
        }

        if (this.getLeapDuration() > 0) {
            this.leapDuration--;
        } else {
            this.setLeapForce(0);
        }
    }

    @Override
    public NBTTagCompound writeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("soulweapons.capability.index", this.getIndex());
        tag.setInteger("soulweapons.capability.tab", this.getCurrentTab());
        tag.setInteger("soulweapons.capability.boundSlot", this.getBoundSlot());
        tag.setInteger("soulweapons.capability.cooldown", this.getCooldown());
        tag.setInteger("soulweapons.capability.leapDuration", this.getLeapDuration());
        tag.setFloat("soulweapons.capability.charging", this.getLeapForce());
        tag.setInteger("soulweapons.capability.lightningCooldown", this.getLightningCooldown());

        this.forEach(
                (final Integer weaponIndex, final Integer valueIndex) ->
                        tag.setInteger(String.format("soulweapons.datum.%s.%s",
                                this.getType(weaponIndex),
                                WEAPON_DATA.get(valueIndex)),
                                this.data[weaponIndex][valueIndex]),
                (final Integer weaponIndex, final Integer valueIndex) ->
                        tag.setFloat(String.format("soulweapons.attribute.%s.%s",
                                this.getType(weaponIndex),
                                SoulWeaponAttribute.get(valueIndex)),
                                this.attributes[weaponIndex][valueIndex]),
                (final Integer weaponIndex, final Integer valueIndex) ->
                        tag.setInteger(String.format("soulweapons.enchantment.%s.%s",
                                this.getType(weaponIndex),
                                SoulWeaponEnchantment.get(valueIndex)),
                                this.enchantments[weaponIndex][valueIndex])
        );

        return tag;
    }

    @Override
    public void readNBT(final NBTTagCompound nbt) {
        this.setCurrentType(nbt.getInteger("soulweapons.capability.index"));
        this.setCurrentTab(nbt.getInteger("soulweapons.capability.tab"));
        this.bindSlot(nbt.getInteger("soulweapons.capability.boundSlot"));
        this.setAttackCooldown(nbt.getInteger("soulweapons.capability.cooldown"));
        this.setLeapDuration(nbt.getInteger("soulweapons.capability.leapDuration"));
        this.setLeapForce(nbt.getFloat("soulweapons.capability.leapForce"));
        this.setLightningCooldown(nbt.getInteger("soulweapons.capability.lightningCooldown"));

        this.forEach(
                (final Integer weaponIndex, final Integer valueIndex) ->
                        this.data[weaponIndex][valueIndex] = nbt.getInteger(String.format("soulweapons.datum.%s.%s",
                                this.getType(weaponIndex),
                                WEAPON_DATA.get(valueIndex)
                        )),
                (final Integer weaponIndex, final Integer valueIndex) ->
                        this.attributes[weaponIndex][valueIndex] = nbt.getFloat(String.format("soulweapons.attribute.%s.%s",
                                this.getType(weaponIndex),
                                SoulWeaponAttribute.get(valueIndex)
                        )),
                (final Integer weaponIndex, final Integer valueIndex) ->
                        this.enchantments[weaponIndex][valueIndex] = nbt.getInteger(String.format("soulweapons.enchantment.%s.%s",
                                this.getType(weaponIndex),
                                SoulWeaponEnchantment.get(valueIndex)
                        ))
        );
    }

    @Override
    public void sync() {
        if (!this.player.world.isRemote) {
            Main.CHANNEL.sendTo(new S2CSync("weapon", this.writeNBT()), (EntityPlayerMP) this.player);
        }
    }
}
