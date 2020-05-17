package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.TranslatableText;
import transfarmer.soulboundarmory.Main;

public abstract class Slider extends SliderWidget {
    protected TranslatableText text;

    public Slider(final int x, final int y, final int width, final int height, final double value, final double min,
                  final double max, final TranslatableText text) {
        super(x, y, width, height, value / (min + max));

        this.value = value;
        this.text = text;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(String.format("%s: %s", this.text.toString(), this.value));
    }

    public String getKey() {
        return this.text.getKey();
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
