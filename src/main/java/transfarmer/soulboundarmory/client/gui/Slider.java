package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.resource.language.I18n;

public abstract class Slider extends SliderWidget {
    protected double value;
    protected String translationKey;

    public Slider(final int x, final int y, final int width, final int height, final double value, final double min,
                  final double max, final String translationKey) {
        super(x, y, width, height, value / (min + max));

        this.value = value;
        this.translationKey = translationKey;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(String.format("%s: %s", I18n.translate(this.translationKey), this.value));
    }
}
