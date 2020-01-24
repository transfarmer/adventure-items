package transfarmer.soulweapons;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponStorage;
import transfarmer.soulweapons.network.ClientWeaponData;
import transfarmer.soulweapons.network.ClientWeaponLevelup;
import transfarmer.soulweapons.network.ClientWeaponType;
import transfarmer.soulweapons.network.ServerWeaponLevelup;
import transfarmer.soulweapons.network.ServerWeaponType;
import transfarmer.soulweapons.tab.SoulWeaponTab;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "soulweapons";
    public static final String NAME = "soul weapons";
    public static final String VERSION = "0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final CreativeTabs SOUL_WEAPON_TAB = new SoulWeaponTab();
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static int id;

    @EventHandler
    public static void commonPreInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);

        CHANNEL.registerMessage(ServerWeaponType.Handler.class, ServerWeaponType.class, id++, SERVER);
        CHANNEL.registerMessage(ServerWeaponLevelup.Handler.class, ServerWeaponLevelup.class, id++, SERVER);
        CHANNEL.registerMessage(ClientWeaponType.Handler.class, ClientWeaponType.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponLevelup.Handler.class, ClientWeaponLevelup.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponData.Handler.class, ClientWeaponData.class, id++, CLIENT);
    }

    @SideOnly(CLIENT)
    public static class KeyBindings {
        public static final KeyBinding WEAPON_MENU = new KeyBinding("key.soulweapons.attributes", Keyboard.KEY_R, "soul weapons");

        @EventHandler
        public static void preInit(FMLPreInitializationEvent event) {
            ClientRegistry.registerKeyBinding(KeyBindings.WEAPON_MENU);
        }
    }
}
