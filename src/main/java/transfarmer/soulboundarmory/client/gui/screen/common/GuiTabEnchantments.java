package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.network.C2S.C2SEnchant;
import transfarmer.soulboundarmory.network.C2S.C2SReset;
import transfarmer.soulboundarmory.util.IndexedMap;

import java.util.List;

import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ENCHANTMENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;

public class GuiTabEnchantments extends GuiTabSoulbound {
    public GuiTabEnchantments(final Capability<? extends SoulboundCapability> key, final List<GuiTab> tabs) {
        super(key, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_ENCHANTMENTS;
    }

    @Override
    public void initGui() {
        super.initGui();

        final IndexedMap<Enchantment, Integer> enchantments = this.capability.getEnchantments(this.capability.getItemType());
        final int size = enchantments.size();
        final GuiButton resetButton = this.addButton(this.resetButton(21));
        final GuiButton[] removePointButtons = new GuiButton[size];

        for (int row = 0; row < size; row++) {
            removePointButtons[row] = this.addButton(this.squareButton(2000 + Enchantment.getEnchantmentID(enchantments.getKey(row)), (width + 162) / 2 - 20, this.getHeight(size, row), "-"));
        }

        resetButton.enabled = this.capability.getDatum(this.capability.getItemType(), SPENT_ENCHANTMENT_POINTS) > 0;

        for (int row = 0; row < size; row++) {
            final GuiButton button = this.addButton(this.squareButton(3000 + Enchantment.getEnchantmentID(enchantments.getKey(row)), (width + 162) / 2, this.getHeight(size, row), "+"));
            button.enabled = this.capability.getDatum(this.capability.getItemType(), ENCHANTMENT_POINTS) > 0;
        }

        for (int i = 0; i < size; i++) {
            removePointButtons[i].enabled = enchantments.getValue(i) > 0;
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final IndexedMap<Enchantment, Integer> enchantments = this.capability.getEnchantments();
        final int points = this.capability.getDatum(this.item, ENCHANTMENT_POINTS);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int i = 0, size = enchantments.size(); i < size; i++) {
            FONT_RENDERER.drawString(enchantments.getKey(i).getTranslatedName(enchantments.getValue(i)), (this.width - 182) / 2, this.getHeight(size, i) - FONT_RENDERER.FONT_HEIGHT / 2, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(final @NotNull GuiButton button) {
        super.actionPerformed(button);

        Enchantment enchantment = Enchantment.getEnchantmentByID(button.id - 3000);

        if (enchantment != null) {
            int amount = 1;

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                amount = this.capability.getDatum(this.item, ENCHANTMENT_POINTS);
            }

            Main.CHANNEL.sendToServer(new C2SEnchant(this.capability.getType(), this.item, enchantment, amount));
        } else if ((enchantment = Enchantment.getEnchantmentByID(button.id - 2000)) != null) {
            int amount = 1;

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                amount = this.capability.getDatum(this.item, SPENT_ENCHANTMENT_POINTS);
            }

            Main.CHANNEL.sendToServer(new C2SEnchant(this.capability.getType(), this.item, enchantment, -amount));
        } else if (button.id == 21) {
            Main.CHANNEL.sendToServer(new C2SReset(this.capability.getType(), this.item, ENCHANTMENT));
        }
    }
}
