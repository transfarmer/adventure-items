package transfarmer.soulboundarmory.client.gui.screen.common;

import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import transfarmer.farmerlib.util.IndexedMap;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.Packets;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

import java.util.List;

import static transfarmer.soulboundarmory.statistics.Category.ENCHANTMENT;
import static transfarmer.soulboundarmory.statistics.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;

public class EnchantmentTab extends SoulboundTab {
    public EnchantmentTab(final ComponentType<? extends ISoulboundComponent> componentType,
                          final List<ScreenTab> tabs) {
        super(Mappings.MENU_BUTTON_ENCHANTMENTS.toString(), componentType, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_ENCHANTMENTS.toString();
    }

    @Override
    public void init() {
        super.init();

        final IndexedMap<Enchantment, Integer> enchantments = this.component.getEnchantments(this.component.getItemType());
        final int size = enchantments.size();
        final ButtonWidget resetButton = this.addButton(this.resetButton(this.resetAction(ENCHANTMENT)));
        final ButtonWidget[] removePointButtons = new ButtonWidget[size];

        for (int row = 0; row < size; row++) {
            removePointButtons[row] = this.addButton(this.squareButton((width + 162) / 2 - 20, this.getHeight(size, row), "-", this.enchantAction(enchantments.getKey(row))));
        }

        resetButton.active = this.component.getDatum(this.component.getItemType(), SPENT_ENCHANTMENT_POINTS) > 0;

        for (int row = 0; row < size; row++) {
            final ButtonWidget button = this.addButton(this.squareButton((width + 162) / 2, this.getHeight(size, row), "+", this.disenchantAction(enchantments.getKey(row))));
            button.active = this.component.getDatum(this.component.getItemType(), ENCHANTMENT_POINTS) > 0;
        }

        for (int i = 0; i < size; i++) {
            removePointButtons[i].active = enchantments.getValue(i) > 0;
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final IndexedMap<Enchantment, Integer> enchantments = this.component.getEnchantments();
        final int points = this.component.getDatum(this.item, ENCHANTMENT_POINTS);

        if (points > 0) {
            TEXT_RENDERER.draw(String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int i = 0, size = enchantments.size(); i < size; i++) {
            TEXT_RENDERER.draw(enchantments.getKey(i).getName(enchantments.getValue(i)).asFormattedString(), (this.width - 182) / 2F, this.getHeight(size, i) - TEXT_RENDERER.fontHeight / 2F, 0xFFFFFF);
        }
    }

    protected PressAction enchantAction(final Enchantment enchantment) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown() ?
                    this.component.getDatum(this.item, ENCHANTMENT_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ENCHANT, new ExtendedPacketBuffer(this.component).writeIdentifier(Registry.ENCHANTMENT.getId(enchantment)).writeInt(amount));
        };
    }

    protected PressAction disenchantAction(final Enchantment enchantment) {
        return (final ButtonWidget button) -> {
            final int amount = hasShiftDown()
                    ? this.component.getDatum(this.item, SPENT_ENCHANTMENT_POINTS)
                    : 1;

            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_ENCHANT, new ExtendedPacketBuffer(this.component).writeIdentifier(Registry.ENCHANTMENT.getId(enchantment)).writeInt(-amount));
        };
    }
}
