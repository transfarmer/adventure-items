package transfarmer.adventureitems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponStorage;
import transfarmer.adventureitems.init.ModItems;
import transfarmer.adventureitems.item.SoulBigsword;
import transfarmer.adventureitems.item.SoulDagger;
import transfarmer.adventureitems.item.SoulSword;
import transfarmer.adventureitems.network.ClientWeaponData;
import transfarmer.adventureitems.network.ClientWeaponLevelup;
import transfarmer.adventureitems.network.ClientWeaponType;
import transfarmer.adventureitems.network.ServerWeaponLevelup;
import transfarmer.adventureitems.network.ServerWeaponType;
import transfarmer.adventureitems.tabs.SoulWeaponTab;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "adventureitems";
    public static final String NAME = "adventure items";
    public static final String VERSION = "0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final CreativeTabs SOUL_WEAPON_TAB = new SoulWeaponTab();
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static int id;

    @SideOnly(CLIENT)
    public static final KeyBinding KEY_BINDING = new KeyBinding("key.adventureitems.attributes", Keyboard.KEY_R, "adventure items");

    @EventHandler
    public static void commonPreInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);

        CHANNEL.registerMessage(ServerWeaponType.Handler.class, ServerWeaponType.class, id++, Side.SERVER);
        CHANNEL.registerMessage(ServerWeaponLevelup.Handler.class, ServerWeaponLevelup.class, id++, Side.SERVER);
        CHANNEL.registerMessage(ClientWeaponType.Handler.class, ClientWeaponType.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponLevelup.Handler.class, ClientWeaponLevelup.class, id++, CLIENT);
        CHANNEL.registerMessage(ClientWeaponData.Handler.class, ClientWeaponData.class, id++, CLIENT);
    }

    @SideOnly(CLIENT)
    @EventHandler
    public static void clientPreInit(FMLPreInitializationEvent event) {
        ClientRegistry.registerKeyBinding(KEY_BINDING);
    }

    @EventBusSubscriber(modid = MODID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onRegisterItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    setup(new SoulBigsword(), "soul_bigsword"),
                    setup(new SoulSword(), "soul_sword"),
                    setup(new SoulDagger(), "soul_dagger"));
        }

        public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
            return setup(entry, new ResourceLocation(MODID, name));
        }

        public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
            entry.setRegistryName(registryName);
            return entry;
        }
    }

    @EventBusSubscriber(value = CLIENT, modid = Main.MODID)
    public static class ClientRegistrationHandler {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            registerModel(ModItems.SOUL_BIGSWORD, 0);
            registerModel(ModItems.SOUL_SWORD, 0);
            registerModel(ModItems.SOUL_DAGGER, 0);
        }

        public static void registerModel(Item item, int meta) {
            ModelLoader.setCustomModelResourceLocation(item, meta,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
