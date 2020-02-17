package transfarmer.soulweapons;

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
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponStorage;
import transfarmer.soulweapons.entity.EntityReachModifier;
import transfarmer.soulweapons.entity.EntitySoulDagger;
import transfarmer.soulweapons.network.client.*;
import transfarmer.soulweapons.network.server.*;
import transfarmer.soulweapons.render.RenderReachModifier;
import transfarmer.soulweapons.render.RenderSoulDagger;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soulweapons.client.KeyBindings.WEAPON_MENU;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "soulweapons";
    public static final String NAME = "soul weapons";
    public static final String VERSION = "1.8.3-beta";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static int id;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);

        CHANNEL.registerMessage(SWeaponType.Handler.class, SWeaponType.class, id++, SERVER);
        CHANNEL.registerMessage(SAttributePoints.Handler.class, SAttributePoints.class, id++, SERVER);
        CHANNEL.registerMessage(SEnchantmentPoints.Handler.class, SEnchantmentPoints.class, id++, SERVER);
        CHANNEL.registerMessage(STab.Handler.class, STab.class, id++, SERVER);
        CHANNEL.registerMessage(SResetAttributes.Handler.class, SResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(SResetEnchantments.Handler.class, SResetEnchantments.class, id++, SERVER);
        CHANNEL.registerMessage(SBindSlot.Handler.class, SBindSlot.class, id++, SERVER);

        CHANNEL.registerMessage(CWeaponType.Handler.class, CWeaponType.class, id++, CLIENT);
        CHANNEL.registerMessage(CSpendAttributePoints.Handler.class, CSpendAttributePoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CSpendEnchantmentPoints.Handler.class, CSpendEnchantmentPoints.class, id++, CLIENT);
        CHANNEL.registerMessage(CResetAttributes.Handler.class, CResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(CResetEnchantments.Handler.class, CResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(CBindSlot.Handler.class, CBindSlot.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponDatum.Handler.class, CWeaponDatum.class, id++, CLIENT);
        CHANNEL.registerMessage(CWeaponData.Handler.class, CWeaponData.class, id++, CLIENT);

        if (FMLCommonHandler.instance().getSide() == CLIENT) {
            ClientRegistry.registerKeyBinding(WEAPON_MENU);
            RenderingRegistry.registerEntityRenderingHandler(EntitySoulDagger.class, new RenderSoulDagger.RenderFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityReachModifier.class, new RenderReachModifier.RenderFactory());
        }
    }
}
