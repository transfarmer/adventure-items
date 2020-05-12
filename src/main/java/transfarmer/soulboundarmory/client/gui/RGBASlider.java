package transfarmer.soulboundarmory.client.gui;

import transfarmer.farmerlib.reflect.FieldWrapper;
import transfarmer.soulboundarmory.config.ClientConfig;

public class RGBASlider extends Slider {
    protected static final FieldWrapper<>

    public RGBASlider(final int x, final int y, final int width, final int height, final double value,
                      final String translationKey) {
        super(x, y, width, height, value, 0, 255, translationKey);
    }

    @Override
    protected void applyValue() {
        ClientConfig.setColor(this.translationKey, (int) this.value);
    }

    public String getColor() {
        return this.translationKey;
    }


}
