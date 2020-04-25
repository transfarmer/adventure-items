package transfarmer.soulboundarmory.client.gui.screen.weapon;

import net.minecraft.client.gui.GuiButton;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSoulbound;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.network.server.C2SAttribute;
import transfarmer.soulboundarmory.network.server.C2SReset;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import java.util.List;

import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.CRITICAL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;

public class GuiTabWeaponAttributes extends GuiTabSoulbound {
    public GuiTabWeaponAttributes(final List<GuiTab> tabs) {
        super(WEAPONS, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_ATTRIBUTES;
    }

    @Override
    public void initGui() {
        super.initGui();

        final int size = this.capability.size(ATTRIBUTE) - 1;
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(20));
        final GuiButton[] addPointButtons = this.addButtons(this.guiFactory.addPointButtons(4, size, this.capability.getDatum(this.item, ATTRIBUTE_POINTS)));
        final GuiButton[] removePointButtons = this.addButtons(guiFactory.removePointButtons(23, size));
        resetButton.enabled = this.capability.getDatum(this.item, SPENT_ATTRIBUTE_POINTS) > 0;

        addPointButtons[2].enabled &= this.capability.getAttribute(this.item, CRITICAL) < 1;

        for (int index = 0; index < size; index++) {
            final Statistic statistic = this.capability.getStatistic(this.item, this.getAttribute(index));

            removePointButtons[index].enabled = statistic.greaterThan(statistic.min());
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final String attackSpeed = String.format("%s%s: %%s", Mappings.ATTACK_SPEED_FORMAT, Mappings.ATTACK_SPEED_NAME);
        final String attackDamage = String.format("%s%s: %%s", Mappings.ATTACK_DAMAGE_FORMAT, Mappings.ATTACK_DAMAGE_NAME);
        final String critical = String.format("%s%s: %%s%%%%", Mappings.CRITICAL_FORMAT, Mappings.CRITICAL_NAME);
        final String knockback = String.format("%s%s: %%s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, Mappings.KNOCKBACK_ATTRIBUTE_NAME);
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final int points = this.capability.getDatum(this.item, ATTRIBUTE_POINTS);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleAttribute(attackSpeed, capability.getAttribute(this.item, ATTACK_SPEED), 0);
        this.renderer.drawMiddleAttribute(attackDamage, capability.getAttributeTotal(this.item, ATTACK_DAMAGE), 1);
        this.renderer.drawMiddleAttribute(critical, capability.getAttribute(this.item, CRITICAL) * 100, 2);
        this.renderer.drawMiddleAttribute(knockback, capability.getAttribute(this.item, KNOCKBACK_ATTRIBUTE), 3);
        this.renderer.drawMiddleAttribute(efficiency, capability.getAttribute(this.item, EFFICIENCY_ATTRIBUTE), 4);
    }

    @Override
    public void actionPerformed(final @NotNull GuiButton button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                int amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(this.item, ATTRIBUTE_POINTS);
                }

                Main.CHANNEL.sendToServer(new C2SAttribute(this.capability.getType(), this.item, this.getAttribute(button.id - 4), amount));
                break;
            case 20:
                Main.CHANNEL.sendToServer(new C2SReset(this.capability.getType(), this.item, ATTRIBUTE));
                break;
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(this.item, SPENT_ATTRIBUTE_POINTS);
                }

                Main.CHANNEL.sendToServer(new C2SAttribute(this.capability.getType(), this.item, this.getAttribute(button.id - 23), -amount));
                break;
        }
    }

    protected IStatistic getAttribute(final int index) {
        switch (index) {
            case 0:
                return ATTACK_SPEED;
            case 1:
                return ATTACK_DAMAGE;
            case 2:
                return CRITICAL;
            case 3:
                return KNOCKBACK_ATTRIBUTE;
            case 4:
                return EFFICIENCY_ATTRIBUTE;
            default:
                return null;
        }
    }
}
