package transfarmer.soulboundarmory.item;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILLS;

public class ItemSoulboundDagger extends ItemSoulboundWeapon {
    public ItemSoulboundDagger(final String name) {
        super(1, -2, 0, name);
    }

    @Override
    public int getMaxItemUseDuration(@Nonnull final ItemStack itemStack) {
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
    public EnumAction getItemUseAction(@Nonnull final ItemStack itemStack) {
        return EnumAction.BOW;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(final World world, @Nonnull final EntityPlayer player, @Nonnull final EnumHand hand) {
        final IWeapon capability = WeaponProvider.get(player);

        if (!world.isRemote && capability.getDatum(DAGGER, SKILLS) >= 1) {
            player.setActiveHand(hand);

            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(@Nonnull final ItemStack itemStack, final World world, @Nonnull final EntityLivingBase entity, final int timeLeft) {
        final EntityPlayer player = (EntityPlayer) entity;
        final IWeapon capability = WeaponProvider.get(player);

        if (!world.isRemote) {
            final EntitySoulDagger dagger = new EntitySoulDagger(world, entity, itemStack, capability.getDatum(DAGGER, SKILLS) >= 2);
            final float attackSpeed = 4 + (float) capability.getAttribute(DAGGER, ATTACK_DAMAGE);
            final float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;

            dagger.shoot(entity, entity.rotationPitch, entity.rotationYaw, velocity, velocity / attackSpeed, 0);
            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.deleteStack(itemStack);
            }
        }
    }
}
