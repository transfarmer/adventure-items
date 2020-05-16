package transfarmer.soulboundarmory.component.soulbound.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import transfarmer.farmerlib.util.CollectionUtil;
import transfarmer.soulboundarmory.client.gui.screen.common.EnchantmentTab;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SkillsTab;
import transfarmer.soulboundarmory.client.gui.screen.tool.ToolAttributesTab;
import transfarmer.soulboundarmory.client.gui.screen.tool.ToolConfirmationTab;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundBase;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.SoulboundItem;
import transfarmer.soulboundarmory.item.SoulboundToolItem;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.StatisticType;
import transfarmer.soulboundarmory.statistics.IItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.Main.SOULBOUND_PICK_ITEM;
import static transfarmer.soulboundarmory.component.soulbound.tool.ToolProvider.TOOLS;
import static transfarmer.soulboundarmory.statistics.Item.PICK;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.EFFICIENCY;
import static transfarmer.soulboundarmory.statistics.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.REACH;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public class ToolComponent extends SoulboundBase implements IToolComponent {
    public ToolComponent(final PlayerEntity player) {
        super(player, new IItem[]{PICK}, new Item[]{SOULBOUND_PICK_ITEM});

        final List<IItem> itemTypes = this.itemTypes.keyList();
    }

    @Override
    public double getAttributeRelative(final IItem type, final StatisticType attribute) {
        if (attribute == REACH) {
            return this.getAttribute(type, REACH) - 3;
        }

        return this.statistics.get(type, attribute).doubleValue();
    }

    @Override
    public double getAttributeTotal(final IItem item, final StatisticType statistic) {
        return this.getAttribute(item, statistic);
    }

    @Override
    public void addAttribute(final IItem item, final StatisticType attribute, final int amount) {
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
    public double getIncrease(final IItem type, final StatisticType statistic) {
        if (type == PICK) {
            return statistic == EFFICIENCY
                    ? 0.5
                    : statistic == REACH
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
    @Environment(EnvType.CLIENT)
    public List<String> getTooltip(final IItem item) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(5);

        tooltip.add(String.format(" %s%s %s", Mappings.REACH_DISTANCE_FORMAT, FORMAT.format(this.getAttribute(item, REACH)), Mappings.REACH_DISTANCE_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.TOOL_EFFICIENCY_FORMAT, FORMAT.format(this.getAttribute(item, EFFICIENCY)), Mappings.EFFICIENCY_NAME));
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
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = CollectionUtil.arrayList(new ToolConfirmationTab(tabs), new ToolAttributesTab(tabs), new EnchantmentTab(TOOLS, tabs), new SkillsTab(TOOLS, tabs));

        return tabs;
    }

    @Override
    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundToolItem.class;
    }
}
