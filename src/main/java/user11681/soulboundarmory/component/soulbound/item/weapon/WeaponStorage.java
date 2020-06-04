package user11681.soulboundarmory.component.soulbound.item.weapon;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import user11681.soulboundarmory.client.gui.screen.tab.AttributeTab;
import user11681.soulboundarmory.client.gui.screen.tab.EnchantmentTab;
import user11681.soulboundarmory.client.gui.screen.tab.SelectionTab;
import user11681.soulboundarmory.client.gui.screen.tab.SkillTab;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponentBase;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundWeaponItem;
import user11681.usersmanual.client.gui.screen.ScreenTab;
import user11681.usersmanual.collections.CollectionUtil;

public abstract class WeaponStorage<T> extends ItemStorage<T> {
    public WeaponStorage(final SoulboundComponentBase component, final Item item) {
        super(component, item);
    }

    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = CollectionUtil.arrayList(new SelectionTab(Mappings.MENU_WEAPON_SELECTION, this.component, tabs), new AttributeTab(this.component, tabs), new EnchantmentTab(this.component, tabs), new SkillTab(this.component, tabs));

        return tabs;
    }

    public int getLevelXP(final int level) {
        return this.canLevelUp()
                ? Configuration.instance().initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundWeaponItem.class;
    }
}
