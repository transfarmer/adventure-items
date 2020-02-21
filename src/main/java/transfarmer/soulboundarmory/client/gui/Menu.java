package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.client.KeyBindings;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.statistics.IType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static transfarmer.soulboundarmory.Main.ResourceLocations.Client.XP_BAR;
import static transfarmer.soulboundarmory.statistics.SoulDatum.LEVEL;
import static transfarmer.soulboundarmory.statistics.SoulDatum.XP;

public abstract class Menu extends GuiScreen {
    protected GuiButton[] tabs;
    protected GUIFactory guiFactory;
    protected Renderer RENDERER;
    protected ISoulCapability capability;
    protected IType type;

    protected GuiButton[] addAddPointButtons(final int id, final int rows, final int points) {
        final GuiButton[] buttons = new GuiButton[rows];

        for (int row = 0; row < rows; row++) {
            buttons[row] = addButton(guiFactory.addSquareButton(id + row, (width + 162) / 2, (row + 1) * height / 16 + 4, "+"));
            buttons[row].enabled = points > 0;
        }

        return buttons;
    }

    protected GuiButton[] addRemovePointButtons(final int id, final int rows) {
        final GuiButton[] buttons = new GuiButton[rows];

        for (int row = 0; row < rows; row++) {
            buttons[row] = this.addButton(guiFactory.addSquareButton(id + row, (width + 162) / 2 - 20, (row + 1) * height / 16 + 4, "-"));
        }

        return buttons;
    }

    protected void drawXPBar(int mouseX, int mouseY) {
        final int barLeftX = (width - 182) / 2;
        final int barTopY = (height - 4) / 2;

        GlStateManager.color(1F, 1F, 1F, 1F);
        this.mc.getTextureManager().bindTexture(XP_BAR);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 40, 182, 5);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 45, Math.min(182, Math.round((float) capability.getDatum(XP, this.type) / capability.getNextLevelXP(this.type) * 182)), 5);
        this.mc.getTextureManager().deleteTexture(XP_BAR);

        final int level = this.capability.getDatum(LEVEL, this.type);
        final String levelString = String.format("%d", level);
        final int levelLeftX = Math.round((width - this.fontRenderer.getStringWidth(levelString)) / 2F) + 1;
        final int levelTopY = height / 2 - 8;
        this.fontRenderer.drawString(levelString, levelLeftX + 1, levelTopY, 0);
        this.fontRenderer.drawString(levelString, levelLeftX - 1, levelTopY, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY + 1, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY - 1, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY, 0xEC00B8);

        if (mouseX >= levelLeftX && mouseX <= levelLeftX + this.fontRenderer.getStringWidth(levelString)
                && mouseY >= levelTopY && mouseY <= levelTopY + this.fontRenderer.FONT_HEIGHT) {
            this.drawHoveringText(String.format("%d/%d", capability.getDatum(LEVEL, this.type), Configuration.maxLevel), mouseX, mouseY);
        } else if (mouseX >= (width - 182) / 2 && mouseX <= barLeftX + 182 && mouseY >= barTopY && mouseY <= barTopY + 4) {
            final String string = this.capability.getDatum(LEVEL, this.type) < Configuration.maxLevel
                    ? String.format("%d/%d", capability.getDatum(XP, this.type), capability.getNextLevelXP(this.type))
                    : String.format("%d", capability.getDatum(XP, this.type));
            this.drawHoveringText(string, mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == 1 || keyCode == KeyBindings.MENU_KEY.getKeyCode() || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void handleMouseInput() {
        try {
            super.handleMouseInput();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        final int dWheel = Mouse.getDWheel();

        if (dWheel != 0 && this.mc.player.getHeldItemMainhand().getItem() instanceof ISoulItem) {
            try {
                this.mc.displayGuiScreen(this.getClass().getDeclaredConstructor(int.class).newInstance(MathHelper.clamp(this.capability.getCurrentTab() - (int) Math.signum(dWheel), 0, this.tabs.length - 1)));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public class GUIFactory {
        public GuiButton tabButton(final int id, final int row, final String text) {
            return new GuiButton(id, width / 24, height / 16 + Math.max(height / 16 * (Configuration.menuOffset - 1 + row), 30 * row), Math.max(96, Math.round(width / 7.5F)), 20, text);
        }

        public GuiButton centeredButton(final int id, final int y, final int buttonWidth, final String text) {
            return new GuiButton(id, (width - buttonWidth) / 2, y, buttonWidth, 20, text);
        }

        public GuiButton addSquareButton(final int id, final int x, final int y, final String text) {
            return new GuiButton(id, x - 10, y - 10, 20, 20, text);
        }

        public GuiButton resetButton(final int id) {
            return new GuiButton(id, width - width / 24 - 112, height - height / 16 - 20, 112, 20, Mappings.MENU_BUTTON_RESET);
        }
    }

    public class Renderer {
        private final NumberFormat FORMAT = DecimalFormat.getInstance();

        public void drawLeftAttribute(String name, float value, int row) {
            drawString(fontRenderer, String.format(name, FORMAT.format(value)), width / 16, (row + Configuration.menuOffset) * height / 16, 0xFFFFFF);
        }

        public void drawMiddleAttribute(String format, float value, int row) {
            drawString(fontRenderer, String.format(format, FORMAT.format(value)), (width - 182) / 2, (row + Configuration.menuOffset) * height / 16, 0xFFFFFF);
        }

        public void drawMiddleEnchantment(String entry, int row) {
            drawString(fontRenderer, entry, (width - 182) / 2, (row + Configuration.menuOffset) * height / 16, 0xFFFFFF);
        }
    }
}
