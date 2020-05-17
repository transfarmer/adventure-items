package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.SwordItem;

public abstract class SoulboundMeleeWeaponItem extends SwordItem implements SoulboundWeaponItem {
    protected float reach;

    public SoulboundMeleeWeaponItem(final int attackDamage, final float attackSpeed, final float reach) {
        super(ModToolMaterials.SOULBOUND, attackDamage, attackSpeed, new Settings());

        this.reach = reach;
    }

    @Override
    public Multimap<String, EntityAttributeModifier> getModifiers(final EquipmentSlot slot) {
        return this.putReach(super.getModifiers(slot), slot, this.reach);
    }
}
