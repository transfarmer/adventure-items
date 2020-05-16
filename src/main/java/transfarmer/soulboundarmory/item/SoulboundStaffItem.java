package transfarmer.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;
import transfarmer.soulboundarmory.entity.SoulboundFireballEntity;

import javax.annotation.Nonnull;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;

public class SoulboundStaffItem extends StaffItem implements SoulboundWeaponItem {
    @Override
    public boolean postHit(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity user, final Hand hand) {
        if (!world.isClient) {
            final IWeaponComponent component = WeaponProvider.get(user);

            if (component.getFireballCooldown() <= 0) {
                world.spawnEntity(new SoulboundFireballEntity(world, user, component.getSpell()));

                if (!user.isCreative()) {
                    component.resetFireballCooldown();
                }

                return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    @Nonnull
    public Multimap<String, EntityAttributeModifier> getModifiers(@Nonnull final EquipmentSlot slot) {
        final Multimap<String, EntityAttributeModifier> modifiers = HashMultimap.create();

        if (slot == MAINHAND) {
            modifiers.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(SoulboundItem.ATTACK_SPEED_MODIFIER, "generic.attackSpeed", 0, ADDITION));
            modifiers.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(SoulboundItem.ATTACK_DAMAGE_MODIFIER, "generic.attackDamage", 0, ADDITION));
        }

        return modifiers;
    }
}
