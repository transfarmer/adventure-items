package transfarmer.soulboundarmory.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.item.ItemSoulboundDagger;
import transfarmer.soulboundarmory.item.ItemSoulboundGreatsword;
import transfarmer.soulboundarmory.item.ItemSoulboundSword;
import transfarmer.soulboundarmory.item.ItemSoulboundPick;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.MOD_ID;

public class ModItems {
    public static final ItemSoulboundDagger SOULBOUND_DAGGER = new ItemSoulboundDagger("soulbound_dagger");
    public static final ItemSoulboundSword SOULBOUND_SWORD = new ItemSoulboundSword("soulbound_sword");
    public static final ItemSoulboundGreatsword SOULBOUND_GREATSWORD = new ItemSoulboundGreatsword("soulbound_greatsword");
    public static final ItemSoulboundPick SOULBOUND_PICK = new ItemSoulboundPick("soulbound_pick");

    @EventBusSubscriber(modid = MOD_ID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onRegisterItems(final Register<Item> event) {
            event.getRegistry().registerAll(SOULBOUND_DAGGER, SOULBOUND_SWORD, SOULBOUND_GREATSWORD, SOULBOUND_PICK);
        }

        @SideOnly(CLIENT)
        @SubscribeEvent
        public static void registerModels(final ModelRegistryEvent event) {
            for (final Item item : new Item[]{SOULBOUND_GREATSWORD, SOULBOUND_SWORD, SOULBOUND_DAGGER, SOULBOUND_PICK}) {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }
    }
}
