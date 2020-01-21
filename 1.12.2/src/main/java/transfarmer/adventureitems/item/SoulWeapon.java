package transfarmer.adventureitems.item;

import net.minecraft.item.ItemSword;


public class SoulWeapon extends ItemSword {
    final int harvestLevel;
    final int maxUses;
    final float efficiency;
    final float attackDamage;
    final int enchantability;

    public SoulWeapon(int attackDamage, float attackSpeed) {
        super(ToolMaterial.WOOD);
        this.harvestLevel = 0;
        this.maxUses = 0;
        this.efficiency = 1;
        this.attackDamage = attackDamage;
        this.enchantability = 0;
    }
}
