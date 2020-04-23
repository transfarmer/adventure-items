package transfarmer.soulboundarmory.client.gui.screen.tool;

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

import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOLS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;

public class GuiTabToolAttributes extends GuiTabSoulbound {
    public GuiTabToolAttributes(final List<GuiTab> tabs) {
        super(TOOLS, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_ATTRIBUTES;
    }

    @Override
    public void initGui() {
        super.initGui();

        final GuiButton resetButton = this.addButton(this.guiFactory.resetButton(20));
        final GuiButton[] removePointButtons = this.addButtons(guiFactory.removePointButtons(23, this.capability.size(ATTRIBUTE)));
        final GuiButton[] addPointButtons = this.addButtons(this.guiFactory.addPointButtons(4, this.capability.size(ATTRIBUTE), this.capability.getDatum(this.item, ATTRIBUTE_POINTS)));
        resetButton.enabled = this.capability.getDatum(this.item, SPENT_ATTRIBUTE_POINTS) > 0;

        for (int index = 0; index < this.capability.size(ATTRIBUTE); index++) {
            final Statistic statistic = this.capability.getStatistic(this.item, this.getAttribute(index));

            removePointButtons[index].enabled = statistic.greaterThan(statistic.min());
        }

        addPointButtons[2].enabled &= this.capability.getAttribute(this.item, HARVEST_LEVEL) < 3;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final String harvestLevel = String.format("%s%s: %%s (%s)", Mappings.HARVEST_LEVEL_FORMAT, Mappings.HARVEST_LEVEL_NAME,
                Mappings.getMiningLevels()[(int) this.capability.getAttribute(this.item, HARVEST_LEVEL)]);
        final String reachDistance = String.format("%s%s: %%s", Mappings.REACH_DISTANCE_FORMAT, Mappings.REACH_DISTANCE_NAME);
        final int points = this.capability.getDatum(this.item, ATTRIBUTE_POINTS);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleAttribute(efficiency, capability.getAttribute(this.item, EFFICIENCY_ATTRIBUTE), 0);
        this.renderer.drawMiddleAttribute(reachDistance, capability.getAttribute(this.item, REACH_DISTANCE), 1);
        this.renderer.drawMiddleAttribute(harvestLevel, capability.getAttribute(this.item, HARVEST_LEVEL), 2);
    }

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
                return EFFICIENCY_ATTRIBUTE;
            case 1:
                return REACH_DISTANCE;
            case 2:
                return HARVEST_LEVEL;
            default:
                return null;
        }
    }
}
