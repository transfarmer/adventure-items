package user11681.soulboundarmory.client.gui.screen.tab;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponentBase;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.usersmanual.client.gui.screen.ScreenTab;
import user11681.usersmanual.collections.ArrayMap;

import static user11681.soulboundarmory.component.statistics.Category.ENCHANTMENT;
import static user11681.soulboundarmory.component.statistics.StatisticType.ENCHANTMENT_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab(final SoulboundComponentBase component, final List<ScreenTab> tabs) {
        super(Mappings.MENU_BUTTON_ENCHANTMENTS, component, tabs);
    }

    @Override
    public void init() {
        super.init();

        final ItemStorage<?> storage = this.storage;
        final ArrayMap<Enchantment, Integer> enchantments = storage.getEnchantments();
        final int size = enchantments.size();
        final ButtonWidget resetButton = this.addButton(this.resetButton(this.resetAction(ENCHANTMENT)));
        resetButton.active = storage.getDatum(SPENT_ENCHANTMENT_POINTS) > 0;

        for (int row = 0; row < size; row++) {
            final ButtonWidget addButton = this.addButton(this.squareButton((width + 162) / 2 - 20, this.getHeight(size, row), "-", this.enchantAction(enchantments.getKey(row))));
            final ButtonWidget removeButton = this.addButton(this.squareButton((width + 162) / 2, this.getHeight(size, row), "+", this.disenchantAction(enchantments.getKey(row))));
            addButton.active = enchantments.get(row) > 0;
            removeButton.active = storage.getDatum(ENCHANTMENT_POINTS) > 0;
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final ArrayMap<Enchantment, Integer> enchantments = this.storage.getEnchantments();
        final int points = this.storage.getDatum(ENCHANTMENT_POINTS);

        if (points > 0) {
            TEXT_RENDERER.draw(String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS.asFormattedString(), points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int row = 0, size = enchantments.size(); row < size; row++) {
            TEXT_RENDERER.draw(enchantments.getKey(row).getName(enchantments.get(row)).asFormattedString(), (this.width - 182) / 2F, this.getHeight(size, row) - TEXT_RENDERER.fontHeight / 2F, 0xFFFFFF);
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
