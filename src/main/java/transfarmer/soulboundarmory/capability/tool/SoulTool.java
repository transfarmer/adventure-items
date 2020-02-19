package transfarmer.soulboundarmory.capability.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.data.IAttribute;
import transfarmer.soulboundarmory.data.IDatum;
import transfarmer.soulboundarmory.data.IEnchantment;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.data.tool.SoulToolDatum;
import transfarmer.soulboundarmory.data.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.i18n.Mappings;
import transfarmer.soulboundarmory.item.IItemSoulTool;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.capability.tool.SoulToolHelper.REACH_DISTANCE_UUID;
import static transfarmer.soulboundarmory.data.tool.SoulToolAttribute.*;
import static transfarmer.soulboundarmory.data.tool.SoulToolDatum.*;

public class SoulTool implements ISoulTool {
    private IType currentType;
    private static final int SOUL_TOOLS = SoulToolType.getTypes().length;
    private static final int DATA = SoulToolDatum.getData().length;
    private static final int ATTRIBUTES = SoulToolAttribute.getAttributes().length;
    private static final int ENCHANTMENTS = SoulToolEnchantment.getEnchantments().length;
    private int[][] data = new int[SOUL_TOOLS][DATA];
    private float[][] attributes = new float[SOUL_TOOLS][ATTRIBUTES];
    private int[][] enchantments = new int[SOUL_TOOLS][ENCHANTMENTS];
    private int boundSlot = -1;
    private int currentTab = -1;

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
    public IType getCurrentType() {
        return this.currentType;
    }

    @Override
    public IDatum getEnumXP() {
        return XP;
    }

    @Override
    public IDatum getEnumLevel() {
        return LEVEL;
    }

    @Override
    public IDatum getEnumAttributePoints() {
        return ATTRIBUTE_POINTS;
    }

    @Override
    public IDatum getEnumEnchantmentPoints() {
        return ENCHANTMENT_POINTS;
    }

    @Override
    public IDatum getEnumSpentAttributePoints() {
        return SPENT_ATTRIBUTE_POINTS;
    }

    @Override
    public IDatum getEnumSpentEnchantmentPoints() {
        return SPENT_ENCHANTMENT_POINTS;
    }

    @Override
    public IDatum getEnumSkills() {
        return SKILLS;
    }

    @Override
    public int getDatum(final IDatum datum, final IType type) {
        return this.data[type.getIndex()][datum.getIndex()];
    }

    @Override
    public boolean addDatum(final int amount, final IDatum datum, final IType type) {
        switch ((SoulToolDatum) datum) {
            case XP:
                this.data[type.getIndex()][XP.getIndex()] += amount;

                if (this.getDatum(XP, type) >= this.getNextLevelXP(type) && this.getDatum(LEVEL, type) < Configuration.maxLevel) {
                    this.addDatum(-this.getNextLevelXP(type), XP, type);
                    this.addDatum(1, LEVEL, type);

                    return true;
                }

                break;
            case LEVEL:
                final int level = ++this.data[type.getIndex()][LEVEL.getIndex()];
                if (level % (Configuration.levelsPerEnchantment) == 0) {
                    this.addDatum(1, ENCHANTMENT_POINTS, type);
                }

                if (level % (Configuration.levelsPerSkill) == 0 && this.getDatum(SKILLS, type) < type.getSkills().length) {
                    this.addDatum(1, SKILLS, type);
                }

                this.addDatum(1, ATTRIBUTE_POINTS, type);
                break;
            default:
                this.data[type.getIndex()][datum.getIndex()] += amount;
        }

        return false;
    }

    @Override
    public void setDatum(final int amount, final IDatum datum, final IType type) {
        this.data[type.getIndex()][datum.getIndex()] = amount;
    }

    @Override
    public float getAttribute(final IAttribute attribute, final IType type) {
        return this.attributes[type.getIndex()][attribute.getIndex()];
    }

    @Override
    public void setAttribute(final float value, final IAttribute attribute, final IType type) {
        this.attributes[type.getIndex()][attribute.getIndex()] = value;
    }

    @Override
    public void setAttributes(final float[] attributes, final IType type) {
        this.attributes[type.getIndex()] = attributes;
    }

    @Override
    public void addAttribute(final int amount, final IAttribute attribute, final IType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(-sign, ATTRIBUTE_POINTS, type);
            this.addDatum(sign, SPENT_ATTRIBUTE_POINTS, type);

            if (attribute == HARVEST_LEVEL && this.getAttribute(HARVEST_LEVEL, type) + sign * HARVEST_LEVEL.getIncrease(type) >= 2.9999) {
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
    public int getEnchantment(final IEnchantment enchantment, final IType type) {
        return this.enchantments[type.getIndex()][enchantment.getIndex()];
    }

    @Override
    public void setEnchantments(final int[] enchantments, final IType type) {
        this.enchantments[type.getIndex()] = enchantments;
    }

    @Override
    public void addEnchantment(final int amount, final IEnchantment enchantment, final IType type) {
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
                : Configuration.initialToolXP + 4 * (int) Math.round(Math.pow(this.getDatum(LEVEL, type), 1.25));
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
    public float getEffectiveEfficiency(final IType type) {
        return this.getAttribute(EFFICIENCY_ATTRIBUTE, type) + ((IItemSoulTool) type.getSoulItem()).getEfficiency();
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return this.getItemStack(SoulToolType.getType(itemStack));
    }

    @Override
    public ItemStack getItemStack(final IType type) {
        final ItemStack itemStack = new ItemStack(type.getItem());
        final AttributeModifier[] attributeModifiers = this.getAttributeModifiers(type);
        final Map<IEnchantment, Integer> enchantments = this.getEnchantments(type);

        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), attributeModifiers[0], MAINHAND);
        enchantments.forEach((final IEnchantment enchantment, final Integer level) -> itemStack.addEnchantment(enchantment.getEnchantment(), level));

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(final IType type) {
        return new AttributeModifier[]{
                new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.getAttribute(REACH_DISTANCE, type) + type.getSoulItem().getReachDistance(), ADD)
        };
    }

    @Override
    public Map<IEnchantment, Integer> getEnchantments(final IType type) {
        final Map<IEnchantment, Integer> enchantments = new LinkedHashMap<>();

        for (final IEnchantment enchantment : SoulToolEnchantment.getEnchantments()) {
            final int level = this.getEnchantment(enchantment, type);

            if (level > 0) {
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }

    @Override
    public String[] getTooltip(final IType type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(7);

        tooltip.add(String.format(" %s%s %s", Mappings.REACH_DISTANCE_FORMAT, FORMAT.format(this.getEffectiveReachDistance(type)), Mappings.REACH_DISTANCE_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, FORMAT.format(this.getEffectiveEfficiency(type)), Mappings.EFFICIENCY_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.HARVEST_LEVEL_FORMAT, FORMAT.format(this.getAttribute(HARVEST_LEVEL, type)), Mappings.HARVEST_LEVEL_NAME));

        tooltip.add("");
        tooltip.add("");

        return tooltip.toArray(new String[0]);
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public int getItemAmount() {
        return SOUL_TOOLS;
    }

    @Override
    public int getDatumAmount() {
        return DATA;
    }

    @Override
    public int getAttributeAmount() {
        return ATTRIBUTES;
    }

    @Override
    public int getEnchantmentAmount() {
        return ENCHANTMENTS;
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
    public float getEffectiveReachDistance(final IType type) {
        return 3 + this.getAttribute(REACH_DISTANCE, type) + type.getSoulItem().getReachDistance();
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
