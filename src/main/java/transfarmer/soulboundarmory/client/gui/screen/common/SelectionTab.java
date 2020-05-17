package transfarmer.soulboundarmory.client.gui.screen.common;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.network.Packets;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public abstract class SelectionTab extends SoulboundTab {
    protected final Map<ISoulboundItemComponent<? extends Component>, Text> selection;

    public SelectionTab(final @Nonnull ISoulboundItemComponent<? extends Component> component,
                        final List<ScreenTab> tabs,
                        final Map<ISoulboundItemComponent<? extends Component>, Text> selection) {
        super(Mappings.MENU_SELECTION, component, tabs);

        this.selection = selection;
    }

    @Override
    public void init() {
        super.init();

        final int width = 128;
        final int height = 20;
        final int centerX = (this.width - width) / 2;
        final int ySep = 32;

        for (final ISoulboundItemComponent<? extends Component> component : this.selection.keySet()) {
            if (!this.component.isUnlocked() && !component.canUnlock()) {
                this.selection.remove(component);
            }
        }

        final int top = (this.height - height - ySep * (this.selection.size() - 1)) / 2;
        int n = 0;

        for (final ISoulboundItemComponent<? extends Component> component : this.selection.keySet()) {
            final Identifier identifier = component.getComponentType().getId();
            final ButtonWidget button = this.addButton(new ButtonWidget(centerX, top + (n++ * ySep), width, height, this.selection.get(component).asFormattedString(), this.selectAction(component)));

            if (this.displayTabs) {
                button.active = identifier != this.component.getComponentType().getId();
            }
        }
    }

    private PressAction selectAction(final ISoulboundItemComponent<? extends Component> component) {
        return (final ButtonWidget button) -> {
            if (this.selection.containsKey(component)) {
                MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ITEM_TYPE, new ExtendedPacketBuffer(component));
            }
        };
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!this.component.isItemEquipped()) {
            this.font.draw(this.getLabel().asFormattedString(), this.width / 2F, 40, 0xFFFFFF);
        }
    }
}
