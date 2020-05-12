package transfarmer.soulboundarmory.client.gui.screen.tool;

import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SelectionTab;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.farmerlib.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.component.soulbound.tool.ToolProvider.TOOLS;

public class ToolConfirmationTab extends SelectionTab {
    public ToolConfirmationTab(final List<ScreenTab> tabs) {
        super(TOOLS, tabs, CollectionUtil.hashMap(0, Mappings.SOULBOUND_PICK_NAME));
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_CONFIRMATION;
    }
}
