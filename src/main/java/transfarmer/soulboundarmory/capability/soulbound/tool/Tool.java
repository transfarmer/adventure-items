package transfarmer.soulboundarmory.capability.soulbound.tool;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundBase;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabEnchantments;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSkills;
import transfarmer.soulboundarmory.client.gui.screen.tool.GuiTabToolAttributes;
import transfarmer.soulboundarmory.client.gui.screen.tool.GuiTabToolConfirmation;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.item.SoulboundTool;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.pick.SkillAmbidexterity;
import transfarmer.soulboundarmory.skill.pick.SkillTeleportation;
import transfarmer.soulboundarmory.statistics.Skills;
import transfarmer.soulboundarmory.statistics.SoulboundEnchantments;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.Statistics;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.init.Enchantments.UNBREAKING;
import static net.minecraft.init.Enchantments.VANISHING_CURSE;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOLS;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_PICK;
import static transfarmer.soulboundarmory.statistics.base.enumeration.CapabilityType.TOOL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.PICK;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public class Tool extends SoulboundBase implements ITool {
    public Tool() {
        super(TOOL, new IItem[]{PICK}, new Item[]{SOULBOUND_PICK});

        final List<IItem> itemTypes = this.itemTypes.keyList();

        this.statistics = new Statistics(itemTypes,
                new ICategory[]{DATUM, ATTRIBUTE},
                new IStatistic[][]{
                        {XP, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS},
                        {EFFICIENCY_ATTRIBUTE, REACH_DISTANCE, HARVEST_LEVEL}
                }, new double[][][]{{{0, 0, 0, 0, 0, 0, 0}, {0.5, 2, 0}}}
        );
        this.enchantments = new SoulboundEnchantments(itemTypes, this.items, (final Enchantment enchantment, final IItem item) -> {
            final String name = enchantment.getName().toLowerCase();

            return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && !name.contains("soulbound") && !name.contains("holding") && !name.contains("smelt")
                    && !name.contains("mending");
        });
        this.skills = new Skills(itemTypes, new Skill[]{new SkillTeleportation(), new SkillAmbidexterity()});
    }

    @Override
    public double getAttributeRelative(final IItem type, final IStatistic attribute) {
        if (attribute == REACH_DISTANCE) {
            return this.getAttribute(type, REACH_DISTANCE) - 3;
        }

        return this.statistics.get(type, attribute).doubleValue();
    }

    @Override
    public double getAttributeTotal(final IItem item, final IStatistic statistic) {
        return this.getAttribute(item, statistic);
    }

    @Override
    public void addAttribute(final IItem item, final IStatistic attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (sign > 0 && this.getDatum(item, ATTRIBUTE_POINTS) > 0 || sign < 0 && this.getDatum(item, SPENT_ATTRIBUTE_POINTS) > 0) {
                this.addDatum(item, ATTRIBUTE_POINTS, -sign);
                this.addDatum(item, SPENT_ATTRIBUTE_POINTS, sign);

                if (attribute.equals(HARVEST_LEVEL) && this.getAttribute(item, HARVEST_LEVEL) + sign * this.getIncrease(item, HARVEST_LEVEL) >= 2.9999) {
                    this.setAttribute(item, attribute, 3);

                    return;
                }

                final Statistic statistic = this.getStatistic(item, attribute);

                if (statistic.doubleValue() + sign * this.getIncrease(item, attribute) <= statistic.min()) {
                    statistic.setValue(statistic.min());

                    return;
                }

                statistic.add(sign * this.getIncrease(item, attribute));
            }
        }
    }

    @Override
    public double getIncrease(final IItem type, final IStatistic statistic) {
        if (type == PICK) {
            return statistic == EFFICIENCY_ATTRIBUTE
                    ? 0.5
                    : statistic == REACH_DISTANCE
                    ? 0.1
                    : statistic == HARVEST_LEVEL
                    ? 0.2
                    : 0;
        }

        return 0;
    }

    @Override
    public int getLevelXP(final IItem type, final int level) {
        return this.canLevelUp(type)
                ? MainConfig.instance().getInitialToolXP() + (int) Math.round(4 * Math.pow(level, 1.25))
                : -1;
    }

    @Override
    @SideOnly(CLIENT)
    public List<String> getTooltip(final IItem item) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(5);

        tooltip.add(String.format(" %s%s %s", Mappings.REACH_DISTANCE_FORMAT, FORMAT.format(this.getAttribute(item, REACH_DISTANCE)), Mappings.REACH_DISTANCE_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(item, EFFICIENCY_ATTRIBUTE)), Mappings.EFFICIENCY_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.HARVEST_LEVEL_FORMAT, FORMAT.format(this.getAttribute(item, HARVEST_LEVEL)), Mappings.HARVEST_LEVEL_NAME));

        tooltip.add("");
        tooltip.add("");

        return tooltip;
    }

    @Override
    public Item getConsumableItem(final IItem item) {
        return item == PICK ? Items.WOODEN_PICKAXE : null;
    }

    @Override
    public List<GuiTab> getTabs() {
        List<GuiTab> tabs = new ArrayList<>();
        tabs = CollectionUtil.arrayList(new GuiTabToolConfirmation(tabs), new GuiTabToolAttributes(tabs), new GuiTabEnchantments(TOOLS, tabs), new GuiTabSkills(TOOLS, tabs));

        return tabs;
    }

    @Override
    public Class<? extends ItemSoulbound> getBaseItemClass() {
        return SoulboundTool.class;
    }
}
