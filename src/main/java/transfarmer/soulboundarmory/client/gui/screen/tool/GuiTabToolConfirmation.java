package transfarmer.soulboundarmory.client.gui.screen.tool;

import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSelection;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOLS;

public class GuiTabToolConfirmation extends GuiTabSelection {
    public GuiTabToolConfirmation(final List<GuiTab> tabs) {
        super(TOOLS, tabs, CollectionUtil.hashMap(0, Mappings.SOULBOUND_PICK_NAME));
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_CONFIRMATION;
    }
}
