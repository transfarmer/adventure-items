package transfarmer.soulboundarmory.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import transfarmer.soulboundarmory.Main;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static transfarmer.soulboundarmory.enchantment.EnchantmentTypes.STAFF;

@EventBusSubscriber(modid = Main.MOD_ID)
public class ModEnchantments {
    public static final Enchantment IMPACT = new EnchantmentImpact(Rarity.COMMON, STAFF, new EntityEquipmentSlot[]{MAINHAND});

    @SubscribeEvent
    public static void onRegisterEnchantment(final Register<Enchantment> event) {
        event.getRegistry().register(IMPACT);
    }
}
