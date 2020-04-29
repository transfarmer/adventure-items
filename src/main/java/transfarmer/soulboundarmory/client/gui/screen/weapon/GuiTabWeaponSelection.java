package transfarmer.soulboundarmory.client.gui.screen.weapon;

import net.minecraft.client.gui.GuiButton;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSoulbound;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.network.C2S.C2SItemType;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;
import java.util.Map;

import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;

public class GuiTabWeaponSelection extends GuiTabSoulbound {
    public GuiTabWeaponSelection(final List<GuiTab> tabs) {
        super(WEAPONS, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_SELECTION;
    }

    @Override
    public void initGui() {
        super.initGui();

        final int width = 128;
        final int height = 20;
        final int xCenter = (this.width - width) / 2;
        final int ySep = 32;

        final Map<Integer, String> labels = CollectionUtil.hashMap(new Integer[]{0, 1, 2, 3}, Mappings.SOUL_DAGGER_NAME, Mappings.SOUL_SWORD_NAME, Mappings.SOUL_GREATSWORD_NAME, Mappings.SOUL_STAFF_NAME);

        for (int id = 0, size = labels.size(); id < size; id++) {
            if (!this.capability.isUnlocked(id) && !this.capability.canUnlock(id)) {
                labels.remove(id);
            }
        }

        final int top = (this.height - height + ySep * (labels.size() - 1)) / 2;
        int row = 0;

        for (final int id : labels.keySet()) {
            final GuiButton button = this.addButton(new GuiButton(id, xCenter, top + (row++ * ySep), width, height, labels.get(id)));

            button.enabled = id != this.capability.getIndex();
        }
    }


    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!SoulItemHelper.hasSoulWeapon(this.mc.player)) {
            this.drawCenteredString(this.fontRenderer, Mappings.MENU_SELECTION,
                    Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(final @NotNull GuiButton button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
            case 1:
            case 2:
            case 3:
                Main.CHANNEL.sendToServer(new C2SItemType(this.capability.getType(), this.capability.getItemType(button.id)));
                break;
        }
    }
}
