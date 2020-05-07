package transfarmer.soulboundarmory.client.gui.screen.weapon;

import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSelection;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;

public class GuiTabWeaponSelection extends GuiTabSelection {
    public GuiTabWeaponSelection(final List<GuiTab> tabs) {
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
