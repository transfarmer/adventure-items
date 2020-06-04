package user11681.soulboundarmory.integration;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.config.Configuration;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return Main.MOD_ID;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (final Screen parent) -> AutoConfig.getConfigScreen(Configuration.class, parent).get();
    }
}

