package user11681.soulboundarmory.client.gui.screen.tool;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import user11681.soulboundarmory.client.gui.screen.common.AttributeTab;
import user11681.soulboundarmory.client.gui.screen.common.ScreenTab;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.StatisticType;

import static user11681.soulboundarmory.component.statistics.Category.ATTRIBUTE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.EFFICIENCY;
import static user11681.soulboundarmory.component.statistics.StatisticType.MINING_LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.REACH;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public class ToolAttributeTab extends AttributeTab implements ToolTab {
    public ToolAttributeTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
        super(component, tabs);
    }

    @Override
    public void init() {
        super.init();

        final int size = this.storage.size(ATTRIBUTE);
        final int start = (this.height - (size - 1) * this.height / 16) / 2;
        final ButtonWidget resetButton = this.addButton(this.resetButton(this.resetAction(ATTRIBUTE)));

        resetButton.active = this.storage.getDatum(SPENT_ATTRIBUTE_POINTS) > 0;

        for (int index = 0; index < size; index++) {
            final ButtonWidget add = this.addButton(this.squareButton((this.width + 162) / 2 - 20, start + index * this.height / 16 + 4, "-", this.addPointAction(index)));
            final ButtonWidget remove = this.addButton(this.squareButton((this.width + 162) / 2, start + index * this.height / 16 + 4, "+", this.removePointAction(index)));
            final StatisticType attribute = this.getAttribute(index);

            add.active = this.storage.size(ATTRIBUTE) > 0;
            remove.active = this.storage.getStatistic(this.getAttribute(index)).aboveMin();

            if (attribute == MINING_LEVEL) {
                add.active &= this.storage.getAttribute(MINING_LEVEL) < 3;
            }
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME.asFormattedString());
        final String harvestLevel = String.format("%s%s: %%s (%s)", Mappings.HARVEST_LEVEL_FORMAT, Mappings.HARVEST_LEVEL_NAME.asFormattedString(), this.getToolStorage().getMiningLevel().asFormattedString());
        final String reachDistance = String.format("%s%s: %%s", Mappings.REACH_DISTANCE_FORMAT, Mappings.REACH_DISTANCE_NAME.asFormattedString());
        final int points = this.storage.getDatum(ATTRIBUTE_POINTS);

        if (points > 0) {
            TEXT_RENDERER.draw(String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.drawMiddleAttribute(efficiency, storage.getAttribute(EFFICIENCY), 0, 3);
        this.drawMiddleAttribute(reachDistance, storage.getAttribute(REACH), 1, 3);
        this.drawMiddleAttribute(harvestLevel, storage.getAttribute(MINING_LEVEL), 2, 3);
    }

    protected StatisticType getAttribute(final int index) {
        switch (index) {
            case 0:
                return EFFICIENCY;
            case 1:
                return REACH;
            case 2:
                return MINING_LEVEL;
            default:
                return null;
        }
    }
}
