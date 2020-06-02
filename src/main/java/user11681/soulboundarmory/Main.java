package user11681.soulboundarmory;

import java.util.UUID;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import nerdhub.cardinal.components.api.util.EntityComponents;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user11681.soulboundarmory.command.SoulboundArmoryCommand;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.enchantment.ImpactEnchantment;
import user11681.soulboundarmory.item.ModItems;
import user11681.soulboundarmory.network.C2S.C2SAttribute;
import user11681.soulboundarmory.network.C2S.C2SBindSlot;
import user11681.soulboundarmory.network.C2S.C2SConfig;
import user11681.soulboundarmory.network.C2S.C2SEnchant;
import user11681.soulboundarmory.network.C2S.C2SItemType;

import static user11681.soulboundarmory.network.Packets.C2S_ATTRIBUTE;
import static user11681.soulboundarmory.network.Packets.C2S_BIND_SLOT;
import static user11681.soulboundarmory.network.Packets.C2S_CONFIG;
import static user11681.soulboundarmory.network.Packets.C2S_ENCHANT;
import static user11681.soulboundarmory.network.Packets.C2S_ITEM_TYPE;

public class Main implements ModInitializer {
    public static final String MOD_ID = "soulboundarmory";
    public static final String MOD_NAME = "soulbound armory";
    public static final String VERSION = "3.0.0";

    public static final Enchantment IMPACT = Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "impact"), new ImpactEnchantment());
    public static final UUID ATTACK_RANGE_MODIFIER_UUID = UUID.fromString("F136C871-E55A-4DB5-A8FE-8EA49D9B5B81");
    public static final UUID REACH_MODIFIER_UUID = UUID.fromString("2D4AA65A-4A15-4C46-9F6B-D3898AEC42B6");

    public static final ServerSidePacketRegistry PACKET_REGISTRY = ServerSidePacketRegistry.INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public void onInitialize() {
        ModItems.register();

        EntityComponents.setRespawnCopyStrategy(Components.CONFIG_COMPONENT, RespawnCopyStrategy.ALWAYS_COPY);
        Components.attach();

        PACKET_REGISTRY.register(C2S_ATTRIBUTE, new C2SAttribute());
        PACKET_REGISTRY.register(C2S_BIND_SLOT, new C2SBindSlot());
        PACKET_REGISTRY.register(C2S_CONFIG, new C2SConfig());
        PACKET_REGISTRY.register(C2S_ENCHANT, new C2SEnchant());
        PACKET_REGISTRY.register(C2S_ITEM_TYPE, new C2SItemType());

        CommandRegistrationCallback.EVENT.register(SoulboundArmoryCommand::register);

        AutoConfig.register(Configuration.class, Toml4jConfigSerializer::new);
    }
}
