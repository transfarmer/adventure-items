package transfarmer.soulboundarmory.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.world.ModWorld;

import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.SWORD;

public class ItemSoulSword extends ItemSoulWeapon {
    public ItemSoulSword() {
        super(2, -2.4F, 1.5F);
    }

    @Override
    public EnumAction getItemUseAction(final ItemStack itemStack) {
        return EnumAction.BLOCK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ISoulWeapon capability = SoulWeaponProvider.get(player);

        if (!world.isRemote && capability.getDatum(DATA.skills, SWORD) >= 1 && capability.getLightningCooldown() <= 0) {
            final RayTraceResult result = ModWorld.rayTraceAll(world, player);

            if (result != null && capability.getDatum(DATA.skills, SWORD) >= 1 && capability.getLightningCooldown() <= 0) {
                player.world.addWeatherEffect(new EntitySoulLightningBolt(player.world, result.hitVec, player));
                capability.resetLightningCooldown();

                return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
        }

        return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
}
