package transfarmer.soulboundarmory;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
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
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolStorage;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponStorage;
import transfarmer.soulboundarmory.client.render.RenderReachModifier;
import transfarmer.soulboundarmory.client.render.RenderSoulDagger;
import transfarmer.soulboundarmory.command.CommandSoulboundArmory;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.network.client.CLevelupMessage;
import transfarmer.soulboundarmory.network.client.tool.*;
import transfarmer.soulboundarmory.network.client.weapon.*;
import transfarmer.soulboundarmory.network.server.tool.*;
import transfarmer.soulboundarmory.network.server.weapon.*;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;

@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MOD_ID = "soulboundarmory";
    public static final String NAME = "soulbound armory";
    public static final String VERSION = "2.1.17-beta";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    public static int id;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);
        CapabilityManager.INSTANCE.register(ISoulCapability.class, new SoulToolStorage(), SoulTool::new);

        CHANNEL.registerMessage(SWeaponType.Handler.class, SWeaponType.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponAttributePoints.Handler.class, SWeaponAttributePoints.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponEnchantmentPoints.Handler.class, SWeaponEnchantmentPoints.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponTab.Handler.class, SWeaponTab.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponResetAttributes.Handler.class, SWeaponResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponResetEnchantments.Handler.class, SWeaponResetEnchantments.class, id++, SERVER);
        CHANNEL.registerMessage(SWeaponBindSlot.Handler.class, SWeaponBindSlot.class, id++, SERVER);

        CHANNEL.registerMessage(CWeaponType.Handler.class, CWeaponType.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponSpendAttributePoints.Handler.class, CWeaponSpendAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponSpendEnchantmentPoints.Handler.class, CWeaponSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponResetAttributes.Handler.class, CWeaponResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponResetEnchantments.Handler.class, CWeaponResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponBindSlot.Handler.class, CWeaponBindSlot.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponDatum.Handler.class, CWeaponDatum.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponData.Handler.class, CWeaponData.class, id++, CLIENT);

        CHANNEL.registerMessage(SToolType.Handler.class, SToolType.class, id++, SERVER);
        CHANNEL.registerMessage(SToolAttributePoints.Handler.class, SToolAttributePoints.class, id++, SERVER);
        CHANNEL.registerMessage(SToolEnchantmentPoints.Handler.class, SToolEnchantmentPoints.class, id++, SERVER);
        CHANNEL.registerMessage(SToolTab.Handler.class, SToolTab.class, id++, SERVER);
        CHANNEL.registerMessage(SToolResetAttributes.Handler.class, SToolResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(SToolResetEnchantments.Handler.class, SToolResetEnchantments.class, id++, SERVER);
        CHANNEL.registerMessage(SToolBindSlot.Handler.class, SToolBindSlot.class, id++, SERVER);

        CHANNEL.registerMessage(CToolSpendAttributePoints.Handler.class, CToolSpendAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolSpendEnchantmentPoints.Handler.class, CToolSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolResetAttributes.Handler.class, CToolResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolResetEnchantments.Handler.class, CToolResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolBindSlot.Handler.class, CToolBindSlot.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolDatum.Handler.class, CToolDatum.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolData.Handler.class, CToolData.class, id++, CLIENT);
        CHANNEL.registerMessage(CLevelupMessage.Handler.class, CLevelupMessage.class, id++, CLIENT);

        if (FMLCommonHandler.instance().getSide() == CLIENT) {
            ClientRegistry.registerKeyBinding(MENU_KEY);
            RenderingRegistry.registerEntityRenderingHandler(EntitySoulDagger.class, new RenderSoulDagger.RenderFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityReachModifier.class, new RenderReachModifier.RenderFactory());
        }
    }

    @EventHandler
    public static void onFMLServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSoulboundArmory());
    }

    public static class ResourceLocations {
        public static final ResourceLocation SOULBOUND_WEAPON = new ResourceLocation(MOD_ID, "soulboundweapon");
        public static final ResourceLocation SOULBOUND_TOOL = new ResourceLocation(MOD_ID, "soulboundtool");

        @SideOnly(CLIENT)
        public static final class Client {
            public static final ResourceLocation REACH_MODIFIER = new ResourceLocation(MOD_ID, "textures/entity/reach_modifier.png");
            public static final ResourceLocation THROWN_SOULBOUND_DAGGER = new ResourceLocation(MOD_ID, "textures/item/soulbound_dagger.png");
            public static final ResourceLocation XP_BAR = new ResourceLocation(MOD_ID, "textures/gui/xp_bar.png");
        }
    }
}
