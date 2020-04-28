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
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.skill.Skills.LEAPING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.GREATSWORD;

public class ItemSoulboundGreatsword extends ItemSoulboundMeleeWeapon {
    public ItemSoulboundGreatsword(final String name) {
        super(2, -3.2F, 3, name);
    }

    @Override
    public int getMaxItemUseDuration(@Nonnull final ItemStack stack) {
        return 200;
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(@Nonnull final ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(final World world, @Nonnull final EntityPlayer player, @Nonnull final EnumHand hand) {
        if (!world.isRemote && WeaponProvider.get(player).hasSkill(GREATSWORD, LEAPING)) {
            player.setActiveHand(hand);

            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(@Nonnull final ItemStack itemStack, @Nonnull final World world, @Nonnull final EntityLivingBase player, final int timeLeft) {
        final int timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            final Vec3d look = player.getLookVec();
            final float maxSpeed = 1.25F;
            final float speed = Math.min(maxSpeed, timeTaken / 20F * maxSpeed);

            player.addVelocity(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            player.setSprinting(true);
            WeaponProvider.get(player).setLeapForce(speed / maxSpeed);
        }
    }


    @Override
    public void onUpdate(@Nonnull final ItemStack itemStack, final World world, @Nonnull final Entity entity, final int itemSlot, final boolean isSelected) {
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
