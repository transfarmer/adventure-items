package transfarmer.adventureitems;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public enum ModItemTier implements IItemTier {
    SOUL(0, 0, 0F, 0F, 0, null);

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final LazyValue<Ingredient> repairMaterial;

    private ModItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairMaterial = new LazyValue<>(repairMaterial);
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public int getEnchantability() {
        return enchantability;
    }

    public Ingredient getRepairMaterial() {
        return repairMaterial.getValue();
    }
}
