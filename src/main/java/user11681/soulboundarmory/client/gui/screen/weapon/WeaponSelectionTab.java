package user11681.soulboundarmory.client.gui.screen.weapon;

import java.util.List;
import net.minecraft.text.Text;
import user11681.soulboundarmory.client.gui.screen.common.ScreenTab;
import user11681.soulboundarmory.client.gui.screen.common.SelectionTab;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;

public class WeaponSelectionTab extends SelectionTab {
    public WeaponSelectionTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
        super(component, tabs);
    }

    @Override
    protected Text getLabel() {
        return this.title;
    }
}
