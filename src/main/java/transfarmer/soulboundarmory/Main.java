package transfarmer.soulboundarmory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfarmer.soulboundarmory.capability.config.IPlayerConfig;
import transfarmer.soulboundarmory.capability.config.PlayerConfig;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.capability.config.PlayerConfigStorage;
import transfarmer.soulboundarmory.capability.frozen.Frozen;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.FrozenStorage;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulTool;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolStorage;
import transfarmer.soulboundarmory.capability.soulbound.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponStorage;
import transfarmer.soulboundarmory.client.render.RenderReachModifier;
import transfarmer.soulboundarmory.client.render.RenderSoulDagger;
import transfarmer.soulboundarmory.command.CommandSoulboundArmory;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.network.client.S2CConfig;
import transfarmer.soulboundarmory.network.client.S2CLevelupMessage;
import transfarmer.soulboundarmory.network.client.S2CSync;
import transfarmer.soulboundarmory.network.client.tool.S2CToolBindSlot;
import transfarmer.soulboundarmory.network.client.tool.S2CToolDatum;
import transfarmer.soulboundarmory.network.client.tool.S2CToolResetAttributes;
import transfarmer.soulboundarmory.network.client.tool.S2CToolResetEnchantments;
import transfarmer.soulboundarmory.network.client.tool.S2CToolSpendAttributePoints;
import transfarmer.soulboundarmory.network.client.tool.S2CToolSpendEnchantmentPoints;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponBindSlot;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponResetAttributes;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponResetEnchantments;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponSpendEnchantmentPoints;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponSpentAttributePoints;
import transfarmer.soulboundarmory.network.server.C2SConfig;
import transfarmer.soulboundarmory.network.server.tool.C2SToolAttributePoints;
import transfarmer.soulboundarmory.network.server.tool.C2SToolBindSlot;
import transfarmer.soulboundarmory.network.server.tool.C2SToolEnchantmentPoints;
import transfarmer.soulboundarmory.network.server.tool.C2SToolResetAttributes;
import transfarmer.soulboundarmory.network.server.tool.C2SToolResetEnchantments;
import transfarmer.soulboundarmory.network.server.tool.C2SToolTab;
import transfarmer.soulboundarmory.network.server.tool.C2SToolType;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponAttributePoints;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponBindSlot;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponEnchantmentPoints;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponResetAttributes;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponResetEnchantments;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponTAb;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;

@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION, guiFactory = "transfarmer.soulboundarmory.config.ConfigGuiFactory")
public class Main {
    public static final String MOD_ID = "soulboundarmory";
    public static final String NAME = "soulbound armory";
    public static final String VERSION = "2.6.4";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    public static int id;

    @EventHandler
    public static void onPreinit(final FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);
        CapabilityManager.INSTANCE.register(ISoulCapability.class, new SoulToolStorage(), SoulTool::new);
        CapabilityManager.INSTANCE.register(IFrozen.class, new FrozenStorage(), Frozen::new);
        CapabilityManager.INSTANCE.register(IPlayerConfig.class, new PlayerConfigStorage(), PlayerConfig::new);

        CHANNEL.registerMessage(S2CSync.Handler.class, S2CSync.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CConfig.Handler.class, S2CConfig.class, id++, CLIENT);

        CHANNEL.registerMessage(S2CToolSpendAttributePoints.Handler.class, S2CToolSpendAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CToolSpendEnchantmentPoints.Handler.class, S2CToolSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CToolResetAttributes.Handler.class, S2CToolResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CToolResetEnchantments.Handler.class, S2CToolResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CToolBindSlot.Handler.class, S2CToolBindSlot.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CToolDatum.Handler.class, S2CToolDatum.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CLevelupMessage.Handler.class, S2CLevelupMessage.class, id++, CLIENT);

        CHANNEL.registerMessage(S2CWeaponSpentAttributePoints.Handler.class, S2CWeaponSpentAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CWeaponSpendEnchantmentPoints.Handler.class, S2CWeaponSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CWeaponResetAttributes.Handler.class, S2CWeaponResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CWeaponResetEnchantments.Handler.class, S2CWeaponResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CWeaponBindSlot.Handler.class, S2CWeaponBindSlot.class, id++, CLIENT);

        CHANNEL.registerMessage(C2SConfig.Handler.class, C2SConfig.class, id++, SERVER);

