package transfarmer.adventureitems.event;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import transfarmer.adventureitems.Keybindings;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponStorage;
import transfarmer.adventureitems.item.*;
import transfarmer.adventureitems.network.WeaponTypePacket;
import transfarmer.adventureitems.network.PacketHandler;


@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {
    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(setup(new SoulBigsword(), "soul_bigsword"),
                setup(new SoulSword(), "soul_sword"),
                setup(new SoulDagger(), "soul_dagger"));
    }

    public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
        return setup(entry, new ResourceLocation(Main.MODID, name));
    }

    public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
        entry.setRegistryName(registryName);
        return entry;
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ISoulWeapon.class, new SoulWeaponStorage(), SoulWeapon::new);
        PacketHandler.INSTANCE.registerMessage(PacketHandler.id++, WeaponTypePacket.class,
                WeaponTypePacket::encode, WeaponTypePacket::new, WeaponTypePacket::handle);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Keybindings.register();
        MinecraftForge.EVENT_BUS.register(new Keybindings());
    }
}
