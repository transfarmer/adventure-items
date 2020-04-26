package transfarmer.soulboundarmory.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ConfigGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(final Minecraft minecraftInstance) {}

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(final GuiScreen parentScreen) {
        return new GuiModConfig(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
