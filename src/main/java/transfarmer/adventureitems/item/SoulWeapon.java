package transfarmer.adventureitems.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import transfarmer.adventureitems.ModItemTier;
import transfarmer.adventureitems.init.ModItemGroups;


public class SoulWeapon extends SwordItem {
    protected int critical;
    protected int lvl;
    protected float attackDamage;
    protected float attackSpeed;
    protected float knockback = 0.1F;
    protected float hardness = 1.5F;

    public SoulWeapon(int attackDamage, float attackSpeed) {
        super(ModItemTier.SOUL, attackDamage, attackSpeed, new Item.Properties().group(ItemGroup.COMBAT).group(ModItemGroups.MOD_ITEM_GROUP));
    }

    @Override
    public boolean hitEntity(ItemStack itemStack, LivingEntity target, LivingEntity atacker) {
        return true;
    }
}
