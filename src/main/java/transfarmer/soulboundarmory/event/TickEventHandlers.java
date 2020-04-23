package transfarmer.soulboundarmory.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;

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
        final EntityPlayer player = event.player;

        ToolProvider.get(player).onTick();
        WeaponProvider.get(player).onTick();
    }
}
