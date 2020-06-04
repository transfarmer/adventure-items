package user11681.soulboundarmory.component.soulbound.item.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import user11681.soulboundarmory.client.gui.screen.tab.AttributeTab;
import user11681.soulboundarmory.client.gui.screen.tab.EnchantmentTab;
import user11681.soulboundarmory.client.gui.screen.tab.SelectionTab;
import user11681.soulboundarmory.client.gui.screen.tab.SkillTab;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponentBase;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundToolItem;
import user11681.usersmanual.client.gui.screen.ScreenTab;

import static user11681.soulboundarmory.component.statistics.StatisticType.MINING_LEVEL;

public abstract class ToolStorage<T> extends ItemStorage<T> {
    public ToolStorage(final SoulboundComponentBase component, final Item item) {
        super(component, item);
    }

    public int getLevelXP(final int level) {
        return this.canLevelUp()
              ? Configuration.instance().initialToolXP + (int) Math.round(4 * Math.pow(level, 1.25))
                : -1;
    }

    public Text getMiningLevel() {
        return Mappings.getMiningLevels()[(int) this.getAttribute(MINING_LEVEL)];
    }

    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = Arrays.asList(new SelectionTab(Mappings.MENU_TOOL_SELECTION, this.component, tabs), new AttributeTab(this.component, tabs), new EnchantmentTab(this.component, tabs), new SkillTab(this.component, tabs));

        return tabs;
    }

    @Override
    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundToolItem.class;
    }
}
