package user11681.soulboundarmory.item;


import javax.annotation.Nonnull;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;
import user11681.soulboundarmory.registry.Skills;

import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_SPEED;

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
        final DaggerStorage component = Components.WEAPON_COMPONENT.get(player).getStorage(StorageType.DAGGER_STORAGE);

        if (!world.isClient && component.hasSkill(Skills.THROWING)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(@Nonnull final ItemStack itemStack, final World world, @Nonnull final LivingEntity entity, final int timeLeft) {
        final PlayerEntity player = (PlayerEntity) entity;
        final DaggerStorage component = DaggerStorage.get(player);

        if (!world.isClient) {
            final float attackSpeed = (float) component.getAttributeTotal(ATTACK_SPEED);
            final float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;
            final float maxVelocity = velocity / attackSpeed;
            final SoulboundDaggerEntity dagger = new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(Skills.SHADOW_CLONE), velocity, maxVelocity);

            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.removeOne(itemStack);
            }
        }
    }
}
