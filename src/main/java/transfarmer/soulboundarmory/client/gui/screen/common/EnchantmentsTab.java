package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.ButtonWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.network.C2S.C2SEnchant;
import transfarmer.soulboundarmory.network.C2S.C2SReset;
import transfarmer.farmerlib.util.IndexedMap;

import java.util.List;

import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ENCHANTMENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;

public class EnchantmentsTab extends SoulboundTab {
    public EnchantmentsTab(final Capability<? extends ISoulboundComponent> key, final List<ScreenTab> tabs) {
        super(key, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_ENCHANTMENTS;
    }

    @Override
    public void init() {
        super.init();

        final IndexedMap<Enchantment, Integer> enchantments = this.component.getEnchantments(this.component.getItemType());
        final int size = enchantments.size();
        final ButtonWidget resetButton = this.addButton(this.resetButton(21));
        final ButtonWidget[] removePointButtons = new ButtonWidget[size];

        for (int row = 0; row < size; row++) {
            removePointButtons[row] = this.addButton(this.squareButton(2000 + Enchantment.getEnchantmentID(enchantments.getKey(row)), (width + 162) / 2 - 20, this.getHeight(size, row), "-"));
        }

        resetButton.enabled = this.component.getDatum(this.component.getItemType(), SPENT_ENCHANTMENT_POINTS) > 0;

        for (int row = 0; row < size; row++) {
            final ButtonWidget button = this.addButton(this.squareButton(3000 + Enchantment.getEnchantmentID(enchantments.getKey(row)), (width + 162) / 2, this.getHeight(size, row), "+"));
            button.enabled = this.component.getDatum(this.component.getItemType(), ENCHANTMENT_POINTS) > 0;
        }

        for (int i = 0; i < size; i++) {
            removePointButtons[i].enabled = enchantments.getValue(i) > 0;
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final IndexedMap<Enchantment, Integer> enchantments = this.component.getEnchantments();
        final int points = this.component.getDatum(this.item, ENCHANTMENT_POINTS);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int i = 0, size = enchantments.size(); i < size; i++) {
            TEXT_RENDERER.drawString(enchantments.getKey(i).getTranslatedName(enchantments.getValue(i)), (this.width - 182) / 2, this.getHeight(size, i) - TEXT_RENDERER.FONT_HEIGHT / 2, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(final @NotNull ButtonWidget button) {
        super.actionPerformed(button);

        Enchantment enchantment = Enchantment.getEnchantmentByID(button.id - 3000);

        if (enchantment != null) {
            int amount = 1;

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                amount = this.component.getDatum(this.item, ENCHANTMENT_POINTS);
            }

            Main.CHANNEL.sendToServer(new C2SEnchant(this.component.getType(), this.item, enchantment, amount));
        } else if ((enchantment = Enchantment.getEnchantmentByID(button.id - 2000)) != null) {
            int amount = 1;

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                amount = this.component.getDatum(this.item, SPENT_ENCHANTMENT_POINTS);
            }

            Main.CHANNEL.sendToServer(new C2SEnchant(this.component.getType(), this.item, enchantment, -amount));
        } else if (button.id == 21) {
            Main.CHANNEL.sendToServer(new C2SReset(this.component.getType(), this.item, ENCHANTMENT));
        }
    }
}
