package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.ButtonWidget;
import net.minecraftforge.common.capabilities.Component;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.C2S.C2SItemType;

import java.util.List;
import java.util.Map;

public abstract class SelectionTab extends SoulboundTab {
    protected final Map<Integer, String> selection;

    public SelectionTab(final @Nonnull Component<? extends ISoulboundComponent> key, final List<ScreenTab> tabs,
                        final Map<Integer, String> selection) {
        super(key, tabs);

        this.selection = selection;
    }

    @Override
    public void init() {
        super.init();

        final int width = 128;
        final int height = 20;
        final int xCenter = (this.width - width) / 2;
        final int ySep = 32;

        for (int id = 0, size = this.selection.size(); id < size; id++) {
            if (!this.component.isUnlocked(id) && !this.component.canUnlock(id)) {
                this.selection.remove(id);
            }
        }

        final int top = (this.height - height - ySep * (this.selection.size() - 1)) / 2;
        int row = 0;

        for (final int id : this.selection.keySet()) {
            final ButtonWidget button = this.addButton(new ButtonWidget(id, xCenter, top + (row++ * ySep), width, height, this.selection.get(id)));

            if (this.component.hasSoulboundItem()) {
                button.active = id != this.component.getIndex();
            }
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!this.component.hasSoulboundItem()) {
            this.drawCenteredString(this.fontRenderer, this.getLabel(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(@Nonnull final ButtonWidget button) {
        super.actionPerformed(button);

        if (this.selection.containsKey(button.id)) {
            Main.CHANNEL.sendToServer(new C2SItemType(this.component.getType(), this.component.getItemType(button.id)));
        }
    }
}
