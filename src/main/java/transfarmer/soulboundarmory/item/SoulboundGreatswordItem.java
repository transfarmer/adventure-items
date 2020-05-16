package transfarmer.soulboundarmory.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.skill.Skills.LEAPING;
import static transfarmer.soulboundarmory.statistics.Item.GREATSWORD;

public class SoulboundGreatswordItem extends SoulboundMeleeWeaponItem {
    public SoulboundGreatswordItem() {
        super(5, -3.2F, 3);
    }

    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return 200;
    }

    @Override
    @Nonnull
    public UseAction getUseAction(@Nonnull final ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    @Nonnull
    public TypedActionResult<ItemStack> use(final World world, @Nonnull final PlayerEntity player, @Nonnull final Hand hand) {
        if (!world.isClient && WeaponProvider.get(player).hasSkill(GREATSWORD, LEAPING)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(@Nonnull final ItemStack itemStack, @Nonnull final World world, @Nonnull final LivingEntity player, final int timeLeft) {
        final int timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            final Vec3d look = player.getRotationVector();
            final float maxSpeed = 1.25F;
            final float speed = Math.min(maxSpeed, timeTaken / 20F * maxSpeed);

            player.addVelocity(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            player.setSprinting(true);
            WeaponProvider.get(player).setLeapForce(speed / maxSpeed);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void inventoryTick(@Nonnull final ItemStack itemStack, final World world, @Nonnull final Entity entity, final int itemSlot, final boolean isSelected) {
        if (world.isClient && isSelected) {
            final ClientPlayerEntity player = (ClientPlayerEntity) entity;
            final ItemStack activeStack = player.getActiveItem();

            if (!activeStack.isEmpty() && activeStack.getItem() == this) {
                player.forwardSpeed *= 4.5;
                player.sidewaysSpeed *= 4.5;
            }
        }
    }
}
