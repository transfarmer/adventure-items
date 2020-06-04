package user11681.soulboundarmory.client.gui.screen.tab;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponentBase;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.usersmanual.client.gui.screen.ScreenTab;

public class SelectionTab extends SoulboundTab {
    public SelectionTab(final Text title, final SoulboundComponentBase component, final List<ScreenTab> tabs) {
        super(title, component, tabs);
    }

    @Override
    public void init() {
        super.init();

        final int width = 128;
        final int height = 20;
        final int centerX = (this.width - width) / 2;
        final int ySep = 32;

        final List<ItemStorage<?>> selection = this.component.getStorages().values();

        selection.removeIf((final ItemStorage<?> storage) -> !this.storage.isUnlocked() && !storage.canUnlock());

        final int top = (this.height - height - ySep * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            final ItemStorage<?> storage = selection.get(row);

            final Identifier identifier = storage.getType().getIdentifier();
            final ButtonWidget button = this.addButton(new ButtonWidget(centerX, top + (row * ySep), width, height, storage.getName().asFormattedString(), this.selectAction(storage)));

            if (this.displayTabs()) {
                button.active = identifier != this.component.getAnyHeldItemStorage().getType().getIdentifier();
            }
        }
    }

    private PressAction selectAction(final ItemStorage<?> component) {
        return (final ButtonWidget button) -> MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ITEM_TYPE, new ExtendedPacketBuffer(component));
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!this.storage.isItemEquipped()) {
            this.font.draw(this.getLabel().asFormattedString(), this.width / 2F, 40, 0xFFFFFF);
        }
    }
}
