package transfarmer.soulweapons.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.entity.EntitySoulLightningBolt;
import transfarmer.soulweapons.world.ModWorld;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponType.SWORD;

public class ItemSoulSword extends ItemSoulWeapon {
    public ItemSoulSword() {
        super(2, -2.4F, 4.5F);
    }

    @Override
    public int getMaxItemUseDuration(final ItemStack itemStack) {
        return 1200;
    }

    @Override
    public EnumAction getItemUseAction(final ItemStack itemStack) {
        return EnumAction.BLOCK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ISoulWeapon capability = player.getCapability(CAPABILITY, null);

        if (!world.isRemote && capability.getDatum(SKILLS, SWORD) >= 1 && capability.getLightningCooldown() <= 0) {
            final RayTraceResult result = ModWorld.rayTraceAll(world, player);

            if (result != null && capability.getDatum(SKILLS, SWORD) >= 1 && capability.getLightningCooldown() <= 0) {
                player.world.addWeatherEffect(new EntitySoulLightningBolt(player.world, result.hitVec, player));
                capability.resetLightningCooldown();

                return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
        }

        return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
}
