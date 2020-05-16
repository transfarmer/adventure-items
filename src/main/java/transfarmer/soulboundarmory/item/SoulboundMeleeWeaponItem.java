package transfarmer.soulboundarmory.item;

import net.minecraft.item.SwordItem;

public abstract class SoulboundMeleeWeaponItem extends SwordItem implements SoulboundWeaponItem {
    public SoulboundMeleeWeaponItem(final int attackDamage, final float attackSpeed) {
        super(ModToolMaterials.SOULBOUND, attackDamage, attackSpeed, new Settings());
    }
}
