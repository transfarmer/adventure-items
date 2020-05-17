package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.client.gui.screen.common.EnchantmentTab;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SkillTab;
import transfarmer.soulboundarmory.client.gui.screen.tool.ToolAttributesTab;
import transfarmer.soulboundarmory.client.gui.screen.tool.ToolConfirmationTab;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public abstract class SoulboundToolComponent<C extends Component> extends SoulboundItemComponent<C> {
    public SoulboundToolComponent(final ItemStack itemStack, final PlayerEntity player) {
        super(itemStack, player);
    }

    @Override
    public double getAttributeTotal(final StatisticType statistic) {
        return this.getAttribute(statistic);
    }

    @Override
    public void addAttribute(final StatisticType attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (sign > 0 && this.getDatum(ATTRIBUTE_POINTS) > 0 || sign < 0 && this.getDatum(SPENT_ATTRIBUTE_POINTS) > 0) {
                this.addDatum(ATTRIBUTE_POINTS, -sign);
                this.addDatum(SPENT_ATTRIBUTE_POINTS, sign);

                if (attribute.equals(HARVEST_LEVEL) && this.getAttribute(HARVEST_LEVEL) + sign * this.getIncrease(HARVEST_LEVEL) >= 2.9999) {
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

    @Override
    public int getLevelXP(final int level) {
        return this.canLevelUp()
                ? MainConfig.instance().getInitialToolXP() + (int) Math.round(4 * Math.pow(level, 1.25))
                : -1;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = Arrays.asList(new ToolConfirmationTab(tabs), new ToolAttributesTab(tabs), new EnchantmentTab(this, tabs), new SkillTab(this, tabs));

        return tabs;
    }
}
