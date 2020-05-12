package transfarmer.soulboundarmory.client.gui.screen.weapon;

import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SelectionTab;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.farmerlib.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.component.soulbound.weapon.WeaponProvider.WEAPONS;

public class WeaponSelectionTab extends SelectionTab {
    public WeaponSelectionTab(final List<ScreenTab> tabs) {
        super(WEAPONS, tabs, CollectionUtil.hashMap(new Integer[]{0, 1, 2, 3},
                Mappings.SOULBOUND_DAGGER_NAME,
                Mappings.SOULBOUND_SWORD_NAME,
                Mappings.SOULBOUND_GREATSWORD_NAME,
                Mappings.SOULBOUND_STAFF_NAME
        ));
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_SELECTION;
    }
}
