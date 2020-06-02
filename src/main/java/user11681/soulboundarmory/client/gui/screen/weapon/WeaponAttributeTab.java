package user11681.soulboundarmory.client.gui.screen.weapon;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import user11681.soulboundarmory.client.gui.screen.common.AttributeTab;
import user11681.soulboundarmory.client.gui.screen.common.ScreenTab;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.StatisticType;

import static user11681.soulboundarmory.component.statistics.Category.ATTRIBUTE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_SPEED;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static user11681.soulboundarmory.component.statistics.StatisticType.EFFICIENCY;
import static user11681.soulboundarmory.component.statistics.StatisticType.KNOCKBACK;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public class WeaponAttributeTab extends AttributeTab {
    public WeaponAttributeTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
        super(component, tabs);
    }

    @Override
    public void init() {
        super.init();

        final int size = this.storage.size(ATTRIBUTE) - 1;
        final int start = (this.height - (size - 1) * this.height / 16) / 2;
        final ButtonWidget resetButton = this.addButton(this.resetButton(this.resetAction(ATTRIBUTE)));

        resetButton.active = this.storage.getDatum(SPENT_ATTRIBUTE_POINTS) > 0;

        for (int index = 0; index < size; index++) {
            final ButtonWidget add = this.addButton(squareButton((this.width + 162) / 2, start + index * this.height / 16 + 4, "+", this.addPointAction(index)));
            final ButtonWidget remove = this.addButton(this.squareButton((this.width + 162) / 2 - 20, start + index * this.height / 16 + 4, "-", this.removePointAction(index)));
            final StatisticType attribute = this.getAttribute(index);

            add.active = this.storage.getDatum(ATTRIBUTE_POINTS) > 0;
            remove.active = this.storage.getStatistic(attribute).aboveMin();

            if (attribute == CRITICAL_STRIKE_PROBABILITY) {
                add.active &= this.storage.getAttribute(CRITICAL_STRIKE_PROBABILITY) < 1;
            }
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final String attackSpeed = String.format("%s%s: %%s", Mappings.ATTACK_SPEED_FORMAT, Mappings.ATTACK_SPEED_NAME.asFormattedString());
        final String attackDamage = String.format("%s%s: %%s", Mappings.ATTACK_DAMAGE_FORMAT, Mappings.ATTACK_DAMAGE_NAME.asFormattedString());
        final String critical = String.format("%s%s: %%s%%%%", Mappings.CRITICAL_FORMAT, Mappings.CRITICAL_NAME.asFormattedString());
        final String knockback = String.format("%s%s: %%s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, Mappings.KNOCKBACK_ATTRIBUTE_NAME.asFormattedString());
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME.asFormattedString());
        final int points = this.storage.getDatum(ATTRIBUTE_POINTS);

        if (points > 0) {
            TEXT_RENDERER.draw(String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.drawMiddleAttribute(attackSpeed, storage.getAttribute(ATTACK_SPEED), 0, 5);
        this.drawMiddleAttribute(attackDamage, storage.getAttributeTotal(ATTACK_DAMAGE), 1, 5);
        this.drawMiddleAttribute(critical, storage.getAttribute(CRITICAL_STRIKE_PROBABILITY) * 100, 2, 5);
        this.drawMiddleAttribute(knockback, storage.getAttribute(KNOCKBACK), 3, 5);
        this.drawMiddleAttribute(efficiency, storage.getAttribute(EFFICIENCY), 4, 5);
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
