package transfarmer.soulboundarmory.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.RayTraceContext.FluidHandling;
import net.minecraft.world.RayTraceContext.ShapeType;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.component.soulbound.item.ISwordComponent;
import transfarmer.soulboundarmory.entity.SoulboundLightningEntity;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.skill.Skills.SUMMON_LIGHTNING;

public class SoulboundSwordItem extends SoulboundMeleeWeaponItem {
    public SoulboundSwordItem() {
        super(3, -2.4F, 1.5F);
    }

    @Override
    @Nonnull
    public UseAction getUseAction(@Nonnull final ItemStack itemStack) {
        return UseAction.BLOCK;
    }

    @Override
    @Nonnull
    public TypedActionResult<ItemStack> use(final World world, @Nonnull final PlayerEntity player, @Nonnull final Hand hand) {
        final ISwordComponent component = (ISwordComponent) ISoulboundItemComponent.get(player, this);

        if (!world.isClient && component.hasSkill(SUMMON_LIGHTNING) && component.getLightningCooldown() <= 0) {
            final Vec3d pos = player.getPos();
            final HitResult result = world.rayTrace(new RayTraceContext(pos, pos.add(player.getRotationVector()).multiply(512), ShapeType.COLLIDER, FluidHandling.NONE, player));


            if (result != null) {
                ((ServerWorld) player.world).addLightning(new SoulboundLightningEntity(player.world, result.getPos(), player.getUuid()));
                component.resetLightningCooldown();

                return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
            }
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }
}
