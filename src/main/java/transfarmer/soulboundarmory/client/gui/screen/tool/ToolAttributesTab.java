package transfarmer.soulboundarmory.client.gui.screen.tool;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.client.gui.widget.ButtonWidget;
import transfarmer.soulboundarmory.client.gui.screen.common.AttributeTab;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.util.List;

import static transfarmer.soulboundarmory.statistics.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.EFFICIENCY;
import static transfarmer.soulboundarmory.statistics.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.REACH;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public class ToolAttributesTab extends AttributeTab {
    public ToolAttributesTab(final ISoulboundItemComponent<? extends Component> component, final List<ScreenTab> tabs) {
        super(component, tabs);
    }

    @Override
    public void init() {
        super.init();

        final int size = this.component.size(ATTRIBUTE);
        final int start = (this.height - (size - 1) * this.height / 16) / 2;
        final ButtonWidget resetButton = this.addButton(this.resetButton(this.resetAction(ATTRIBUTE)));

        resetButton.active = this.component.getDatum(SPENT_ATTRIBUTE_POINTS) > 0;

        for (int index = 0; index < size; index++) {
            final ButtonWidget add = this.addButton(this.squareButton((this.width + 162) / 2 - 20, start + index * this.height / 16 + 4, "-", this.addPointAction(index)));
            final ButtonWidget remove = this.addButton(this.squareButton((this.width + 162) / 2, start + index * this.height / 16 + 4, "+", this.removePointAction(index)));
            final StatisticType attribute = this.getAttribute(index);

            add.active = this.component.size(ATTRIBUTE) > 0;
            remove.active = this.component.getStatistic(this.getAttribute(index)).aboveMin();

            if (attribute == HARVEST_LEVEL) {
                add.active &= this.component.getAttribute(HARVEST_LEVEL) < 3;
            }
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final String harvestLevel = String.format("%s%s: %%s (%s)", Mappings.HARVEST_LEVEL_FORMAT, Mappings.HARVEST_LEVEL_NAME,
                Mappings.getMiningLevels()[(int) this.component.getAttribute(HARVEST_LEVEL)]);
        final String reachDistance = String.format("%s%s: %%s", Mappings.REACH_DISTANCE_FORMAT, Mappings.REACH_DISTANCE_NAME);
        final int points = this.component.getDatum(ATTRIBUTE_POINTS);

        if (points > 0) {
            TEXT_RENDERER.draw(String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.drawMiddleAttribute(efficiency, component.getAttribute(EFFICIENCY), 0, 3);
        this.drawMiddleAttribute(reachDistance, component.getAttribute(REACH), 1, 3);
        this.drawMiddleAttribute(harvestLevel, component.getAttribute(HARVEST_LEVEL), 2, 3);
    }

    protected StatisticType getAttribute(final int index) {
        switch (index) {
            case 0:
                return EFFICIENCY;
            case 1:
                return REACH;
            case 2:
                return HARVEST_LEVEL;
            default:
                return null;
        }
    }
}
