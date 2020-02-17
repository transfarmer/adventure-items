package transfarmer.soularsenal.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soularsenal.item.ItemSoulPick;
import transfarmer.soularsenal.item.ItemSoulTool;
import transfarmer.soularsenal.item.ItemSoulDagger;
import transfarmer.soularsenal.item.ItemSoulGreatsword;
import transfarmer.soularsenal.item.ItemSoulSword;
import transfarmer.soularsenal.item.ItemSoulWeapon;

import javax.annotation.Nonnull;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soularsenal.Main.MOD_ID;


@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@ObjectHolder(MOD_ID)
public class ModItems {
    @Nonnull
    public static final ItemSoulWeapon SOUL_GREATSWORD = null;
    @Nonnull
    public static final ItemSoulWeapon SOUL_SWORD = null;
    @Nonnull
    public static final ItemSoulWeapon SOUL_DAGGER = null;
    @Nonnull
    public static final ItemSoulPick SOUL_PICK = null;

    @EventBusSubscriber(modid = MOD_ID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onRegisterItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    setup(new ItemSoulGreatsword(), "soul_greatsword"),
                    setup(new ItemSoulSword(), "soul_sword"),
                    setup(new ItemSoulDagger(), "soul_dagger"),
                    setup(new ItemSoulPick(), "soul_pick")
            );
        }

        public static Item setup(ItemSoulWeapon item, String name) {
            return item.setRegistryName(MOD_ID, name).setCreativeTab(CreativeTabs.COMBAT)
                    .setTranslationKey(String.format("%s.%s", MOD_ID, name));
        }

        public static Item setup(ItemSoulTool item, String name) {
            return item.setRegistryName(MOD_ID, name).setCreativeTab(CreativeTabs.TOOLS)
                    .setTranslationKey(String.format("%s.%s", MOD_ID, name));
        }

        @SideOnly(CLIENT)
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            registerModel(ModItems.SOUL_GREATSWORD, 0);
            registerModel(ModItems.SOUL_SWORD, 0);
            registerModel(ModItems.SOUL_DAGGER, 0);
            registerModel(ModItems.SOUL_PICK, 0);
        }

        @SideOnly(CLIENT)
        private static void registerModel(Item item, int meta) {
            ModelLoader.setCustomModelResourceLocation(item, meta,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
