package transfarmer.soulboundarmory.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.item.ItemSoulDagger;
import transfarmer.soulboundarmory.item.ItemSoulGreatsword;
import transfarmer.soulboundarmory.item.ItemSoulboundPick;
import transfarmer.soulboundarmory.item.ItemSoulSword;

import javax.annotation.Nonnull;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.MOD_ID;


@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@ObjectHolder(MOD_ID)
public class ModItems {
    @Nonnull
    public static final ItemSoulGreatsword SOULBOUND_GREATSWORD = null;
    @Nonnull
    public static final ItemSoulSword SOULBOUND_SWORD = null;
    @Nonnull
    public static final ItemSoulDagger SOULBOUND_DAGGER = null;
    @Nonnull
    public static final ItemSoulboundPick SOULBOUND_PICK = null;

    private static final Item[] ITEMS = {SOULBOUND_DAGGER, SOULBOUND_GREATSWORD, SOULBOUND_SWORD, SOULBOUND_PICK};

    @EventBusSubscriber(modid = MOD_ID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onRegisterItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    new ItemSoulGreatsword("soulbound_greatsword"),
                    new ItemSoulSword("soulbound_sword"),
                    new ItemSoulDagger("soulbound_dagger"),
                    new ItemSoulboundPick("soulbound_pick")
            );
        }

        @SideOnly(CLIENT)
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            registerModel(ModItems.SOULBOUND_GREATSWORD, 0);
            registerModel(ModItems.SOULBOUND_SWORD, 0);
            registerModel(ModItems.SOULBOUND_DAGGER, 0);
            registerModel(ModItems.SOULBOUND_PICK, 0);
        }

        @SideOnly(CLIENT)
        private static void registerModel(Item item, int meta) {
            ModelLoader.setCustomModelResourceLocation(item, meta,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
