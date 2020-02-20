package transfarmer.soulboundarmory.capability.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.capability.SoulItemHelper.REACH_DISTANCE_UUID;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.*;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolDatum.*;

public class SoulTool implements ISoulCapability {
    private IType currentType;
    private int[][] data = new int[this.getItemAmount()][this.getDatumAmount()];
    private float[][] attributes = new float[this.getItemAmount()][this.getAttributeAmount()];
    private int[][] enchantments = new int[this.getItemAmount()][this.getEnchantmentAmount()];
    private int boundSlot = -1;
    private int currentTab = 0;

    @Override
    public void setStatistics(final int[][] data, final float[][] attributes, final int[][] enchantments) {
        this.data = data;
        this.attributes = attributes;
        this.enchantments = enchantments;
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
    public void setData(final int[][] data) {
        this.data = data;
    }

    @Override
    public void setAttributes(final float[][] attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setEnchantments(final int[][] enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public IType getType(final ItemStack itemStack) {
        return SoulToolType.getType(itemStack);
    }

    @Override
    public IType getCurrentType() {
        return this.currentType;
    }

    @Override
    public int getDatum(final SoulDatum datum, final IType type) {
        return this.data[type.getIndex()][datum.getIndex()];
    }

    @Override
    public boolean addDatum(final int amount, final SoulDatum datum, final IType type) {
        if (XP.equals(datum)) {
            this.data[type.getIndex()][XP.getIndex()] += amount;

            if (this.getDatum(XP, type) >= this.getNextLevelXP(type) && this.getDatum(LEVEL, type) < Configuration.maxLevel) {
                final int nextLevelXP = this.getNextLevelXP(type);
                this.addDatum(1, LEVEL, type);
                this.addDatum(-nextLevelXP, XP, type);

                return true;
            }
        } else if (LEVEL.equals(datum)) {
            final int level = ++this.data[type.getIndex()][LEVEL.getIndex()];
            if (level % (Configuration.levelsPerEnchantment) == 0) {
                this.addDatum(1, ENCHANTMENT_POINTS, type);
            }

            if (level % (Configuration.levelsPerSkill) == 0 && this.getDatum(SKILLS, type) < type.getSkills().length) {
                this.addDatum(1, SKILLS, type);
            }

            this.addDatum(1, ATTRIBUTE_POINTS, type);
        } else {
            this.data[type.getIndex()][datum.getIndex()] += amount;
        }

        return false;
    }

    @Override
    public void setDatum(final int amount, final SoulDatum datum, final IType type) {
        this.data[type.getIndex()][datum.getIndex()] = amount;
    }

    @Override
    public float getAttribute(final SoulAttribute attribute, final IType type, final boolean total, final boolean effective) {
        if (total) {
            if (attribute.equals(EFFICIENCY_ATTRIBUTE)) {
                return this.getAttribute(EFFICIENCY_ATTRIBUTE, type) + ((IItemSoulTool) type.getSoulItem()).getEfficiency();
            } else if (attribute.equals(REACH_DISTANCE)) {
                return 3 + this.getAttribute(REACH_DISTANCE, type) + type.getSoulItem().getReachDistance();
            }
        }

        return this.attributes[type.getIndex()][attribute.getIndex()];
    }

    @Override
    public float getAttribute(final SoulAttribute attribute, final IType type, final boolean total) {
        return this.getAttribute(attribute, type, total, false);
    }

    @Override
    public float getAttribute(final SoulAttribute attribute, final IType type) {
        return this.getAttribute(attribute, type, false, false);
    }

    @Override
    public void setAttribute(final float value, final SoulAttribute attribute, final IType type) {
        this.attributes[type.getIndex()][attribute.getIndex()] = value;
    }

    @Override
    public void setAttributes(final float[] attributes, final IType type) {
        this.attributes[type.getIndex()] = attributes;
    }

    @Override
    public void addAttribute(final int amount, final SoulAttribute attribute, final IType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(-sign, ATTRIBUTE_POINTS, type);
            this.addDatum(sign, SPENT_ATTRIBUTE_POINTS, type);

            if (attribute.equals(HARVEST_LEVEL) && this.getAttribute(HARVEST_LEVEL, type) + sign * HARVEST_LEVEL.getIncrease(type) >= 2.9999) {
                this.attributes[type.getIndex()][HARVEST_LEVEL.getIndex()] = 3;
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
    public void setData(final int[] data, final IType type) {
        this.data[type.getIndex()] = data;
    }

    @Override
    public int getEnchantment(final SoulEnchantment enchantment, final IType type) {
        return this.enchantments[type.getIndex()][enchantment.getIndex()];
    }

    @Override
    public void setEnchantments(final int[] enchantments, final IType type) {
        this.enchantments[type.getIndex()] = enchantments;
    }

    @Override
    public void addEnchantment(final int amount, final SoulEnchantment enchantment, final IType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (this.getEnchantment(enchantment, type) + sign >= 0) {
                this.addDatum(-sign, ENCHANTMENT_POINTS, type);
                this.addDatum(sign, SPENT_ENCHANTMENT_POINTS, type);

                this.enchantments[type.getIndex()][enchantment.getIndex()] += sign;
            } else {
                return;
            }
        }
    }

    @Override
    public int getNextLevelXP(final IType type) {
        return this.getDatum(LEVEL, type) >= Configuration.maxLevel
                ? 1
                : Configuration.initialToolXP + (int) Math.round(4 * Math.pow(this.getDatum(LEVEL, type), 1.25));
    }

    @Override
    public void setCurrentType(final IType type) {
        this.currentType = type;
    }

    @Override
    public void setCurrentType(final int index) {
        this.currentType = SoulToolType.getType(index);
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return this.getItemStack(SoulToolType.getType(itemStack));
    }

    @Override
    public ItemStack getItemStack(final IType type) {
        final ItemStack itemStack = new ItemStack(type.getItem());
        final AttributeModifier[] attributeModifiers = this.getAttributeModifiers(type);
        final Map<SoulEnchantment, Integer> enchantments = this.getEnchantments(type);

        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), attributeModifiers[0], MAINHAND);
        enchantments.forEach((final SoulEnchantment enchantment, final Integer level) -> itemStack.addEnchantment(enchantment.getEnchantment(), level));

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(final IType type) {
        return new AttributeModifier[]{
                new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.getAttribute(REACH_DISTANCE, type) + type.getSoulItem().getReachDistance(), ADD)
        };
    }

    @Override
    public Map<SoulEnchantment, Integer> getEnchantments(final IType type) {
        final Map<SoulEnchantment, Integer> enchantments = new LinkedHashMap<>();

        for (final SoulEnchantment enchantment : SoulToolEnchantment.get()) {
            final int level = this.getEnchantment(enchantment, type);

            if (level > 0) {
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }

    @Override
    public List<String> getTooltip(final IType type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(5);

        tooltip.add(String.format(" %s%s %s", Mappings.REACH_DISTANCE_FORMAT, FORMAT.format(this.getAttribute(REACH_DISTANCE, type, true, true)), Mappings.REACH_DISTANCE_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(EFFICIENCY_ATTRIBUTE, type, true, true)), Mappings.EFFICIENCY_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.HARVEST_LEVEL_FORMAT, FORMAT.format(this.getAttribute(HARVEST_LEVEL, type)), Mappings.HARVEST_LEVEL_NAME));

        tooltip.add("");
        tooltip.add("");

        return tooltip;
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public int getItemAmount() {
        return SoulToolType.getAmount();
    }

    @Override
    public int getDatumAmount() {
        return SoulToolDatum.getAmount();
    }

    @Override
    public int getAttributeAmount() {
        return SoulToolAttribute.getAmount();
    }

    @Override
    public int getEnchantmentAmount() {
        return SoulToolEnchantment.getAmount();
    }

    @Override
    public void bindSlot(final int slot) {
        this.boundSlot = slot;
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
    }
}
