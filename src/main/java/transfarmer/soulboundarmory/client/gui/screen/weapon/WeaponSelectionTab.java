package transfarmer.soulboundarmory.client.gui.screen.weapon;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.text.Text;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SelectionTab;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

import java.util.List;

public class WeaponSelectionTab extends SelectionTab {
    public WeaponSelectionTab(final ISoulboundItemComponent<? extends Component> component, final List<ScreenTab> tabs) {
        super(component, tabs, CollectionUtil.hashMap(CollectionUtil.toArray(component.getParent().getComponents()),
                Mappings.SOULBOUND_DAGGER_NAME,
                Mappings.SOULBOUND_SWORD_NAME,
                Mappings.SOULBOUND_GREATSWORD_NAME,
                Mappings.SOULBOUND_STAFF_NAME
        ));
    }

    @Override
    protected Text getLabel() {
        return this.title;
    }
}
