package transfarmer.soulboundarmory.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGUI extends GuiConfig {
    public ConfigGUI(final GuiScreen parentScreen, final String modid, final String title) {
        super(parentScreen, modid, title);
    }

    public ConfigGUI(final GuiScreen parentScreen) {
        this(parentScreen, "soulboundarmory", "soulbound armory configuration");
    }
}
