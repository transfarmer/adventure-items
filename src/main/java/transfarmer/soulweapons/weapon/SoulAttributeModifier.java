package transfarmer.soulweapons.weapon;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class SoulAttributeModifier {
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static boolean attributeEquality;

    public static boolean areAttributesEqual(ItemStack itemStack0, ItemStack itemStack1, EntityEquipmentSlot slot) {
        attributeEquality = true;

        itemStack0.getAttributeModifiers(slot).forEach((String key, AttributeModifier modifier0) -> {
            if (!attributeEquality) return;

            itemStack1.getAttributeModifiers(slot).get(key).forEach((AttributeModifier modifier1) -> {
                if (!attributeEquality) return;

                if (!modifier0.getID().equals(modifier1.getID()) || modifier0.getAmount() != modifier1.getAmount()) {
                    attributeEquality = false;
                }
            });
        });

        return attributeEquality;
    }
}
