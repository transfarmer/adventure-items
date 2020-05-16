package transfarmer.soulboundarmory.item;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;
import transfarmer.soulboundarmory.entity.SoulboundDaggerEntity;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.skill.Skills.SHADOW_CLONE;
import static transfarmer.soulboundarmory.skill.Skills.THROWING;
import static transfarmer.soulboundarmory.statistics.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;

public class SoulboundDaggerItem extends SoulboundMeleeWeaponItem {
    public SoulboundDaggerItem() {
        super(1, -2, 0);
    }

    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return this.getMaxItemUseDuration();
    }

    public int getMaxItemUseDuration() {
        return 1200;
    }

    public float getMaxUsageRatio(final float attackSpeed, final int timeLeft) {
        return Math.min(attackSpeed / 2 * (this.getMaxItemUseDuration() - timeLeft) / 20F, 1);
    }

    @Override
    @Nonnull
    public UseAction getUseAction(@Nonnull final ItemStack itemStack) {
        return UseAction.SPEAR;
    }

    @Override
    @Nonnull
    public TypedActionResult<ItemStack> use(final World world, @Nonnull final PlayerEntity player, @Nonnull final Hand hand) {
        final IWeaponComponent component = WeaponProvider.get(player);

        if (!world.isClient && component.hasSkill(DAGGER, THROWING)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(@Nonnull final ItemStack itemStack, final World world, @Nonnull final LivingEntity entity, final int timeLeft) {
        final PlayerEntity player = (PlayerEntity) entity;
        final IWeaponComponent component = WeaponProvider.get(player);

        if (!world.isClient) {
            final SoulboundDaggerEntity dagger = new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(DAGGER, SHADOW_CLONE));
            final float attackSpeed = (float) component.getAttributeTotal(DAGGER, ATTACK_SPEED);
            final float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;

            dagger.shoot(entity, entity.pitch, entity.yaw, velocity, velocity / attackSpeed, 0);
            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.removeOne(itemStack);
            }
        }
    }
}
