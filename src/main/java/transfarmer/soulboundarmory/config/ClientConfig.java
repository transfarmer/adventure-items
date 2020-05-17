package transfarmer.soulboundarmory.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.gui.GuiXPBar.Style;

import java.util.HashMap;
import java.util.Map;

@Config(name = Main.MOD_ID)
@Environment(EnvType.CLIENT)
public class ClientConfig extends AbstractConfig {
//    private static final ClientConfig INSTANCE = new ClientConfig(new ClientConfiguration(new File(String.format("%s/soulboundarmory", Loader.instance().getConfigDir()), "xp_bar.cfg")));
//    public static final String CATEGORY_COLOR = "color";
//    public static final String CATEGORY_OTHER = "other";

//    private final ClientConfiguration configFile;
    private final Map<String, Integer> rgba;

    public String style;
    public boolean displayOptions;
    public boolean overlayXPBar;

    public ClientConfig() {
        this.rgba = new HashMap<>(4);
    }

    public static ClientConfig instance() {
        return AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
    }

//    private ClientConfig(final ClientConfiguration configFile) {
//        super(configFile);
//
//        this.configFile = configFile;
//        this.rgba = CollectionUtil.hashMap(new String[]{
//                Mappings.RED.getKey(),
//                Mappings.GREEN.getKey(),
//                Mappings.BLUE.getKey(),
//                Mappings.ALPHA.getKey()
//        });
//    }
//
    public void setColor(final TranslatableText color, final int value) {
        this.rgba.put(color.getKey(), value);
    }

    public int getRGBA(final TranslatableText color) {
        return this.rgba.get(color.getKey());
    }

//    public void load() {
//        this.configFile.load();
//
//        this.red = this.configFile.getRed().getInt();
//        this.green = this.configFile.getGreen().getInt();
//        this.blue = this.configFile.getBlue().getInt();
//        this.alpha = this.configFile.getAlpha().getInt();
//        this.style = this.configFile.getStyle().getString();
//        this.displayOptions = this.configFile.getDisplaySliders().getBoolean();
//        this.overlayXPBar = this.configFile.getOverlayXPBar().getBoolean();
//
//        this.configFile.save();
//    }
//
//    public void save() {
//        this.configFile.getRed().set(this.red);
//        this.configFile.getGreen().set(this.green);
//        this.configFile.getBlue().set(this.blue);
//        this.configFile.getAlpha().set(this.alpha);
//        this.configFile.getStyle().set(this.style);
//        this.configFile.getDisplaySliders().set(this.displayOptions);
//        this.configFile.getOverlayXPBar().set(this.overlayXPBar);
//
//        this.configFile.save();
//    }
//
//    public Configuration getConfigFile() {
//        return this.configFile;
//    }
//
//
//    public int getRed() {
//        return red;
//    }
//
//    public static void setRed(final int red) {
//        INSTANCE.red = red;
//    }
//
//    public static int getGreen() {
//        return INSTANCE.green;
//    }
//
//    public static void setGreen(final int green) {
//        INSTANCE.green = green;
//    }
//
//    public static int getBlue() {
//        return INSTANCE.blue;
//    }
//
//    public static void setBlue(final int blue) {
//        INSTANCE.blue = blue;
//    }
//
//    public static int getAlpha() {
//        return INSTANCE.alpha;
//    }
//
//    public static void setAlpha(final int alpha) {
//        INSTANCE.alpha = alpha;
//    }

    public Style getStyle() {
        return Style.get(this.style);
    }

    public void setStyle(final Style style) {
        this.style = style.name();
    }
//
//    public static boolean getDisplayOptions() {
//        return INSTANCE.displayOptions;
//    }
//
//    public static void setDisplayOptions(final boolean displayOptions) {
//        INSTANCE.displayOptions = displayOptions;
//    }
//
//    public static boolean getOverlayXPBar() {
//        return INSTANCE.overlayXPBar;
//    }
//
//    public static void setOverlayXPBar(final boolean overlayXPBar) {
//        INSTANCE.overlayXPBar = overlayXPBar;
//    }
//
//    public void saveAndLoad() {
//        this.configFile.save();
//        this.load();
//    }
//
//    private static class ClientConfiguration extends ExtendedConfiguration {
//        public ClientConfiguration(final File file) {
//            super(file, "2.10.11");
//        }
//
//        @Override
//        List<Property> getProperties() {
//            final List<Property> properties = new ArrayList<>();
//
//            properties.add(this.getRed());
//            properties.add(this.getGreen());
//            properties.add(this.getBlue());
//            properties.add(this.getAlpha());
//            properties.add(this.getStyle());
//            properties.add(this.getDisplaySliders());
//            properties.add(this.getOverlayXPBar());
//
//            return properties;
//        }
//
//        public Property getRed() {
//            return this.get(CATEGORY_COLOR, "red", 0, "red color component of XP bar");
//        }
//
//        public Property getGreen() {
//            return this.get(CATEGORY_COLOR, "green", 128, "green color component of XP bar");
//        }
//
//        public Property getBlue() {
//            return this.get(CATEGORY_COLOR, "blue", 255, "blue color component of XP bar");
//        }
//
//        public Property getAlpha() {
//            return this.get(CATEGORY_COLOR, "alpha", 255, "alpha component of XP bar");
//        }
//
//        public Property getStyle() {
//            return this.get(CATEGORY_OTHER, "XP bar style", "experience");
//        }
//
//        public Property getDisplaySliders() {
//            return this.get(CATEGORY_OTHER, "display options", true, "display option buttons in GUI");
//        }
//
//        public Property getOverlayXPBar() {
//            return this.get(CATEGORY_OTHER, "overlay XP bar", true, "display an XP bar for the current item in the in-game overlay");
//        }
//
//   }
}
