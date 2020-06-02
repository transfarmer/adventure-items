package user11681.soulboundarmory.client.gui.screen.common;

import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.network.Packets;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

public abstract class SelectionTab extends SoulboundTab {
    public SelectionTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
        super(Mappings.MENU_SELECTION, component, tabs);
    }

    @Override
    public void init() {
        super.init();

        final int width = 128;
        final int height = 20;
        final int centerX = (this.width - width) / 2;
        final int ySep = 32;

        final Collection<ItemStorage<?>> selection = this.component.getStorages().values();

        selection.removeIf(component -> !storage.isUnlocked() && !component.canUnlock());

        final int top = (this.height - height - ySep * (selection.size() - 1)) / 2;
        int n = 0;

        for (final ItemStorage<?> storage : selection) {
            final Identifier identifier = storage.getType().getIdentifier();
            final ButtonWidget button = this.addButton(new ButtonWidget(centerX, top + (n++ * ySep), width, height, storage.getName().asFormattedString(), this.selectAction(storage)));

            if (this.displayTabs) {
                button.active = identifier != this.component.getComponentType().getId();
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
