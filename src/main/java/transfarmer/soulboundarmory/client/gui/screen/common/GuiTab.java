package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.KeyBindings;
import transfarmer.soulboundarmory.client.gui.GuiExtendedButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public abstract class GuiTab extends GuiScreen implements GuiExtended {
    protected final List<GuiTab> tabs;
    protected final List<GuiExtendedButton> tabButtons;
    protected final int index;
    protected boolean displayTabs;
    protected GuiExtendedButton button;

    public GuiTab(final List<GuiTab> tabs) {
        this.tabs = tabs;
        this.tabButtons = new ArrayList<>();

        if (!tabs.contains(this)) {
            tabs.add(this);
        }

        this.index = tabs.indexOf(this);
        this.displayTabs = true;
    }

    @SafeVarargs
    protected final <T extends GuiButton> T[] addButtons(final T... buttons) {
        for (final T button : buttons) {
            this.addButton(button);
        }

        return buttons;
    }

    abstract protected String getLabel();

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        if (this.displayTabs) {
            for (int row = 0, size = this.tabs.size(); row < size; row++) {
                final GuiExtendedButton button = this.addButton(new GuiExtendedButton(16 + row, width / 24, height / 16 + row * Math.max(height / 16, 30), Math.max(96, Math.round(width / 7.5F)), 20, this.tabs.get(row).getLabel()));

                if (row == this.index) {
                    this.button = button;
                    button.enabled = false;
                }
            }
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
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
            final int index = MathHelper.clamp(this.index - dWheel / 120, 0, this.tabs.size() - 1);

            if (index != this.index) {
                this.setTab(index);
            }
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
            Main.LOGGER.error(exception);
        }
    }

    public void refresh() {
        this.initGui();
    }

    @Override
    public void actionPerformed(@NotNull final GuiButton button) {
        try {
            super.actionPerformed(button);
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }
    }

    public int getTop(final int rows) {
        return this.getTop(this.height / 16, rows);
    }

    public int getTop(final int sep, final int rows) {
        return (this.height - (rows - 1) * sep) / 2;
    }

    public int getHeight(final int rows, final int row) {
        return this.getTop(rows) + row * this.height / 16;
    }

    public int getHeight(final int sep, final int rows, final int row) {
        return this.getTop(rows, sep) + row * sep;
    }
}
