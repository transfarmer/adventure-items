package transfarmer.soulboundarmory.client.gui;

import transfarmer.soulboundarmory.client.i18n.LangEntry;
import transfarmer.soulboundarmory.config.ClientConfig;

public class RGBASlider extends Slider {
    protected int componentValue;

    public RGBASlider(final int x, final int y, final int width, final int height, final double value,
                      final LangEntry langEntry) {
        super(x, y, width, height, value, 0, 255, langEntry);
    }

    public int getValue() {
        return this.componentValue;
    }

    @Override
    public void applyValue() {
        this.componentValue = (int) (2.55 * this.value);

        ClientConfig.setColor(this.langEntry.getKey(), this.componentValue);
    }
}
