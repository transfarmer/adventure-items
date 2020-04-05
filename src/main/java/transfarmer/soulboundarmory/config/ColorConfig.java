package transfarmer.soulboundarmory.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class ColorConfig {
    private static final ColorConfig INSTANCE = new ColorConfig();
    private static final String CATEGORY_COLOR = "color";
    private static final String CATEGORY_OTHER = "other";
    private final Configuration configFile = new Configuration(new File(String.format("%s/soulboundarmory", Loader.instance().getConfigDir()), "xp_bar.cfg"), true);
    private static float red;
    private static float green;
    private static float blue;
    private static float alpha;
    private static boolean displaySliders;

    private ColorConfig() {}

    public void load() {
        this.configFile.load();

        red = (float) this.configFile.get(CATEGORY_COLOR, "red", 1D).getDouble();
        green = (float) this.configFile.get(CATEGORY_COLOR, "green", 0D).getDouble();
        blue = (float) this.configFile.get(CATEGORY_COLOR, "blue", 1D).getDouble();
        alpha = (float) this.configFile.get(CATEGORY_COLOR, "alpha", 1D).getDouble();
        displaySliders = this.configFile.get(CATEGORY_OTHER, "displaySliders", true).getBoolean();

        this.configFile.save();
    }

    public void save() {
        this.configFile.get(CATEGORY_COLOR, "red", 1D).set(red);
        this.configFile.get(CATEGORY_COLOR, "green", 0D).set(green);
        this.configFile.get(CATEGORY_COLOR, "blue", 1D).set(blue);
        this.configFile.get(CATEGORY_COLOR, "alpha", 1D).set(alpha);
        this.configFile.get(CATEGORY_OTHER, "displaySliders", true).getBoolean();

        this.configFile.save();
    }

    public static ColorConfig instance() {
        return INSTANCE;
    }

    public static float getRed() {
        return red;
    }

    public void setRed(final float red) {
        ColorConfig.red = red;
    }

    public static float getGreen() {
        return green;
    }

    public void setGreen(final float green) {
        ColorConfig.green = green;
    }

    public static float getBlue() {
        return blue;
    }

    public void setBlue(final float blue) {
        ColorConfig.blue = blue;
    }

    public static float getAlpha() {
        return alpha;
    }

    public void setAlpha(final float alpha) {
        ColorConfig.alpha = alpha;
    }

    public static boolean getDisplaySliders() {
        return displaySliders;
    }

    public void setDisplaySliders(final boolean displaySliders) {
        ColorConfig.displaySliders = displaySliders;
    }
}
