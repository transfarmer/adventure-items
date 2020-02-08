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
import transfarmer.soulweapons.network.ClientResetAttributes;
import transfarmer.soulweapons.network.ClientResetEnchantments;
import transfarmer.soulweapons.network.ClientSpendAttributePoint;
import transfarmer.soulweapons.network.ClientSpendEnchantmentPoint;
import transfarmer.soulweapons.network.ClientWeaponData;
import transfarmer.soulweapons.network.ClientWeaponDatum;
import transfarmer.soulweapons.network.ClientWeaponType;
import transfarmer.soulweapons.network.ServerResetAttributes;
import transfarmer.soulweapons.network.ServerResetEnchantments;
import transfarmer.soulweapons.network.ServerSpendAttributePoint;
import transfarmer.soulweapons.network.ServerSpendEnchantmentPoint;
import transfarmer.soulweapons.network.ServerTab;
import transfarmer.soulweapons.network.ServerWeaponType;
import transfarmer.soulweapons.render.RenderReachModifier;
import transfarmer.soulweapons.render.RenderSoulDagger;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soulweapons.client.KeyBindings.WEAPON_MENU;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "soulweapons";
    public static final String NAME = "soul weapons";
    public static final String VERSION = "1.6.8-beta";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static int id;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);

        CHANNEL.registerMessage(ServerWeaponType.Handler.class, ServerWeaponType.class, id++, SERVER);
        CHANNEL.registerMessage(ServerSpendAttributePoint.Handler.class, ServerSpendAttributePoint.class, id++, SERVER);
        CHANNEL.registerMessage(ServerSpendEnchantmentPoint.Handler.class, ServerSpendEnchantmentPoint.class, id++, SERVER);
        CHANNEL.registerMessage(ServerTab.Handler.class, ServerTab.class, id++, SERVER);
        CHANNEL.registerMessage(ServerResetAttributes.Handler.class, ServerResetAttributes.class, id++, SERVER);
        CHANNEL.registerMessage(ServerResetEnchantments.Handler.class, ServerResetEnchantments.class, id++, SERVER);

        CHANNEL.registerMessage(ClientWeaponType.Handler.class, ClientWeaponType.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientSpendAttributePoint.Handler.class, ClientSpendAttributePoint.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientSpendEnchantmentPoint.Handler.class, ClientSpendEnchantmentPoint.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientResetAttributes.Handler.class, ClientResetAttributes.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientResetEnchantments.Handler.class, ClientResetEnchantments.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponDatum.Handler.class, ClientWeaponDatum.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponData.Handler.class, ClientWeaponData.class, id++, CLIENT);

        if (FMLCommonHandler.instance().getSide() == CLIENT) {
            ClientRegistry.registerKeyBinding(WEAPON_MENU);
            RenderingRegistry.registerEntityRenderingHandler(EntitySoulDagger.class, new RenderSoulDagger.RenderFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityReachModifier.class, new RenderReachModifier.RenderFactory());
        }
    }
}
