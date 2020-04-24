package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.KeyBindings;
import transfarmer.soulboundarmory.client.i18n.Mappings;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public abstract class GuiTab extends GuiScreen {
    protected final GUIFactory guiFactory;
    protected final Renderer renderer;

    protected final List<GuiTab> tabs;
    protected final int index;

    public GuiTab(final List<GuiTab> tabs) {
        this.guiFactory = new GUIFactory(this);
        this.renderer = new Renderer(this);
        this.tabs = tabs;

        if (!tabs.contains(this)) {
            tabs.add(this);
        }

        this.index = tabs.indexOf(this);
    }

    @SafeVarargs
    protected final <T extends GuiButton> T[] addButtons(final T... buttons) {
        for (final GuiButton button : buttons) {
            this.addButton(button);
        }

        return buttons;
    }

    abstract protected String getLabel();

    @Override
    public void initGui() {
        super.initGui();

        for (int row = 0; row < this.tabs.size(); row++) {
            final GuiButton button = this.addButton(new GuiButton(16 + row, width / 24, height / 16 + row * Math.max(height / 16, 30), Math.max(96, Math.round(width / 7.5F)), 20, this.tabs.get(row).getLabel()));

            button.enabled = row != this.index;
        }
    }

    @Override
    public void handleMouseInput() {
        try {
            super.handleMouseInput();
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }

        this.handleMouseWheel(Mouse.getDWheel() / 120);
    }

    public void handleMouseWheel(final int dWheel) {
        if (dWheel != 0) {
            this.setTab(MathHelper.clamp(this.index - dWheel / 120, 0, this.tabs.size() - 1));
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) {
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }

        if (keyCode == KeyBindings.MENU_KEY.getKeyCode() || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
        }
    }

    public void setTab(final int tab) {
        try {
            this.mc.displayGuiScreen(this.tabs.get(tab));
        } catch (final Throwable exception) {
            exception.printStackTrace();
        }
    }

    public void refresh() {
        this.setTab(this.index);
    }

    @Override
    public void actionPerformed(final GuiButton button) {
        try {
            super.actionPerformed(button);
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }
    }

    protected static class GUIFactory {
        private final GuiScreen screen;

        public GUIFactory(final GuiScreen screen) {
            this.screen = screen;
        }

        public GuiButton centeredButton(final int id, final int y, final int buttonWidth, final String text) {
            return new GuiButton(id, (screen.width - buttonWidth) / 2, y, buttonWidth, 20, text);
        }

        public GuiButton squareButton(final int id, final int x, final int y, final String text) {
            return new GuiButton(id, x - 10, y - 10, 20, 20, text);
        }

        public GuiButton resetButton(final int id) {
            return new GuiButton(id, screen.width - screen.width / 24 - 112, screen.height - screen.height / 16 - 20, 112, 20, Mappings.MENU_BUTTON_RESET);
        }

        public GuiSlider colorSlider(final int id, final int row, final double currentValue, final String text) {
            return new GuiSlider(id, this.getColorSliderX(), this.getColorSliderY(row), 100, 20, text, "", 0, 255, currentValue * 255, false, true);
        }

        public GuiButton[] addPointButtons(final int id, final int rows, final int points) {
            final GuiButton[] buttons = new GuiButton[rows];

            for (int row = 0; row < rows; row++) {
                buttons[row] = squareButton(id + row, (screen.width + 162) / 2, (row + 1) * screen.height / 16 + 4, "+");
                buttons[row].enabled = points > 0;
            }

            return buttons;
        }

        public int getColorSliderX() {
            return Math.round(screen.width * (1 - 1 / 24F)) - 100;
        }

        public int getColorSliderY(final int row) {
            return screen.height / 16 + Math.max(screen.height / 16 * row, 30 * row);
        }

        public GuiButton[] removePointButtons(final int id, final int rows) {
            final GuiButton[] buttons = new GuiButton[rows];

            for (int row = 0; row < rows; row++) {
                buttons[row] = squareButton(id + row, (screen.width + 162) / 2 - 20, (row + 1) * screen.height / 16 + 4, "-");
            }

            return buttons;
        }
    }

    public static class Renderer {
        private final GuiScreen screen;
        private final NumberFormat format;

        public Renderer(final GuiScreen screen) {
            this.screen = screen;
            this.format = DecimalFormat.getInstance();
        }

        public void drawMiddleAttribute(String format, double value, int row) {
            screen.drawString(screen.mc.fontRenderer, String.format(format, this.format.format(value)), (screen.width - 182) / 2, (row + 1) * screen.height / 16, 0xFFFFFF);
        }

        public void drawMiddleEnchantment(String entry, int row) {
            screen.drawString(screen.mc.fontRenderer, entry, (screen.width - 182) / 2, (row + 1) * screen.height / 16, 0xFFFFFF);
        }
    }
}
