package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.widget.SliderWidget;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.i18n.LangEntry;

public abstract class Slider extends SliderWidget {
    protected LangEntry langEntry;

    public Slider(final int x, final int y, final int width, final int height, final double value, final double min,
                  final double max, final LangEntry langEntry) {
        super(x, y, width, height, value / (min + max));

        this.value = value;
        this.langEntry = langEntry;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(String.format("%s: %s", this.langEntry.toString(), this.value));
    }

    public String getKey() {
        return this.langEntry.getKey();
    }

    public void setValue(final double value) {
        this.value = value;

        this.applyValue();
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        Main.LOGGER.warn("button scrolled");

        return true;
    }
}
