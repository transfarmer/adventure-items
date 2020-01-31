package transfarmer.soulweapons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
import transfarmer.soulweapons.network.ClientAddAttribute;
import transfarmer.soulweapons.network.ClientWeaponData;
import transfarmer.soulweapons.network.ClientWeaponType;
import transfarmer.soulweapons.network.ClientWeaponXP;
import transfarmer.soulweapons.network.ServerAddAttribute;
import transfarmer.soulweapons.network.ServerWeaponLevelup;
import transfarmer.soulweapons.network.ServerWeaponType;
import transfarmer.soulweapons.tab.SoulWeaponTab;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static transfarmer.soulweapons.KeyBindings.WEAPON_MENU;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "soulweapons";
    public static final String NAME = "soul weapons";
    public static final String VERSION = "1.1.0-beta";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final CreativeTabs SOUL_WEAPON_TAB = new SoulWeaponTab();
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static int id;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);

        CHANNEL.registerMessage(ServerWeaponType.Handler.class, ServerWeaponType.class, id++, SERVER);
        CHANNEL.registerMessage(ServerWeaponLevelup.Handler.class, ServerWeaponLevelup.class, id++, SERVER);
        CHANNEL.registerMessage(ServerAddAttribute.Handler.class, ServerAddAttribute.class, id++, SERVER);
        CHANNEL.registerMessage(ClientAddAttribute.Handler.class, ClientAddAttribute.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponType.Handler.class, ClientWeaponType.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponXP.Handler.class, ClientWeaponXP.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponData.Handler.class, ClientWeaponData.class, id++, CLIENT);

        if (FMLCommonHandler.instance().getSide() == CLIENT) {
            ClientRegistry.registerKeyBinding(WEAPON_MENU);
        }
    }
}
