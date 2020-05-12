package transfarmer.soulboundarmory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;

public class MainClient implements ClientModInitializer {
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static final ClientSidePacketRegistry PACKET_REGISTRY = ClientSidePacketRegistry.INSTANCE;

    @Override
    public void onInitializeClient() {
    }
}
