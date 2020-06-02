package user11681.soulboundarmory.component.soulbound.item.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import user11681.soulboundarmory.client.gui.screen.common.EnchantmentTab;
import user11681.soulboundarmory.client.gui.screen.common.ScreenTab;
import user11681.soulboundarmory.client.gui.screen.common.SkillTab;
import user11681.soulboundarmory.client.gui.screen.tool.ToolAttributeTab;
import user11681.soulboundarmory.client.gui.screen.tool.ToolConfirmationTab;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.Statistic;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundToolItem;

import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.MINING_LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public abstract class ToolStorage<T> extends ItemStorage<T> {
    public ToolStorage(final SoulboundComponent component, final Item item) {
        super(component, item);
    }

    public double getAttributeTotal(final StatisticType statistic) {
        return this.getAttribute(statistic);
    }

    public void addAttribute(final StatisticType attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (sign > 0 && this.getDatum(ATTRIBUTE_POINTS) > 0 || sign < 0 && this.getDatum(SPENT_ATTRIBUTE_POINTS) > 0) {
                this.addDatum(ATTRIBUTE_POINTS, -sign);
                this.addDatum(SPENT_ATTRIBUTE_POINTS, sign);

                if (attribute.equals(MINING_LEVEL) && this.getAttribute(MINING_LEVEL) + sign * this.getIncrease(MINING_LEVEL) >= 2.9999) {
                    this.setAttribute(attribute, 3);

                    return;
                }

                final Statistic statistic = this.getStatistic(attribute);

                if (statistic.doubleValue() + sign * this.getIncrease(attribute) <= statistic.min()) {
                    statistic.setValue(statistic.min());

                    return;
                }

                statistic.add(sign * this.getIncrease(attribute));
            }
        }
    }

    public int getLevelXP(final int level) {
        return this.canLevelUp()
              ? Configuration.instance().initialToolXP + (int) Math.round(4 * Math.pow(level, 1.25))
                : -1;
    }

    public Text getMiningLevel() {
        return Mappings.getMiningLevels()[(int) this.getAttribute(MINING_LEVEL)];
    }

    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = Arrays.asList(new ToolConfirmationTab(this.component, tabs), new ToolAttributeTab(this.component, tabs), new EnchantmentTab(this.component, tabs), new SkillTab(this.component, tabs));

        return tabs;
    }

    @Override
    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundToolItem.class;
    }
}
