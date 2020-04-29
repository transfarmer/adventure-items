package transfarmer.soulboundarmory.item;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.entity.EntitySoulboundDagger;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.skill.Skills.SHADOW_CLONE;
import static transfarmer.soulboundarmory.skill.Skills.THROWING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;

public class ItemSoulboundDagger extends ItemSoulboundMeleeWeapon {
    public ItemSoulboundDagger(final String name) {
        super(0, -2, 0, name);
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
        final IWeaponCapability capability = WeaponProvider.get(player);

        if (!world.isRemote && capability.hasSkill(DAGGER, THROWING)) {
            player.setActiveHand(hand);

            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(@Nonnull final ItemStack itemStack, final World world, @Nonnull final EntityLivingBase entity, final int timeLeft) {
        final EntityPlayer player = (EntityPlayer) entity;
        final IWeaponCapability capability = WeaponProvider.get(player);

        if (!world.isRemote) {
            final EntitySoulboundDagger dagger = new EntitySoulboundDagger(world, entity, itemStack, capability.hasSkill(DAGGER, SHADOW_CLONE));
            final float attackSpeed = (float) capability.getAttributeTotal(DAGGER, ATTACK_SPEED);
            final float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;

            dagger.shoot(entity, entity.rotationPitch, entity.rotationYaw, velocity, velocity / attackSpeed, 0);
            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.deleteStack(itemStack);
            }
        }
    }
}
