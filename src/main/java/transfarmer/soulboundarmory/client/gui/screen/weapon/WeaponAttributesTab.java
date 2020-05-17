package transfarmer.soulboundarmory.client.gui.screen.weapon;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.client.gui.widget.ButtonWidget;
import transfarmer.soulboundarmory.client.gui.screen.common.AttributeTab;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.util.List;

import static transfarmer.soulboundarmory.statistics.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static transfarmer.soulboundarmory.statistics.StatisticType.EFFICIENCY;
import static transfarmer.soulboundarmory.statistics.StatisticType.KNOCKBACK;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public class WeaponAttributesTab extends AttributeTab {
    public WeaponAttributesTab(final ISoulboundItemComponent<? extends Component> component,
                               final List<ScreenTab> tabs) {
        super(component, tabs);
    }

    @Override
    public void init() {
        super.init();

        final int size = this.component.size(ATTRIBUTE) - 1;
        final int start = (this.height - (size - 1) * this.height / 16) / 2;
        final ButtonWidget resetButton = this.addButton(this.resetButton(this.resetAction(ATTRIBUTE)));

        resetButton.active = this.component.getDatum(SPENT_ATTRIBUTE_POINTS) > 0;

        for (int index = 0; index < size; index++) {
            final ButtonWidget add = this.addButton(squareButton((this.width + 162) / 2, start + index * this.height / 16 + 4, "+", this.addPointAction(index)));
            final ButtonWidget remove = this.addButton(this.squareButton((this.width + 162) / 2 - 20, start + index * this.height / 16 + 4, "-", this.removePointAction(index)));
            final StatisticType attribute = this.getAttribute(index);

            add.active = this.component.getDatum(ATTRIBUTE_POINTS) > 0;
            remove.active = this.component.getStatistic(attribute).aboveMin();

            if (attribute == CRITICAL_STRIKE_PROBABILITY) {
                add.active &= this.component.getAttribute(CRITICAL_STRIKE_PROBABILITY) < 1;
            }
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final String attackSpeed = String.format("%s%s: %%s", Mappings.ATTACK_SPEED_FORMAT, Mappings.ATTACK_SPEED_NAME);
        final String attackDamage = String.format("%s%s: %%s", Mappings.ATTACK_DAMAGE_FORMAT, Mappings.ATTACK_DAMAGE_NAME);
        final String critical = String.format("%s%s: %%s%%%%", Mappings.CRITICAL_FORMAT, Mappings.CRITICAL_NAME);
        final String knockback = String.format("%s%s: %%s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, Mappings.KNOCKBACK_ATTRIBUTE_NAME);
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final int points = this.component.getDatum(ATTRIBUTE_POINTS);

        if (points > 0) {
            TEXT_RENDERER.draw(String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.drawMiddleAttribute(attackSpeed, component.getAttribute(ATTACK_SPEED), 0, 5);
        this.drawMiddleAttribute(attackDamage, component.getAttributeTotal(ATTACK_DAMAGE), 1, 5);
        this.drawMiddleAttribute(critical, component.getAttribute(CRITICAL_STRIKE_PROBABILITY) * 100, 2, 5);
        this.drawMiddleAttribute(knockback, component.getAttribute(KNOCKBACK), 3, 5);
        this.drawMiddleAttribute(efficiency, component.getAttribute(EFFICIENCY), 4, 5);
    }

    protected StatisticType getAttribute(final int index) {
        switch (index) {
            case 0:
                return ATTACK_SPEED;
            case 1:
                return ATTACK_DAMAGE;
            case 2:
                return CRITICAL_STRIKE_PROBABILITY;
            case 3:
                return KNOCKBACK;
            case 4:
                return EFFICIENCY;
            default:
                return null;
        }
    }
}
