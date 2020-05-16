package transfarmer.soulboundarmory.client.gui.screen.common;

import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.Packets;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

import java.util.List;

import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public abstract class AttributeTab extends SoulboundTab {
    public AttributeTab(final ComponentType<? extends ISoulboundComponent> componentType,
                        final List<ScreenTab> tabs) {
        super(Mappings.MENU_BUTTON_ATTRIBUTES.toString(), componentType, tabs);
    }

    @Override
    protected String getLabel() {
        return this.title.asFormattedString();
    }

    protected abstract StatisticType getAttribute(final int index);

    protected PressAction addPointAction(final int index) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown()
                    ? this.component.getDatum(this.item, ATTRIBUTE_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ATTRIBUTE, new ExtendedPacketBuffer(this.component).writeString(this.getAttribute(index).toString()).writeInt(amount));
        };
    }

    protected PressAction removePointAction(final int index) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown()
                    ? this.component.getDatum(this.item, SPENT_ATTRIBUTE_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ATTRIBUTE, new ExtendedPacketBuffer(this.component).writeString(this.getAttribute(index).toString()).writeInt(-amount));
        };
    }
}
