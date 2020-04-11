package transfarmer.soulboundarmory.config;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import transfarmer.soulboundarmory.Main;

public class ConfigGUI extends GuiConfig {
    private boolean main;

    public ConfigGUI(final GuiScreen parent, final String modid, final String title) {
        super(parent, modid, title);
    }

    public ConfigGUI(final GuiScreen parent) {
        this(parent, Main.MOD_ID, "soulbound armory configuration");

        this.main = true;
    }

    public ConfigGUI(final GuiScreen parent, final String category) {
        super(parent, new ConfigElement(MainConfig.instance().getConfigFile().getCategory(category)).getChildElements(), Main.MOD_ID, category, false, false, category);
    }

    @Override
    public void initGui() {
        super.initGui();

        if (this.main) {
            this.addButton(new GuiButton(0, width / 2 - 100, height / 2 - 20, 200, 20, "general"));
            this.addButton(new GuiButton(1, width / 2 - 100, height / 2 + 10, 200, 20, "multipliers"));
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (!this.main) {
            MainConfig.instance().saveAndLoad();
        }
    }

    @Override
    public void actionPerformed(final GuiButton button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new ConfigGUI(this, MainConfig.CATEGORY_GENERAL));
                break;
            case 1:
                this.mc.displayGuiScreen(new ConfigGUI(this, MainConfig.CATEGORY_MULTIPLIERS));
                break;
        }
    }
}
