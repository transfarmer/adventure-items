package transfarmer.soulboundarmory.config;

import net.minecraft.client.gui.ButtonWidget;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.network.C2S.C2SConfig;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@Environment(CLIENT)
public class GuiModConfig extends GuiConfig {
    private boolean main;

    public GuiModConfig(final GuiScreen parent, final String modid, final String title) {
        super(parent, modid, title);
    }

    public GuiModConfig(final GuiScreen parent) {
        this(parent, Main.MOD_ID, "soulbound armory configuration");

        this.main = true;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.addButton(new ButtonWidget(0, width / 2 - 100, height / 2 - 45, MainConfig.CATEGORY_GENERAL));
        this.addButton(new ButtonWidget(1, width / 2 - 100, height / 2 - 15, MainConfig.CATEGORY_MULTIPLIERS));
        this.addButton(new ButtonWidget(2, width / 2 - 100, height / 2 + 15, ClientConfig.CATEGORY_COLOR));
        this.addButton(new ButtonWidget(3, width / 2 - 100, height / 2 + 45, ClientConfig.CATEGORY_OTHER));
    }

    @Override
    public void actionPerformed(final ButtonWidget button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiModConfigMain(this, MainConfig.CATEGORY_GENERAL));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiModConfigMain(this, MainConfig.CATEGORY_MULTIPLIERS));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiModConfigClient(this, ClientConfig.CATEGORY_COLOR));
                break;
            case 3:
                this.mc.displayGuiScreen(new GuiModConfigClient(this, ClientConfig.CATEGORY_OTHER));
        }
    }

    public static class GuiModConfigMain extends GuiConfig {
        public GuiModConfigMain(final GuiScreen parent, final String category) {
            super(parent, new ConfigElement(MainConfig.instance().getConfigFile().getCategory(category)).getChildElements(), Main.MOD_ID, category, false, false, category);
        }

        @Override
        public void onGuiClosed() {
            MainConfig.instance().saveAndLoad();
            Main.CHANNEL.sendToServer(new C2SConfig());
        }
    }

    public static class GuiModConfigClient extends GuiConfig {
        public GuiModConfigClient(final GuiScreen parent, final String category) {
            super(parent, new ConfigElement(ClientConfig.instance().getConfigFile().getCategory(category)).getChildElements(), Main.MOD_ID, category, false, false, category);
        }

        @Override
        public void onGuiClosed() {
            ClientConfig.instance().saveAndLoad();
        }
    }
}
