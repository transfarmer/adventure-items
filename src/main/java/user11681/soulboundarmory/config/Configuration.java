package user11681.soulboundarmory.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Category;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.TransitiveObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay.Style;
import user11681.soulboundarmory.client.i18n.Mappings;

@Config(name = Main.MOD_ID)
@Background(value = "minecraft:textures/block/andesite.png")
public class Configuration implements ConfigData {
    @Tooltip
    public int initialWeaponXP = 64;
    @Tooltip
    public int initialToolXP = 16;
    @Tooltip
    public int levelsPerEnchantment = 10;
    @Tooltip
    public int levelsPerSkillPoint = 5;
    @Tooltip(count = 2)
    public int maxLevel = -1;
    @Tooltip
    public int preservationLevel = 0;

    @Category("multipliers")
    @Tooltip
    public double armorMultiplier = 0.2;
    @Category("multipliers")
    @Tooltip
    public double attackDamageMultiplier = 0.35;
    @Category("multipliers")
    @Tooltip(count = 2)
    public double difficultyMultiplier = 0.5;
    @Category("multipliers")
    @Tooltip
    public double babyMultiplier = 2;
    @Category("multipliers")
    @Tooltip
    public double bossMultiplier = 3;
    @Category("multipliers")
    @Tooltip
    public double hardcoreMultiplier = 2;
    @Category("multipliers")
    @Tooltip
    public double passiveMultiplier = 0;

    @SuppressWarnings("NewExpressionSideOnly")
    @TransitiveObject
    @Category("client")
    @Environment(EnvType.CLIENT)
    public Client client = new Client();

    public static Configuration instance() {
        return AutoConfig.getConfigHolder(Configuration.class).getConfig();
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        @Tooltip
        public boolean addToOffhand = true;
        @Tooltip
        public boolean levelupNotifications = true;
        @Tooltip
        public boolean displayOptions = true;
        @Tooltip
        public boolean overlayExperienceBar = true;
        @EnumHandler(option = EnumDisplayOption.BUTTON)
        public Style style = Style.EXPERIENCE;
        @Tooltip
        @CollapsibleObject(startExpanded = true)
        public Colors colors = new Colors();

        public void toggleOverlayExperienceBar() {
            this.overlayExperienceBar = !overlayExperienceBar;
        }

        public static class Colors {
            @BoundedDiscrete(max = 255)
            public int red = 160;
            @BoundedDiscrete(max = 255)
            public int green = 255;
            @BoundedDiscrete(max = 255)
            public int blue = 160;
            @BoundedDiscrete(max = 255)
            public int alpha = 255;

            public void set(final String key, final int value) {
                if (key.equals(Mappings.RED.getKey())) {
                    this.red = value;
                } else
                if (key.equals(Mappings.GREEN.getKey())) {
                    this.green = value;
                } else
                if (key.equals(Mappings.BLUE.getKey())) {
                    this.blue = value;
                } else
                if (key.equals(Mappings.ALPHA.getKey())) {
                    this.alpha = value;
                }
            }

            public int get(final String key) {
                return key.equals(Mappings.RED.getKey())
                        ? this.red
                        : key.equals(Mappings.GREEN.getKey())
                        ? this.green
                        : key.equals(Mappings.BLUE.getKey())
                        ? this.blue
                        : key.equals(Mappings.ALPHA.getKey())
                        ? this.alpha
                        : -1;
            }
        }
    }
}
