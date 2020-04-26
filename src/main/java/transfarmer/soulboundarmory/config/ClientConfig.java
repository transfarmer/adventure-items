package transfarmer.soulboundarmory.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class ClientConfig {
    private static final ClientConfig INSTANCE = new ClientConfig();
    public static final String CATEGORY_COLOR = "color";
    public static final String CATEGORY_OTHER = "other";
    private final ClientConfiguration configFile = new ClientConfiguration(new File(String.format("%s/soulboundarmory", Loader.instance().getConfigDir()), "xp_bar.cfg"));
    private float red;
    private float green;
    private float blue;
    private float alpha;
    private boolean displaySliders;
    private boolean overlayXPBar;

    private ClientConfig() {}

    public static ClientConfig instance() {
        return INSTANCE;
    }

    public void load() {
        this.configFile.load();

        this.red = (float) this.configFile.getRed().getDouble();
        this.green = (float) this.configFile.getGreen().getDouble();
        this.blue = (float) this.configFile.getBlue().getDouble();
        this.alpha = (float) this.configFile.getAlpha().getDouble();
        this.displaySliders = this.configFile.getDisplaySliders().getBoolean();
        this.overlayXPBar = this.configFile.getOverlayXPBar().getBoolean();

        this.configFile.save();
    }

    public void save() {
        this.configFile.getRed().set(this.red);
        this.configFile.getGreen().set(this.green);
        this.configFile.getBlue().set(this.blue);
        this.configFile.getAlpha().set(this.alpha);
        this.configFile.getDisplaySliders().set(this.displaySliders);
        this.configFile.getOverlayXPBar().set(this.overlayXPBar);

        this.configFile.save();
    }

    public Configuration getConfigFile() {
        return this.configFile;
    }

    public static float getRed() {
        return INSTANCE.red;
    }

    public static void setRed(final float red) {
        INSTANCE.red = red;
    }

    public static float getGreen() {
        return INSTANCE.green;
    }

    public static void setGreen(final float green) {
        INSTANCE.green = green;
    }

    public static float getBlue() {
        return INSTANCE.blue;
    }

    public static void setBlue(final float blue) {
        INSTANCE.blue = blue;
    }

    public static float getAlpha() {
        return INSTANCE.alpha;
    }

    public static void setAlpha(final float alpha) {
        INSTANCE.alpha = alpha;
    }

    public static boolean getDisplaySliders() {
        return INSTANCE.displaySliders;
    }

    public static void setDisplaySliders(final boolean displaySliders) {
        INSTANCE.displaySliders = displaySliders;
    }

    public static boolean getOverlayXPBar() {
        return INSTANCE.overlayXPBar;
    }

    public static void setOverlayXPBar(final boolean overlayXPBar) {
        INSTANCE.overlayXPBar = overlayXPBar;
    }

    public void saveAndLoad() {
        this.configFile.save();
        this.load();
    }

    private static class ClientConfiguration extends Configuration {
        public ClientConfiguration(final File file) {
            super(file);
        }

        public Property getRed() {
            return this.get(CATEGORY_COLOR, "red", 160D / 255D);
        }

        public Property getGreen() {
            return this.get(CATEGORY_COLOR, "green", 1D);
        }

        public Property getBlue() {
            return this.get(CATEGORY_COLOR, "blue", 160D / 255D);
        }

        public Property getAlpha() {
            return this.get(CATEGORY_COLOR, "alpha", 1D);
        }

        public Property getDisplaySliders() {
            return this.get(CATEGORY_OTHER, "displaySliders", true);
        }

        public Property getOverlayXPBar() {
            return this.get(CATEGORY_OTHER, "overlayXPBar", true, "display an XP bar for the current item in the in-game overlay");
        }
    }
}