        CHANNEL.registerMessage(C2SToolType.Handler.class, C2SToolType.class, id++, SERVER);
        CHANNEL.registerMessage(C2SToolAttributePoints.Handler.class, C2SToolAttributePoints.class, id++, SERVER);
        CHANNEL.registerMessage(C2SToolEnchantmentPoints.Handler.class, C2SToolEnchantmentPoints.class, id++, SERVER);
        CHANNEL.registerMessage(C2SToolTab.Handler.class, C2SToolTab.class, id++, SERVER);
        CHANNEL.registerMessage(C2SToolResetAttributes.Handler.class, C2SToolResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(C2SToolResetEnchantments.Handler.class, C2SToolResetEnchantments.class, id++, SERVER);
        CHANNEL.registerMessage(C2SToolBindSlot.Handler.class, C2SToolBindSlot.class, id++, SERVER);

        CHANNEL.registerMessage(C2SWeaponType.Handler.class, C2SWeaponType.class, id++, SERVER);
        CHANNEL.registerMessage(C2SWeaponAttributePoints.Handler.class, C2SWeaponAttributePoints.class, id++, SERVER);
        CHANNEL.registerMessage(C2SWeaponEnchantmentPoints.Handler.class, C2SWeaponEnchantmentPoints.class, id++, SERVER);
        CHANNEL.registerMessage(C2SWeaponTAb.Handler.class, C2SWeaponTAb.class, id++, SERVER);
        CHANNEL.registerMessage(C2SWeaponResetAttributes.Handler.class, C2SWeaponResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(C2SWeaponResetEnchantments.Handler.class, C2SWeaponResetEnchantments.class, id++, SERVER);
        CHANNEL.registerMessage(C2SWeaponBindSlot.Handler.class, C2SWeaponBindSlot.class, id++, SERVER);

        if (FMLCommonHandler.instance().getSide() == CLIENT) {
            ClientRegistry.registerKeyBinding(MENU_KEY);
            RenderingRegistry.registerEntityRenderingHandler(EntitySoulDagger.class, new RenderSoulDagger.RenderFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityReachModifier.class, new RenderReachModifier.RenderFactory());
        }

        MainConfig.instance().load();
        ColorConfig.instance().load();
    }

    @EventHandler
    public static void onFMLServerStarting(final FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSoulboundArmory());
    }

    @SideOnly(CLIENT)
    public static class ResourceLocations {
        public static final ResourceLocation REACH_MODIFIER = new ResourceLocation(MOD_ID, "textures/entity/reach_modifier.png");
        public static final ResourceLocation THROWN_SOULBOUND_DAGGER = new ResourceLocation(MOD_ID, "textures/item/soulbound_dagger.png");
        public static final ResourceLocation XP_BAR = new ResourceLocation(MOD_ID, "textures/gui/xp_bar.png");
    }

    @EventBusSubscriber
    public static class Setup {
        @SubscribeEvent
        public static void onRegisterEntityEntry(final Register<EntityEntry> entry) {
            entry.getRegistry().register(EntityEntryBuilder.create()
                    .entity(EntitySoulDagger.class)
                    .id(new ResourceLocation(Main.MOD_ID, "entity_soul_dagger"), 0)
                    .name("soul dagger")
                    .tracker(512, 1, true)
                    .build()
            );
            entry.getRegistry().register(EntityEntryBuilder.create()
                    .entity(EntityReachModifier.class)
                    .id(new ResourceLocation(Main.MOD_ID, "entity_reach_extender"), 1)
                    .name("reach extender")
                    .tracker(16, 1, true)
                    .build()
            );
        }

        @SubscribeEvent
        public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            final Entity entity = event.getObject();

            if (entity instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(MOD_ID, "soulboundtool"), new SoulToolProvider());
                event.addCapability(new ResourceLocation(MOD_ID, "soulboundweapon"), new SoulWeaponProvider());
                event.addCapability(new ResourceLocation(MOD_ID, "playerconfig"), new PlayerConfigProvider());
            }

            if ((entity instanceof EntityLivingBase || entity instanceof IProjectile)
                    && !(entity instanceof EntityReachModifier) && !(entity instanceof EntitySoulDagger)
                    && entity.world instanceof WorldServer) {
                event.addCapability(new ResourceLocation(MOD_ID, "frozen"), new FrozenProvider());
            }
        }
    }
}