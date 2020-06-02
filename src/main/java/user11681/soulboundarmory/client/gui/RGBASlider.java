package user11681.soulboundarmory.client.gui;

import net.minecraft.text.TranslatableText;
import user11681.soulboundarmory.config.Configuration;

public class RGBASlider extends Slider {
    protected int componentValue;

    public RGBASlider(final int x, final int y, final int width, final int height, final double value,
                      final TranslatableText text) {
        super(x, y, width, height, value, 0, 255, text);
    }

    public int getValue() {
        return this.componentValue;
    }

    @Override
    public void applyValue() {
        this.componentValue = (int) (2.55 * this.value);

        Configuration.instance().client.colors.set(this.text.getKey(), this.componentValue);
    }
}
