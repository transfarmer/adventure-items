package transfarmer.soulboundarmory;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfarmer.soulboundarmory.client.render.RenderReachModifier;
import transfarmer.soulboundarmory.client.render.RenderSoulDagger;
import transfarmer.soulboundarmory.command.CommandSoulboundArmory;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.network.client.S2CConfig;
import transfarmer.soulboundarmory.network.client.S2CEnchant;
import transfarmer.soulboundarmory.network.client.S2COpenGUI;
import transfarmer.soulboundarmory.network.client.S2CSync;
import transfarmer.soulboundarmory.network.server.C2SAttribute;
import transfarmer.soulboundarmory.network.server.C2SBindSlot;
import transfarmer.soulboundarmory.network.server.C2SConfig;
import transfarmer.soulboundarmory.network.server.C2SEnchant;
import transfarmer.soulboundarmory.network.server.C2SItemType;
import transfarmer.soulboundarmory.network.server.C2SReset;
import transfarmer.soulboundarmory.network.server.C2STab;
import transfarmer.soulboundarmory.network.server.C2SUpgradeSkill;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;

@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION, guiFactory = "transfarmer.soulboundarmory.config.ConfigGuiFactory")
public class Main {
    public static final String MOD_ID = "soulboundarmory";
    public static final String NAME = "soulbound armory";
    public static final String VERSION = "2.8.1";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    public static int id;

    @EventHandler
    public static void onPreinit(final FMLPreInitializationEvent event) {
        CHANNEL.registerMessage(S2CConfig.Handler.class, S2CConfig.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CEnchant.Handler.class, S2CEnchant.class, id++, CLIENT);
        CHANNEL.registerMessage(S2COpenGUI.Handler.class, S2COpenGUI.class, id++, CLIENT);
        CHANNEL.registerMessage(S2CSync.Handler.class, S2CSync.class, id++, CLIENT);

        CHANNEL.registerMessage(C2SAttribute.Handler.class, C2SAttribute.class, id++, SERVER);
        CHANNEL.registerMessage(C2SBindSlot.Handler.class, C2SBindSlot.class, id++, SERVER);
        CHANNEL.registerMessage(C2SConfig.Handler.class, C2SConfig.class, id++, SERVER);
        CHANNEL.registerMessage(C2SEnchant.Handler.class, C2SEnchant.class, id++, SERVER);
        CHANNEL.registerMessage(C2SItemType.Handler.class, C2SItemType.class, id++, SERVER);
        CHANNEL.registerMessage(C2SReset.Handler.class, C2SReset.class, id++, SERVER);
        CHANNEL.registerMessage(C2STab.Handler.class, C2STab.class, id++, SERVER);
        CHANNEL.registerMessage(C2SUpgradeSkill.Handler.class, C2SUpgradeSkill.class, id++, SERVER);

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
}