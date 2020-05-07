package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.network.C2S.C2SItemType;

import java.util.List;
import java.util.Map;

public abstract class GuiTabSelection extends GuiTabSoulbound {
    protected final Map<Integer, String> options;

    public GuiTabSelection(final @NotNull Capability<? extends SoulboundCapability> key, final List<GuiTab> tabs,
                           final Map<Integer, String> options) {
        super(key, tabs);

        this.options = options;
    }

    @Override
    public void initGui() {
        super.initGui();

        final int width = 128;
        final int height = 20;
        final int xCenter = (this.width - width) / 2;
        final int ySep = 32;

        for (int id = 0, size = this.options.size(); id < size; id++) {
            if (!this.capability.isUnlocked(id) && !this.capability.canUnlock(id)) {
                this.options.remove(id);
            }
        }

        final int top = (this.height - height - ySep * (this.options.size() - 1)) / 2;
        int row = 0;

        for (final int id : this.options.keySet()) {
            final GuiButton button = this.addButton(new GuiButton(id, xCenter, top + (row++ * ySep), width, height, this.options.get(id)));

            if (this.capability.hasSoulboundItem()) {
                button.enabled = id != this.capability.getIndex();
            }
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!this.capability.hasSoulboundItem()) {
            this.drawCenteredString(this.fontRenderer, this.getLabel(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(@NotNull final GuiButton button) {
        super.actionPerformed(button);

        if (this.options.containsKey(button.id)) {
            Main.CHANNEL.sendToServer(new C2SItemType(this.capability.getType(), this.capability.getItemType(button.id)));
        }
    }
}
