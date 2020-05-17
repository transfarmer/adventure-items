package transfarmer.soulboundarmory.client.gui.screen.tool;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.text.Text;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SelectionTab;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

import java.util.List;

public class ToolConfirmationTab extends SelectionTab {
    public ToolConfirmationTab(final ISoulboundItemComponent<? extends Component> component, final List<ScreenTab> tabs) {
        super(component, tabs, CollectionUtil.hashMap(component, Mappings.SOULBOUND_PICK_NAME));
    }

    @Override
    protected Text getLabel() {
        return Mappings.MENU_CONFIRMATION;
    }
}
