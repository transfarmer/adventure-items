package transfarmer.soulboundarmory.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.world.ModWorld;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.init.Skills.SUMMON_LIGHTNING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.SWORD;

public class ItemSoulboundSword extends ItemSoulboundMeleeWeapon {
    public ItemSoulboundSword(final String name) {
        super(1, -2.4F, 1.5F, name);
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(@Nonnull final ItemStack itemStack) {
        return EnumAction.BLOCK;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(final World world, @Nonnull final EntityPlayer player, @Nonnull final EnumHand hand) {
        final IWeaponCapability capability = WeaponProvider.get(player);

        if (!world.isRemote && capability.hasSkill(SWORD, SUMMON_LIGHTNING) && capability.getLightningCooldown() <= 0) {
            final RayTraceResult result = ModWorld.rayTraceAll(world, player);

            if (result != null) {
                player.world.addWeatherEffect(new EntitySoulLightningBolt(player.world, result.hitVec, player.getUniqueID()));
                capability.resetLightningCooldown();

                return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
        }

        return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
}
