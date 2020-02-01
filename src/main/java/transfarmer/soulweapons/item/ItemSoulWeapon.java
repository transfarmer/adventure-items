package transfarmer.soulweapons.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public abstract class ItemSoulWeapon extends ItemSword {
    private final float attackDamage;
    private final float attackSpeed;

    public ItemSoulWeapon(final int attackDamage, final float attackSpeed) {
        super(ToolMaterial.WOOD);
        this.setMaxDamage(0).setNoRepair();
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    public int getItemEnchantability() {
        return 0;
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }
}
