package transfarmer.soulboundarmory.client.gui.screen.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.gui.ExtendedButtonWidget;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.MainClient.CLIENT;

@Environment(EnvType.CLIENT)
public abstract class ScreenTab extends ExtendedScreen {
    protected final List<ScreenTab> tabs;
    protected final List<ExtendedButtonWidget> tabButtons;
    protected final int index;
    protected boolean displayTabs;
    protected ExtendedButtonWidget button;

    public ScreenTab(final Text title, final List<ScreenTab> tabs) {
        super(title);

        this.tabs = tabs;
        this.tabButtons = new ArrayList<>();

        if (!tabs.contains(this)) {
            tabs.add(this);
        }

        this.index = tabs.indexOf(this);
        this.displayTabs = true;
    }

    @SafeVarargs
    protected final <T extends ButtonWidget> T[] addButtons(final T... buttons) {
        for (final T button : buttons) {
            this.addButton(button);
        }

        return buttons;
    }

    abstract protected String getLabel();

    @Override
    public void init() {
        super.init();

        if (this.displayTabs) {
            for (int row = 0, size = this.tabs.size(); row < size; row++) {
                final ExtendedButtonWidget button = this.addButton(new ExtendedButtonWidget(
                        width / 24,
                        height / 16 + row * Math.max(height / 16, 30),
                        Math.max(96, Math.round(width / 7.5F)),
                        20,
                        this.tabs.get(row).getLabel(),
                        new
                ));

                this.tabButtons.add(button);

                if (row == this.index) {
                    this.button = button;
                    button.active = false;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double dWheel) {
        if (dWheel != 0) {
            final int index = MathHelper.clamp((int) (this.index - dWheel), 0, this.tabs.size() - 1);

            if (index != this.index) {
                this.setTab(index);

                return true;
            }
        }

        return super.mouseScrolled(x, y, dWheel);
    }

    public void setTab(final int tab) {
        try {
            CLIENT.openScreen(this.tabs.get(tab));
        } catch (final Throwable exception) {
            Main.LOGGER.error(exception);
        }
    }

    public void refresh() {
        this.buttons.clear();
        this.init();
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
