package user11681.soulboundarmory;

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
import user11681.soulboundarmory.client.keyboard.ExperienceBarKeyBinding;
import user11681.soulboundarmory.client.keyboard.GUIKeyBinding;
import user11681.soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import user11681.soulboundarmory.client.render.SoulboundFireballEntityRenderer;
import user11681.soulboundarmory.entity.EntityTypes;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static final FabricKeyBinding GUI_KEY_BINDING = new GUIKeyBinding(new Identifier(Main.MOD_ID, "gui"), Type.KEYSYM, GLFW.GLFW_KEY_R, Main.MOD_NAME);
    public static final FabricKeyBinding TOGGLE_XP_BAR_KEY_BINDING = new ExperienceBarKeyBinding(new Identifier(Main.MOD_ID, "xp_bar"), Type.KEYSYM, GLFW.GLFW_KEY_X, Main.MOD_NAME);

    public static final ClientSidePacketRegistry PACKET_REGISTRY = ClientSidePacketRegistry.INSTANCE;

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory(Main.MOD_NAME);
        KeyBindingRegistry.INSTANCE.register(GUI_KEY_BINDING);
        KeyBindingRegistry.INSTANCE.register(TOGGLE_XP_BAR_KEY_BINDING);

        EntityRendererRegistry.INSTANCE.register(EntityTypes.SOULBOUND_DAGGER_ENTITY, SoulboundDaggerEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EntityTypes.SOULBOUND_FIREBALL_ENTITY, SoulboundFireballEntityRenderer::new);
    }

    public static PlayerEntity getPlayer() {
        return CLIENT.player;
    }
}
