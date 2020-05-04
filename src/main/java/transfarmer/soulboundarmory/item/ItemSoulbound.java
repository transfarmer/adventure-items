package transfarmer.soulboundarmory.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.util.ReflectUtil;

import java.util.UUID;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;

public interface ItemSoulbound {
    UUID ATTACK_SPEED_MODIFIER = ReflectUtil.getAttackSpeedModifier();
    UUID ATTACK_DAMAGE_MODIFIER = ReflectUtil.getAttackDamageModifier();

    default int getMainhandAttributeEntries(final ItemStack itemStack, final EntityPlayer player) {
        int entries = 0;

        for (final AttributeModifier modifier : itemStack.getAttributeModifiers(MAINHAND).values()) {
            double amount = modifier.getAmount();
            boolean flag = false;

            if (modifier.getID() == ATTACK_DAMAGE_MODIFIER) {
                amount += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                amount += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
                flag = true;
            } else if (modifier.getID() == ATTACK_SPEED_MODIFIER) {
                amount += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                flag = true;
            }

            if (flag || amount != 0D) {
                entries++;
            }
        }

        return entries;
    }
}
