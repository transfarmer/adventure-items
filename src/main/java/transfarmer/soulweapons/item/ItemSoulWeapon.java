package transfarmer.soulweapons.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.Vec3d;
import transfarmer.soulweapons.entity.EntityReachModifier;

public abstract class ItemSoulWeapon extends ItemSword {
    private final float attackDamage;
    private final float attackSpeed;
    private final float reach;

    public ItemSoulWeapon(final int attackDamage, final float attackSpeed, final float reach) {
        super(ToolMaterial.WOOD);
        this.setMaxDamage(0).setNoRepair();
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.reach = reach;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemStack) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            final Vec3d look = entity.getLookVec();
            final EntityReachModifier entityReachModifier = new EntityReachModifier(entity.world, entity, this.reach);

            entityReachModifier.shoot(look.x, look.y, look.z);
            entity.world.spawnEntity(entityReachModifier);
        }

        return false;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }
}
