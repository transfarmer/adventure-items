package transfarmer.soulboundarmory.item;

import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import transfarmer.soulboundarmory.capability.weapon.*;

import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulWeaponDatum.*;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.*;

public class ItemSoulGreatsword extends ItemSoulWeapon {
    public ItemSoulGreatsword() {
        super(3, -3.2F, 3);
    }

    @Override
    public int getMaxItemUseDuration(final ItemStack stack) {
        return 200;
    }

    @Override
    public EnumAction getItemUseAction(final ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        if (!world.isRemote && player != null && SoulWeaponProvider.get(player).getDatum(WEAPON_DATA.skills, GREATSWORD) >= 1) {
            player.setActiveHand(hand);

            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(final ItemStack itemStack, final World world, final EntityLivingBase entity, final int timeLeft) {
        final int timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            final Vec3d look = entity.getLookVec();
            final float maxSpeed = 1.25F;
            final float speed = Math.min(maxSpeed, timeTaken / 15F * maxSpeed);

            entity.addVelocity(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            entity.setSprinting(true);

            if (entity instanceof EntityPlayer) {
                final ISoulWeapon capability = SoulWeaponProvider.get(entity);

                capability.setCharging(maxSpeed / speed);
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
