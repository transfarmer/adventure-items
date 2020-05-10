package transfarmer.soulboundarmory.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import transfarmer.soulboundarmory.client.gui.GuiXPBar.Style;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig extends Config {
    private static final ClientConfig INSTANCE = new ClientConfig(new ClientConfiguration(new File(String.format("%s/soulboundarmory", Loader.instance().getConfigDir()), "xp_bar.cfg")));
    public static final String CATEGORY_COLOR = "color";
    public static final String CATEGORY_OTHER = "other";

    private final ClientConfiguration configFile;
    private float red;
    private float green;
    private float blue;
    private float alpha;
    private String style;
    private boolean displayOptions;
    private boolean overlayXPBar;

    private ClientConfig(final ClientConfiguration configFile) {
        super(configFile);

        this.configFile = configFile;
    }

    public static ClientConfig instance() {
        return INSTANCE;
    }

    public void load() {
        this.configFile.load();

        this.red = (float) this.configFile.getRed().getDouble();
        this.green = (float) this.configFile.getGreen().getDouble();
        this.blue = (float) this.configFile.getBlue().getDouble();
        this.alpha = (float) this.configFile.getAlpha().getDouble();
        this.style = this.configFile.getStyle().getString();
        this.displayOptions = this.configFile.getDisplaySliders().getBoolean();
        this.overlayXPBar = this.configFile.getOverlayXPBar().getBoolean();

        this.configFile.save();
    }

    public void save() {
        this.configFile.getRed().set(this.red);
        this.configFile.getGreen().set(this.green);
        this.configFile.getBlue().set(this.blue);
        this.configFile.getAlpha().set(this.alpha);
        this.configFile.getStyle().set(this.style);
        this.configFile.getDisplaySliders().set(this.displayOptions);
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

    public static Style getStyle() {
        return Style.get(INSTANCE.style);
    }

    public static void setStyle(final Style style) {
        INSTANCE.style = style.name();
    }

    public static boolean getDisplayOptions() {
        return INSTANCE.displayOptions;
    }

    public static void setDisplayOptions(final boolean displayOptions) {
        INSTANCE.displayOptions = displayOptions;
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

    private static class ClientConfiguration extends ExtendedConfiguration {
        public ClientConfiguration(final File file) {
            super(file, "2.10.11");
        }

        @Override
        List<Property> getProperties() {
            final List<Property> properties = new ArrayList<>();

            properties.add(this.getRed());
            properties.add(this.getGreen());
            properties.add(this.getBlue());
            properties.add(this.getAlpha());
            properties.add(this.getStyle());
            properties.add(this.getDisplaySliders());
            properties.add(this.getOverlayXPBar());

            return properties;
        }

        public Property getRed() {
            return this.get(CATEGORY_COLOR, "red", 160D / 255D, "red color component of XP bar");
        }

        public Property getGreen() {
            return this.get(CATEGORY_COLOR, "green", 1D, "green color component of XP bar");
        }

        public Property getBlue() {
            return this.get(CATEGORY_COLOR, "blue", 160D / 255D, "blue color component of XP bar");
        }

        public Property getAlpha() {
            return this.get(CATEGORY_COLOR, "alpha", 1D, "alpha component of XP bar");
        }

        public Property getStyle() {
            return this.get(CATEGORY_OTHER, "XP bar style", "experience");
        }

        public Property getDisplaySliders() {
            return this.get(CATEGORY_OTHER, "display options", true, "display option buttons in GUI");
        }

        public Property getOverlayXPBar() {
            return this.get(CATEGORY_OTHER, "overlay XP bar", true, "display an XP bar for the current item in the in-game overlay");
        }
    }
}
