package transfarmer.soulboundarmory.event;

import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;

import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;

@EventBusSubscriber(modid = Main.MOD_ID)
public class TickEventHandlers {
    @SubscribeEvent
    public static void onLivingUpdate(final LivingUpdateEvent event) {
        final IFrozen capability = FrozenProvider.get(event.getEntityLiving());

        if (capability != null) {
            event.setCanceled(capability.update());
        }

    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase == END) {
            final IItemCapability weaponCapability = WeaponProvider.get(event.player);
            final IItemCapability toolCapability = ToolProvider.get(event.player);

            weaponCapability.onTick();
            toolCapability.onTick();
        }
    }
}
