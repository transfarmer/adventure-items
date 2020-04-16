package transfarmer.soulboundarmory.capability.soulbound.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.BaseSoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.network.client.S2CSync;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.soulboundarmory.statistics.v2.statistics.Statistics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper.REACH_DISTANCE_UUID;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulToolDatum.DATA;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulToolDatum.TOOL_DATA;
import static transfarmer.soulboundarmory.statistics.SoulType.PICK;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.REACH_DISTANCE;

public class SoulTool extends BaseSoulCapability implements ISoulCapability {
    public SoulTool() {
        super(TOOL_DATA, new Statistics(new String[]{"pick"},
                new String[]{"datum, attribute, enchantment"},
                new String[][]{
                        {"xp", "level", "skills", "attributePoints", "enchantmentPoints", "spentAttributePoints", "spentEnchantmentPoints"},
                        {"efficiencyAttribute", "reachDistance", "harvestLevel"},
                        {"efficiencyEnchantment", "fortune", "silkTouch"}},
                new double[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0.5, 2, 0, 0, 0, 0},
                }
        ));

        this.currentTab = 0;
    }

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
    public SoulType getType(final int index) {
        return index == 0 ? PICK : null;
    }

    @Override
    public SoulType getType(final ItemStack itemStack) {
        return SoulToolType.get(itemStack);
    }

    @Override
    public SoulType getCurrentType() {
        return this.currentType;
    }

    @Override
    public int getIndex(final SoulType type) {
        return type == PICK ? 0 : -1;
    }

    @Override
    public int getIndex() {
        return this.getIndex(this.getCurrentType());
    }

    @Override
    public int getDatum(final SoulDatum datum, final SoulType type) {
        return this.data[type.getIndex()][datum.getIndex()];
    }

    @Override
    public void setDatum(final int amount, final SoulDatum datum, final SoulType type) {
        this.data[type.getIndex()][datum.getIndex()] = amount;
    }

    @Override
    public float getAttribute(final SoulAttribute attribute, final SoulType type, final boolean total, final boolean effective) {
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
    public void setAttributes(final float[] attributes, final SoulType type) {
        this.attributes[type.getIndex()] = attributes;
    }

    @Override
    public void addAttribute(final int amount, final SoulAttribute attribute, final SoulType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(-sign, DATA.attributePoints, type);
            this.addDatum(sign, DATA.spentAttributePoints, type);

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
    public void setData(final int[] data, final SoulType type) {
        this.data[type.getIndex()] = data;
    }

    @Override
    public int getEnchantment(final SoulEnchantment enchantment, final SoulType type) {
        return this.enchantments[type.getIndex()][enchantment.getIndex()];
    }

    @Override
    public void setEnchantments(final int[] enchantments, final SoulType type) {
        this.enchantments[type.getIndex()] = enchantments;
    }

    @Override
    public void addEnchantment(final int amount, final SoulEnchantment enchantment, final SoulType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (this.getEnchantment(enchantment, type) + sign >= 0) {
                this.addDatum(-sign, DATA.enchantmentPoints, type);
                this.addDatum(sign, DATA.spentEnchantmentPoints, type);

                this.enchantments[type.getIndex()][enchantment.getIndex()] += sign;
            } else {
                return;
            }
        }
    }

    @Override
    public int getLevelXP(final SoulType type, final int level) {
        return this.canLevelUp(type)
                ? MainConfig.instance().getInitialToolXP() + (int) Math.round(4 * Math.pow(level, 1.25))
                : -1;
    }

    @Override
    public void setCurrentType(final SoulType type) {
        this.currentType = type;
    }

    @Override
    public void setCurrentType(final int index) {
        this.currentType = SoulToolType.get(index);
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return this.getItemStack(SoulToolType.get(itemStack));
    }

    @Override
    public ItemStack getItemStack(final SoulType type) {
        final ItemStack itemStack = new ItemStack(type.getItem());
        final AttributeModifier[] attributeModifiers = this.getAttributeModifiers(type);
        final Map<SoulEnchantment, Integer> enchantments = this.getEnchantments(type);

        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), attributeModifiers[0], MAINHAND);
        enchantments.forEach((final SoulEnchantment enchantment, final Integer level) -> itemStack.addEnchantment(enchantment.getEnchantment(), level));

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(final SoulType type) {
        return new AttributeModifier[]{
                new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.getAttribute(REACH_DISTANCE, type) + type.getSoulItem().getReachDistance(), ADD)
        };
    }

    @Override
    public Map<SoulEnchantment, Integer> getEnchantments(final SoulType type) {
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
    public List<String> getTooltip(final SoulType type) {
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
    public List<Item> getConsumableItems() {
        return Arrays.asList(Items.WOODEN_PICKAXE);
    }

    @Override
    public int getItemAmount() {
        return SoulToolType.getAmount();
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
    public Class<? extends ISoulItem> getBaseItemClass() {
        return IItemSoulTool.class;
    }

    @Override
    public NBTTagCompound writeToNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("soultools.capability.index", this.getCurrentType() == null ? -1 : this.getCurrentType().getIndex());
        tag.setInteger("soultools.capability.tab", this.getCurrentTab());
        tag.setInteger("soultools.capability.boundSlot", this.getBoundSlot());

        this.forEach(
                (final Integer toolIndex, final Integer valueIndex) ->
                        tag.setInteger(String.format("soultools.datum.%s.%s",
                                this.getType(toolIndex),
                                TOOL_DATA.get(valueIndex)),
                                this.data[toolIndex][valueIndex]),
                (final Integer toolIndex, final Integer valueIndex) ->
                        tag.setFloat(String.format("soultools.attribute.%s.%s",
                                this.getType(toolIndex),
                                SoulToolAttribute.get(valueIndex)),
                                this.attributes[toolIndex][valueIndex]),
                (final Integer toolIndex, final Integer valueIndex) ->
                        tag.setInteger(String.format("soultools.enchantment.%s.%s",
                                this.getType(toolIndex),
                                SoulToolEnchantment.get(valueIndex)),
                                this.enchantments[toolIndex][valueIndex])
        );

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        this.setCurrentType(tag.getInteger("soultools.capability.index"));
        this.setCurrentTab(tag.getInteger("soultools.capability.tab"));
        this.bindSlot(tag.getInteger("soultools.capability.boundSlot"));

        this.forEach(
                (final Integer toolIndex, final Integer valueIndex) ->
                        this.data[toolIndex][valueIndex] = tag.getInteger(String.format("soultools.datum.%s.%s",
                                this.getType(toolIndex),
                                TOOL_DATA.get(valueIndex)
                        )),
                (final Integer toolIndex, final Integer valueIndex) ->
                        this.attributes[toolIndex][valueIndex] = tag.getFloat(String.format("soultools.attribute.%s.%s",
                                this.getType(toolIndex),
                                SoulToolAttribute.get(valueIndex)
                        )),
                (final Integer toolIndex, final Integer valueIndex) ->
                        this.enchantments[toolIndex][valueIndex] = tag.getInteger(String.format("soultools.enchantment.%s.%s",
                                this.getType(toolIndex),
                                SoulToolEnchantment.get(valueIndex)
                        ))
        );
    }

    @Override
    public void sync() {
        if (!this.player.world.isRemote) {
            Main.CHANNEL.sendTo(new S2CSync("tool", this.writeToNBT()), (EntityPlayerMP) this.player);
        }
    }
}
