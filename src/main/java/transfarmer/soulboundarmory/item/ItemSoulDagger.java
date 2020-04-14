package transfarmer.soulboundarmory.item;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.capability.soulbound.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;

import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;

public class ItemSoulDagger extends ItemSoulWeapon {
    public ItemSoulDagger() {
        super(1, -2, 0);
    }

    @Override
    public int getMaxItemUseDuration(final ItemStack itemStack) {
        return this.getMaxItemUseDuration();
    }

    public int getMaxItemUseDuration() {
        return 1200;
    }

    public float getMaxUsageRatio(final float attackSpeed, final int timeLeft) {
        return Math.min(attackSpeed / 2 * (this.getMaxItemUseDuration() - timeLeft) / 20F, 1);
    }

    @Override
    public EnumAction getItemUseAction(final ItemStack itemStack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ISoulWeapon capability = SoulWeaponProvider.get(player);

        if (!world.isRemote && capability.getDatum(DATA.skills, DAGGER) >= 1) {
            player.setActiveHand(hand);

            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(final ItemStack itemStack, final World world, final EntityLivingBase entity, final int timeLeft) {
        final EntityPlayer player = (EntityPlayer) entity;
        final ISoulWeapon capability = SoulWeaponProvider.get(player);

        if (!world.isRemote) {
            final EntitySoulDagger dagger = new EntitySoulDagger(world, entity, itemStack, capability.getDatum(DATA.skills, DAGGER) >= 2);
            final float attackSpeed = 4 + capability.getAttribute(ATTACK_SPEED, DAGGER, true);
            final float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;

            dagger.shoot(entity, entity.rotationPitch, entity.rotationYaw, velocity, velocity / attackSpeed, 0);
            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.deleteStack(itemStack);
            }
        }
    }
}
