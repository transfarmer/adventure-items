package transfarmer.soulboundarmory.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.util.EntityUtil;

import java.util.List;

@EventBusSubscriber(modid = Main.MOD_ID)
public class GeneralEventHandlers {
    @SubscribeEvent
    public static void onGetCollsionBoxes(final GetCollisionBoxesEvent event) {
        final Entity entity = event.getEntity();

        if (!event.getWorld().isRemote && entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) entity;
            final IWeapon capability = WeaponProvider.get(player);
            final double leapForce = capability.getLeapForce();

            if (leapForce > 0) {
                final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, event.getAabb());

                for (final Entity nearbyEntity : nearbyEntities) {
                    capability.freeze(nearbyEntity, (int) (20 * leapForce), (float) EntityUtil.getVelocity(player) * (float) leapForce);
                }

                if (capability.getLeapDuration() <= 0 && player.onGround && (player.motionY <= 0.01 || player.isCreative())) {
                    capability.setLeapDuration(7);
                }

                if (player.isInLava()) {
                    capability.resetLeapForce();
                }
            }
        }
    }
}
