package transfarmer.soulweapons.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.item.SoulBigsword;
import transfarmer.soulweapons.item.SoulDagger;
import transfarmer.soulweapons.item.SoulSword;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.Main.MODID;


@ObjectHolder(MODID)
public class ModItems {
    public static final Item SOUL_BIGSWORD = null;
    public static final Item SOUL_SWORD = null;
    public static final Item SOUL_DAGGER = null;

    @EventBusSubscriber(modid = MODID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onRegisterItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                setup(new SoulBigsword(), "soul_bigsword"),
                setup(new SoulSword(), "soul_sword"),
                setup(new SoulDagger(), "soul_dagger"));
        }

        public static Item setup(Item item, String name) {
            return item.setRegistryName(MODID, name).setCreativeTab(Main.SOUL_WEAPON_TAB);
        }

        @SideOnly(CLIENT)
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            registerModel(ModItems.SOUL_BIGSWORD, 0);
            registerModel(ModItems.SOUL_SWORD, 0);
            registerModel(ModItems.SOUL_DAGGER, 0);
        }

        @SideOnly(CLIENT)
        private static void registerModel(Item item, int meta) {
            ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
