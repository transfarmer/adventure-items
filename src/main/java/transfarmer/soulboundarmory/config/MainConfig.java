package transfarmer.soulboundarmory.config;

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
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.gui.XPBarGUI.Style;

@Config(name = Main.MOD_ID)
@Background(value = "minecraft:textures/block/andesite.png")
public class MainConfig implements ConfigData {
    @Tooltip
    public int initialWeaponXP = 48;
    @Tooltip
    public int initialToolXP = 16;
    @Tooltip
    public int levelsPerEnchantment = 10;
    @Tooltip
    public int levelsPerSkillPoint = 5;
    @Tooltip
    public int maxLevel = -1;
    @Tooltip
    public int preservationLevel = 0;
    @Tooltip
    public boolean addToOffhand = true;
    @Tooltip
    public boolean levelupNotifications = true;

    @Category("multipliers")
    @Tooltip
    public double armorMultiplier = 0.2;
    @Category("multipliers")
    @Tooltip
    public double attackDamageMultiplier = 0.35;
    @Category("multipliers")
    @Tooltip
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

    @Category("client")
    @Tooltip
    public boolean displayOptions = true;
    @Category("client")
    @Tooltip
    public boolean overlayXPBar = true;
    @Category("client")
    @Tooltip
    @EnumHandler(option = EnumDisplayOption.BUTTON)
    public Style style = Style.EXPERIENCE;
    @Category("client")
    @Tooltip
    @CollapsibleObject(startExpanded = true)
    public Colors colors = new Colors();

    public static MainConfig instance() {
        return AutoConfig.getConfigHolder(MainConfig.class).getConfig();
    }

    @SuppressWarnings("MethodCallSideOnly")
    public MainConfig() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ConfigBuilder.create().getOrCreateCategory("multipliers");
            ConfigBuilder.create().getOrCreateCategory("client");
        }
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
    }
}
