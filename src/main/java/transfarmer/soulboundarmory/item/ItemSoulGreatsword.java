package transfarmer.soulboundarmory.item;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.capability.soulbound.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulWeaponDatum.WEAPON_DATA;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.GREATSWORD;

public class ItemSoulGreatsword extends ItemSoulWeapon {
    public ItemSoulGreatsword() {
        super(3, -3.2F, 3);
    }

    @Override
    public int getMaxItemUseDuration(final ItemStack stack) {
        return 200;
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(final ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(final World world, @Nonnull final EntityPlayer player, @Nonnull final EnumHand hand) {
        if (!world.isRemote && SoulWeaponProvider.get(player).getDatum(WEAPON_DATA.skills, GREATSWORD) >= 1) {
            player.setActiveHand(hand);

            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(@Nonnull final ItemStack itemStack, @Nonnull final World world, @Nonnull final EntityLivingBase entity, final int timeLeft) {
        final int timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            final Vec3d look = entity.getLookVec();
            final float maxSpeed = 1.25F;
            final float speed = Math.min(maxSpeed, timeTaken / 20F * maxSpeed);

            entity.addVelocity(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            entity.setSprinting(true);

            if (entity instanceof EntityPlayer) {
                final ISoulWeapon capability = SoulWeaponProvider.get(entity);

                capability.setLeapForce(maxSpeed / speed);
            }
        }
    }


    @Override
    public void onUpdate(final ItemStack itemStack, final World world, final Entity entity, final int itemSlot, final boolean isSelected) {
        if (world.isRemote && isSelected) {
            final EntityPlayerSP player = (EntityPlayerSP) entity;
            final ItemStack activeStack = player.getActiveItemStack();

            if (!activeStack.isEmpty() && activeStack.getItem() == this) {
                player.movementInput.moveForward *= 4.5;
                player.movementInput.moveStrafe *= 4.5;
            }
        }
    }
}
