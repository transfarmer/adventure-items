package transfarmer.soulboundarmory;

import com.sun.tools.javac.tree.JCTree.JCLambda.ParameterKind;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.util.EntityComponents;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfarmer.soulboundarmory.component.config.ConfigComponent;
import transfarmer.soulboundarmory.component.config.IConfigComponent;
import transfarmer.soulboundarmory.component.entity.EntityData;
import transfarmer.soulboundarmory.component.entity.IEntityData;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.tool.IToolComponent;
import transfarmer.soulboundarmory.component.soulbound.tool.ToolComponent;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulboundDagger;
import transfarmer.soulboundarmory.init.ModItems;
import transfarmer.soulboundarmory.network.C2S.C2SConfig;

import static transfarmer.soulboundarmory.network.Packets.C2S_CONFIG;

public class Main implements ModInitializer {
    public static final String MOD_ID = "soulboundarmory";
    public static final String NAME = "soulbound armory";
    public static final String VERSION = "3.0.0";

    public static final ComponentType<IConfigComponent> CONFIG = ComponentRegistry.INSTANCE
            .registerIfAbsent(new Identifier(MOD_ID, "config_component"), IConfigComponent.class)
            .attach(EntityComponentCallback.event(PlayerEntity.class), ConfigComponent::new);
    public static final ComponentType<IEntityData> ENTITY_DATA = ComponentRegistry.INSTANCE
            .registerIfAbsent(new Identifier(MOD_ID, "entity_data"), IEntityData.class)
            .attach(EntityComponentCallback.event(Entity.class), (final Entity entity) -> {
                        if ((entity instanceof LivingEntity || entity instanceof Projectile)
                                && !(entity instanceof EntityReachModifier || entity instanceof EntitySoulboundDagger)) {
                            return new EntityData(entity);
                        }

                        return null;
                    }
            );
    public static final ComponentType<ISoulboundComponent> TOOLS = ComponentRegistry.INSTANCE
            .registerIfAbsent(new Identifier(MOD_ID, "tools"), IToolComponent.class)
            .attach(EntityComponentCallback.event(PlayerEntity.class), (final PlayerEntity player) -> ToolComponent::new);

    public static final ServerSidePacketRegistry PACKET_REGISTRY = ServerSidePacketRegistry.INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public void onInitialize() {
        EntityComponents.setRespawnCopyStrategy(CONFIG, RespawnCopyStrategy.ALWAYS_COPY);
        EntityComponents.setRespawnCopyStrategy(TOOLS, RespawnCopyStrategy.ALWAYS_COPY);

        PACKET_REGISTRY.register(C2S_CONFIG, new C2SConfig());

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_pick"), ModItems.SOULBOUND_PICK);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_dagger"), ModItems.SOULBOUND_DAGGER);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_sword"), ModItems.SOULBOUND_SWORD);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_greatsword"), ModItems.SOULBOUND_GREATSWORD);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_staff"), ModItems.SOULBOUND_STAFF);
//        CHANNEL.registerMessage(S2CConfig.Handler.class, S2CConfig.class, id++, CLIENT);
//        CHANNEL.registerMessage(S2CEnchant.Handler.class, S2CEnchant.class, id++, CLIENT);
//        CHANNEL.registerMessage(S2COpenGUI.Handler.class, S2COpenGUI.class, id++, CLIENT);
//        CHANNEL.registerMessage(S2CSync.Handler.class, S2CSync.class, id++, CLIENT);
//        CHANNEL.registerMessage(S2CItemType.Handler.class, S2CItemType.class, id++, CLIENT);
//        CHANNEL.registerMessage(S2CRefresh.Handler.class, S2CRefresh.class, id++, CLIENT);
//
//        CHANNEL.registerMessage(C2SAttribute.Handler.class, C2SAttribute.class, id++, SERVER);
//        CHANNEL.registerMessage(C2SBindSlot.Handler.class, C2SBindSlot.class, id++, SERVER);
//        CHANNEL.registerMessage(C2SConfig.Handler.class, C2SConfig.class, id++, SERVER);
//        CHANNEL.registerMessage(C2SEnchant.Handler.class, C2SEnchant.class, id++, SERVER);
//        CHANNEL.registerMessage(C2SItemType.Handler.class, C2SItemType.class, id++, SERVER);
//        CHANNEL.registerMessage(C2SReset.Handler.class, C2SReset.class, id++, SERVER);
//        CHANNEL.registerMessage(C2SSync.Handler.class, C2SSync.class, id++, SERVER);
//        CHANNEL.registerMessage(C2SSkill.Handler.class, C2SSkill.class, id++, SERVER);
//
//        if (FMLCommonHandler.instance().getSide() == CLIENT) {
//            ClientRegistry.registerKeyBinding(MENU_KEY);
//            ClientRegistry.registerKeyBinding(TOGGLE_XP_BAR_KEY);
//
//            RenderingRegistry.registerEntityRenderingHandler(EntitySoulboundDagger.class, new RenderSoulDagger.RenderFactory());
//            RenderingRegistry.registerEntityRenderingHandler(EntityReachModifier.class, new RenderReachModifier.RenderFactory());
//            RenderingRegistry.registerEntityRenderingHandler(EntitySoulboundSmallFireball.class, new RenderSoulboundFireball.RenderFactory());
//        }

        MainConfig.instance().load();
        MainConfig.instance().update();
        MainConfig.instance().save();
        ClientConfig.instance().load();
        ClientConfig.instance().update();
        ClientConfig.instance().save();

//        CommandRegistrationCallback.EVENT.invoker().register();
    }
}
