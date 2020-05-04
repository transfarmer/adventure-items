package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.util.ReflectUtil;

import javax.annotation.Nonnull;
import java.util.UUID;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;

public interface ItemSoulbound {
    @Nonnull
    UUID ATTACK_DAMAGE_MODIFIER = ReflectUtil.getFieldValue(Item.class, null, "ATTACK_DAMAGE_MODIFIER");
    @Nonnull
    UUID ATTACK_SPEED_MODIFIER = ReflectUtil.getFieldValue(Item.class, null, "ATTACK_SPEED_MODIFIER");

    default int getMainhandAttributeEntries(final ItemStack itemStack, final EntityPlayer player) {
        final Multimap<String, AttributeModifier> multimap = itemStack.getAttributeModifiers(MAINHAND);
        int entries = 0;

        for (final AttributeModifier modifier : multimap.values()) {
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
