package transfarmer.soularsenal.capability.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import transfarmer.soularsenal.Configuration;
import transfarmer.soularsenal.data.tool.SoulToolAttribute;
import transfarmer.soularsenal.data.tool.SoulToolDatum;
import transfarmer.soularsenal.data.tool.SoulToolEnchantment;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.i18n.Mappings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soularsenal.capability.tool.SoulToolHelper.*;
import static transfarmer.soularsenal.data.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soularsenal.data.tool.SoulToolAttribute.REACH_DISTANCE;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.*;

public class SoulTool implements ISoulTool {
    private SoulToolType currentType;
    private int[][] data = new int[SOUL_TOOLS][DATA];
    private float[][] attributes = new float[SOUL_TOOLS][ATTRIBUTES];
    private int[][] enchantments = new int[SOUL_TOOLS][ENCHANTMENTS];
    private int boundSlot = -1;
    private int currentTab;

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
    public int getDatum(final SoulToolDatum datum, final SoulToolType type) {
        return this.data[type.index][datum.index];
    }

    @Override
    public boolean addDatum(final int amount, final SoulToolDatum datum, final SoulToolType type) {
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

                if (level % (Configuration.levelsPerSkill) == 0 && this.getDatum(SKILLS, type) < SoulToolHelper.getMaxSkills(type)) {
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
    public void setDatum(final int amount, final SoulToolDatum datum, final SoulToolType type) {
        this.data[datum.index][type.index] = amount;
    }

    @Override
    public float getAttribute(final SoulToolAttribute attribute, final SoulToolType type) {
        return this.attributes[type.index][attribute.index];
    }

    @Override
    public void setAttributes(final float[] attributes, final SoulToolType type) {
        this.attributes[type.index] = attributes;
    }

    @Override
    public void addAttribute(final int amount, final SoulToolAttribute attribute, final SoulToolType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(-sign, ATTRIBUTE_POINTS, type);
            this.addDatum(sign, SPENT_ATTRIBUTE_POINTS, type);

            if (this.attributes[type.index][attribute.index] + sign * attribute.getIncrease(type) > 0.0001) {
                this.attributes[type.index][attribute.index] += sign * attribute.getIncrease(type);
            } else {
                this.attributes[type.index][attribute.index] = 0;
                return;
            }
        }
    }

    @Override
    public int getEnchantment(final SoulToolEnchantment enchantment, final SoulToolType type) {
        return this.enchantments[type.index][enchantment.index];
    }

    @Override
    public void setEnchantments(final int[] enchantments, final SoulToolType type) {
        this.enchantments[type.index] = enchantments;
    }

    @Override
    public void addEnchantment(final int amount, final SoulToolEnchantment enchantment, final SoulToolType type) {
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
    public SoulToolType getCurrentType() {
        return this.currentType;
    }

    @Override
    public void setCurrentType(final SoulToolType type) {
        this.currentType = type;
    }

    @Override
    public void setCurrentType(final int index) {
        this.currentType = SoulToolType.getType(index);
    }

    @Override
    public float getEffectiveEfficiency(final SoulToolType type) {
        return this.data[type.index][EFFICIENCY_ATTRIBUTE.index] + type.getItem().getEfficiency();
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return getItemStack(SoulToolType.getType(itemStack));
    }

    @Override
    public ItemStack getItemStack(final SoulToolType type) {
        final ItemStack itemStack = new ItemStack(type.getItem());
        final AttributeModifier[] attributeModifiers = getAttributeModifiers(type);
        final SortedMap<SoulToolEnchantment, Integer> enchantments = this.getEnchantments(type);

        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), attributeModifiers[0], MAINHAND);
        enchantments.forEach((final SoulToolEnchantment enchantment, final Integer level) -> itemStack.addEnchantment(enchantment.getEnchantment(), level));

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(final SoulToolType type) {
        return new AttributeModifier[]{
                new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", type.getItem().getReachDistance(), ADD)
        };
    }

    @Override
    public SortedMap<SoulToolEnchantment, Integer> getEnchantments(final SoulToolType type) {
        final SortedMap<SoulToolEnchantment, Integer> enchantments = new TreeMap<>();

        for (final SoulToolEnchantment enchantment : SoulToolEnchantment.getEnchantments()) {
            final int level = this.getEnchantment(enchantment, type);

            if (level > 0) {
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }

    @Override
    public String[] getTooltip(final SoulToolType type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(7);
        final Map<SoulToolEnchantment, Integer> enchantments = this.getEnchantments(type);

        tooltip.add(String.format(" %s%s %s", Mappings.REACH_DISTANCE_FORMAT, FORMAT.format(this.getEffectiveReachDistance(type)), Mappings.REACH_DISTANCE_NAME));

        tooltip.add("");
        tooltip.add("");

        return tooltip.toArray(new String[0]);
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public void setBoundSlot(final int slot) {
        this.boundSlot = slot;
    }

    @Override
    public void unbindSlot() {
        this.boundSlot = -1;
    }

    @Override
    public float getEffectiveReachDistance(final SoulToolType type) {
        return 3 + this.getAttribute(REACH_DISTANCE, type) + type.getItem().getReachDistance();
    }

    @Override
    public int getCurrentTab() {
        return this.currentTab;
    }

    @Override
    public void setCurrentTab(final int tab) {
        this.currentTab = tab;
    }

    @Override
    public int getNextLevelXP(final SoulToolType type) {
        return Configuration.maxLevel >= this.getDatum(LEVEL, type)
                ? 1
                : Configuration.initialXP + (int) Math.round(Math.pow(this.getDatum(LEVEL, type), 1.25));
    }
}
