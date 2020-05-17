package transfarmer.soulboundarmory.client.gui.screen.common;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.network.Packets;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.util.List;

import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public abstract class AttributeTab extends SoulboundTab {
    public AttributeTab(final ISoulboundItemComponent<? extends Component> component,
                        final List<ScreenTab> tabs) {
        super(Mappings.MENU_BUTTON_ATTRIBUTES, component, tabs);
    }

    @Override
    protected Text getLabel() {
        return this.title;
    }

    protected abstract StatisticType getAttribute(final int index);

    protected PressAction addPointAction(final int index) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown()
                    ? this.component.getDatum(ATTRIBUTE_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ATTRIBUTE, new ExtendedPacketBuffer(this.component).writeString(this.getAttribute(index).toString()).writeInt(amount));
        };
    }

    protected PressAction removePointAction(final int index) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown()
                    ? this.component.getDatum(SPENT_ATTRIBUTE_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ATTRIBUTE, new ExtendedPacketBuffer(this.component).writeString(this.getAttribute(index).toString()).writeInt(-amount));
        };
    }
}
