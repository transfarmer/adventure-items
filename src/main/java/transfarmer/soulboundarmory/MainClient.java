package transfarmer.soulboundarmory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import transfarmer.soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import transfarmer.soulboundarmory.config.ClientConfig;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public static final PlayerEntity PLAYER = CLIENT.player;

    public static final FabricKeyBinding GUI_KEY_BINDING = FabricKeyBinding.Builder
            .create(new Identifier("gui"), Type.KEYSYM, GLFW.GLFW_KEY_R, Main.MOD_NAME)
            .build();
    public static final FabricKeyBinding TOGGLE_XP_BAR_KEY_BINDING = FabricKeyBinding.Builder
            .create(new Identifier("xp_bar"), Type.KEYSYM, GLFW.GLFW_KEY_X, Main.MOD_NAME)
            .build();

    public static final ClientSidePacketRegistry PACKET_REGISTRY = ClientSidePacketRegistry.INSTANCE;

    @Override
    public void onInitializeClient() {
        ClientConfig.instance().load();
        ClientConfig.instance().update();
        ClientConfig.instance().save();

        KeyBindingRegistry.INSTANCE.addCategory(Main.MOD_NAME);
        KeyBindingRegistry.INSTANCE.register(GUI_KEY_BINDING);
        KeyBindingRegistry.INSTANCE.register(TOGGLE_XP_BAR_KEY_BINDING);

        EntityRendererRegistry.INSTANCE.register(Main.SOULBOUND_DAGGER_ENTITY, SoulboundDaggerEntityRenderer::new);
    }
}
