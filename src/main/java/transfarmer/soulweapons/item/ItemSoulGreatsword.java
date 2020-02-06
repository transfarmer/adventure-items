package transfarmer.soulweapons.item;

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
import transfarmer.soulweapons.capability.SoulWeaponProvider;

import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponType.GREATSWORD;

public class ItemSoulGreatsword extends ItemSoulWeapon {
    public ItemSoulGreatsword() {
        super(3, -2.8F);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 200;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (player != null && player.getCapability(SoulWeaponProvider.CAPABILITY, null).getDatum(SKILLS, GREATSWORD) >= 1) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityLivingBase entity, int timeLeft) {
        final Vec3d look = entity.getLookVec();
        final int timeTaken = 200 - timeLeft;
        final double strength = Math.min(1.25, timeTaken / 15F * 1.25);

        entity.addVelocity(look.x * strength, look.y * strength / 2, look.z * strength);
    }


    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote && entity instanceof EntityPlayerSP) {
            final EntityPlayerSP playerSP = (EntityPlayerSP) entity;
            final ItemStack activeStack = playerSP.getActiveItemStack();

            if (!activeStack.isEmpty() && activeStack.getItem() == this) {
                playerSP.movementInput.moveForward *= 4.5;
                playerSP.movementInput.moveStrafe *= 4.5;
            }
        }
    }
}
