package user11681.soulboundarmory.item;

import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import user11681.usersmanual.item.ItemModifiers;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static user11681.soulboundarmory.Main.ATTACK_RANGE_MODIFIER_UUID;

public interface SoulboundItem {
    default int getMainhandAttributeEntries(final ItemStack itemStack, final PlayerEntity player) {
        int entries = 0;

        for (final EntityAttributeModifier modifier : itemStack.getAttributeModifiers(MAINHAND).values()) {
            double amount = modifier.getAmount();
            boolean flag = false;

            if (modifier.getId() == ItemModifiers.ATTACK_DAMAGE_MODIFIER_UUID) {
                amount += player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getBaseValue();
                amount += EnchantmentHelper.getAttackDamage(itemStack, EntityGroup.DEFAULT);
                flag = true;
            } else if (modifier.getId() == ItemModifiers.ATTACK_SPEED_MODIFIER_UUID) {
                amount += player.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getBaseValue();
                flag = true;
            }

            if (flag || amount != 0D) {
                entries++;
            }
        }

        return entries;
    }

    default Multimap<String, EntityAttributeModifier> putReach(
            final Multimap<String, EntityAttributeModifier> modifiers, final EquipmentSlot slot, final float reach) {
        if (slot == MAINHAND) {
            modifiers.put(ReachEntityAttributes.ATTACK_RANGE.getId(), new EntityAttributeModifier(ATTACK_RANGE_MODIFIER_UUID, "Weapon modifier", reach, ADDITION));
        }

        return modifiers;
    }
}
