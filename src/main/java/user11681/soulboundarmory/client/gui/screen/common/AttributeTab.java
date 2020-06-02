package user11681.soulboundarmory.client.gui.screen.common;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.network.Packets;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public abstract class AttributeTab extends SoulboundTab {
    public AttributeTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
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
                    ? this.storage.getDatum(ATTRIBUTE_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ATTRIBUTE, new ExtendedPacketBuffer(this.storage).writeString(this.getAttribute(index).toString()).writeInt(amount));
        };
    }

    protected PressAction removePointAction(final int index) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown()
                    ? this.storage.getDatum(SPENT_ATTRIBUTE_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ATTRIBUTE, new ExtendedPacketBuffer(this.storage).writeString(this.getAttribute(index).toString()).writeInt(-amount));
        };
    }
}
