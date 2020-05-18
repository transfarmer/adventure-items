package transfarmer.soulboundarmory.client;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.config.MainConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return Main.MOD_ID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (final Screen parent) -> AutoConfig.getConfigScreen(MainConfig.class, parent).get();
    }
}

