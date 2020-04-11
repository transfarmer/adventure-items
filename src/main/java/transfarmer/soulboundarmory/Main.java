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
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.frozen.Frozen;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.FrozenStorage;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.tool.SoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.tool.SoulToolStorage;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponStorage;
import transfarmer.soulboundarmory.client.render.RenderReachModifier;
import transfarmer.soulboundarmory.client.render.RenderSoulDagger;
import transfarmer.soulboundarmory.command.CommandSoulboundArmory;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.network.client.CConfig;
import transfarmer.soulboundarmory.network.client.CLevelupMessage;
import transfarmer.soulboundarmory.network.client.tool.CToolBindSlot;
import transfarmer.soulboundarmory.network.client.tool.CToolDatum;
import transfarmer.soulboundarmory.network.client.tool.CToolResetAttributes;
import transfarmer.soulboundarmory.network.client.tool.CToolResetEnchantments;
import transfarmer.soulboundarmory.network.client.tool.CToolSpendAttributePoints;
import transfarmer.soulboundarmory.network.client.tool.CToolSpendEnchantmentPoints;
import transfarmer.soulboundarmory.network.client.S2CSync;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponBindSlot;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponResetAttributes;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponResetEnchantments;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponSpendAttributePoints;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponSpendEnchantmentPoints;
import transfarmer.soulboundarmory.network.server.tool.SToolAttributePoints;
import transfarmer.soulboundarmory.network.server.tool.SToolBindSlot;
import transfarmer.soulboundarmory.network.server.tool.SToolEnchantmentPoints;
import transfarmer.soulboundarmory.network.server.tool.SToolResetAttributes;
import transfarmer.soulboundarmory.network.server.tool.SToolResetEnchantments;
import transfarmer.soulboundarmory.network.server.tool.SToolTab;
import transfarmer.soulboundarmory.network.server.tool.SToolType;
import transfarmer.soulboundarmory.network.server.weapon.SWeaponAttributePoints;
import transfarmer.soulboundarmory.network.server.weapon.SWeaponBindSlot;
import transfarmer.soulboundarmory.network.server.weapon.SWeaponEnchantmentPoints;
import transfarmer.soulboundarmory.network.server.weapon.SWeaponResetAttributes;
import transfarmer.soulboundarmory.network.server.weapon.SWeaponResetEnchantments;
import transfarmer.soulboundarmory.network.server.weapon.SWeaponTab;
import transfarmer.soulboundarmory.network.server.weapon.SWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;

@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION, guiFactory = "transfarmer.soulboundarmory.config.ConfigGuiFactory")
public class Main {
    public static final String MOD_ID = "soulboundarmory";
    public static final String NAME = "soulbound armory";
    public static final String VERSION = "2.6.0";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    public static int id;

    @EventHandler
    public static void onPreinit(final FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);
        CapabilityManager.INSTANCE.register(ISoulCapability.class, new SoulToolStorage(), SoulTool::new);
        CapabilityManager.INSTANCE.register(IFrozen.class, new FrozenStorage(), Frozen::new);

        CHANNEL.registerMessage(S2CSync.Handler.class, S2CSync.class, id++, CLIENT);
        CHANNEL.registerMessage(CConfig.Handler.class, CConfig.class, id++, CLIENT);

        CHANNEL.registerMessage(CToolSpendAttributePoints.Handler.class, CToolSpendAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolSpendEnchantmentPoints.Handler.class, CToolSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolResetAttributes.Handler.class, CToolResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolResetEnchantments.Handler.class, CToolResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolBindSlot.Handler.class, CToolBindSlot.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolDatum.Handler.class, CToolDatum.class, id++, CLIENT);
        CHANNEL.registerMessage(CLevelupMessage.Handler.class, CLevelupMessage.class, id++, CLIENT);

        CHANNEL.registerMessage(CWeaponSpendAttributePoints.Handler.class, CWeaponSpendAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponSpendEnchantmentPoints.Handler.class, CWeaponSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponResetAttributes.Handler.class, CWeaponResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponResetEnchantments.Handler.class, CWeaponResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponBindSlot.Handler.class, CWeaponBindSlot.class, id++, CLIENT);

        CHANNEL.registerMessage(SToolType.Handler.class, SToolType.class, id++, SERVER);
        CHANNEL.registerMessage(SToolAttributePoints.Handler.class, SToolAttributePoints.class, id++, SERVER);
        CHANNEL.registerMessage(SToolEnchantmentPoints.Handler.class, SToolEnchantmentPoints.class, id++, SERVER);
        CHANNEL.registerMessage(SToolTab.Handler.class, SToolTab.class, id++, SERVER);
        CHANNEL.registerMessage(SToolResetAttributes.Handler.class, SToolResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(SToolResetEnchantments.Handler.class, SToolResetEnchantments.class, id++, SERVER);
        CHANNEL.registerMessage(SToolBindSlot.Handler.class, SToolBindSlot.class, id++, SERVER);

        CHANNEL.registerMessage(SWeaponType.Handler.class, SWeaponType.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponAttributePoints.Handler.class, SWeaponAttributePoints.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponEnchantmentPoints.Handler.class, SWeaponEnchantmentPoints.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponTab.Handler.class, SWeaponTab.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponResetAttributes.Handler.class, SWeaponResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponResetEnchantments.Handler.class, SWeaponResetEnchantments.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponBindSlot.Handler.class, SWeaponBindSlot.class, id++, SERVER);

        if (FMLCommonHandler.instance().getSide() == CLIENT) {
            ClientRegistry.registerKeyBinding(MENU_KEY);
            RenderingRegistry.registerEntityRenderingHandler(EntitySoulDagger.class, new RenderSoulDagger.RenderFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityReachModifier.class, new RenderReachModifier.RenderFactory());
        }

        MainConfig.instance().load();
        ColorConfig.instance().load();
    }

    @EventHandler
    public static void onFMLServerStarting(FMLServerStartingEvent event) {
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
            }

            if (entity.isNonBoss() && (entity instanceof EntityLivingBase || entity instanceof IProjectile)
                    && !(entity instanceof EntityReachModifier) && entity.world instanceof WorldServer) {
                event.addCapability(new ResourceLocation(MOD_ID, "frozen"), new FrozenProvider());
            }
        }
    }
}