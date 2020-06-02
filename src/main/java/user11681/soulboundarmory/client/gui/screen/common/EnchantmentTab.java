package user11681.soulboundarmory.client.gui.screen.common;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.network.Packets;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.usersmanual.collections.ArrayMap;

import static user11681.soulboundarmory.component.statistics.Category.ENCHANTMENT;
import static user11681.soulboundarmory.component.statistics.StatisticType.ENCHANTMENT_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
        super(Mappings.MENU_BUTTON_ENCHANTMENTS, component, tabs);
    }

    @Override
    protected Text getLabel() {
        return Mappings.MENU_BUTTON_ENCHANTMENTS;
    }

    @Override
    public void init() {
        super.init();

        final ArrayMap<Enchantment, Integer> enchantments = this.storage.getEnchantments();
        final int size = enchantments.size();
        final ButtonWidget resetButton = this.addButton(this.resetButton(this.resetAction(ENCHANTMENT)));
        final ButtonWidget[] removePointButtons = new ButtonWidget[size];

        for (int row = 0; row < size; row++) {
            removePointButtons[row] = this.addButton(this.squareButton((width + 162) / 2 - 20, this.getHeight(size, row), "-", this.enchantAction(enchantments.getKey(row))));
        }

        resetButton.active = this.storage.getDatum(SPENT_ENCHANTMENT_POINTS) > 0;

        for (int row = 0; row < size; row++) {
            final ButtonWidget button = this.addButton(this.squareButton((width + 162) / 2, this.getHeight(size, row), "+", this.disenchantAction(enchantments.getKey(row))));
            button.active = this.storage.getDatum(ENCHANTMENT_POINTS) > 0;
        }

        for (int i = 0; i < size; i++) {
            removePointButtons[i].active = enchantments.get(i) > 0;
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final ArrayMap<Enchantment, Integer> enchantments = this.storage.getEnchantments();
        final int points = this.storage.getDatum(ENCHANTMENT_POINTS);

        if (points > 0) {
            TEXT_RENDERER.draw(String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int i = 0, size = enchantments.size(); i < size; i++) {
            TEXT_RENDERER.draw(enchantments.getKey(i).getName(enchantments.get(i)).asFormattedString(), (this.width - 182) / 2F, this.getHeight(size, i) - TEXT_RENDERER.fontHeight / 2F, 0xFFFFFF);
        }
    }

    protected PressAction enchantAction(final Enchantment enchantment) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown() ?
                    this.storage.getDatum(ENCHANTMENT_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ENCHANT, new ExtendedPacketBuffer(this.storage).writeIdentifier(Registry.ENCHANTMENT.getId(enchantment)).writeInt(amount));
        };
    }

    protected PressAction disenchantAction(final Enchantment enchantment) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown()
                    ? this.storage.getDatum(SPENT_ENCHANTMENT_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ENCHANT, new ExtendedPacketBuffer(this.storage).writeIdentifier(Registry.ENCHANTMENT.getId(enchantment)).writeInt(-amount));
        };
    }
}
