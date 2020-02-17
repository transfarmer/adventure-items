package transfarmer.soularsenal;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfarmer.soularsenal.network.tool.client.*;
import transfarmer.soularsenal.network.tool.server.*;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolStorage;
import transfarmer.soularsenal.capability.weapon.ISoulWeapon;
import transfarmer.soularsenal.capability.weapon.SoulWeapon;
import transfarmer.soularsenal.capability.weapon.SoulWeaponStorage;
import transfarmer.soularsenal.entity.EntityReachModifier;
import transfarmer.soularsenal.entity.EntitySoulDagger;
import transfarmer.soularsenal.network.weapon.client.*;
import transfarmer.soularsenal.network.weapon.client.CWeaponResetAttributes;
import transfarmer.soularsenal.network.weapon.client.CWeaponResetEnchantments;
import transfarmer.soularsenal.network.weapon.client.CWeaponSpendAttributePoints;
import transfarmer.soularsenal.network.weapon.server.*;
import transfarmer.soularsenal.network.weapon.server.SWeaponResetAttributes;
import transfarmer.soularsenal.network.weapon.server.SWeaponTab;
import transfarmer.soularsenal.render.RenderReachModifier;
import transfarmer.soularsenal.render.RenderSoulDagger;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soularsenal.client.KeyBindings.TOOL_MENU;
import static transfarmer.soularsenal.client.KeyBindings.WEAPON_MENU;

@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MOD_ID = "soularsenal";
    public static final String NAME = "soul arsenal";
    public static final String VERSION = "2.0.0-beta";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    public static int id;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);
        CapabilityManager.INSTANCE.register(ISoulTool.class, new SoulToolStorage(), SoulTool::new);

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

        CHANNEL.registerMessage(CToolType.Handler.class, CToolType.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolSpendAttributePoints.Handler.class, CToolSpendAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolSpendEnchantmentPoints.Handler.class, CToolSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolResetAttributes.Handler.class, CToolResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolResetEnchantments.Handler.class, CToolResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolBindSlot.Handler.class, CToolBindSlot.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolDatum.Handler.class, CToolDatum.class, id++, CLIENT);
        CHANNEL.registerMessage(CToolData.Handler.class, CToolData.class, id++, CLIENT);

        if (FMLCommonHandler.instance().getSide() == CLIENT) {
            ClientRegistry.registerKeyBinding(WEAPON_MENU);
            ClientRegistry.registerKeyBinding(TOOL_MENU);
            RenderingRegistry.registerEntityRenderingHandler(EntitySoulDagger.class, new RenderSoulDagger.RenderFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityReachModifier.class, new RenderReachModifier.RenderFactory());
        }
    }
}
