package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.Vec3d;
import transfarmer.soulboundarmory.entity.EntityReachModifier;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.*;

public abstract class ItemSoulWeapon extends ItemSword implements ISoulItem {
    private final float attackDamage;
    private final float attackSpeed;
    private final float reachDistance;

    public ItemSoulWeapon(final int attackDamage, final float attackSpeed, final float reachDistance) {
        super(ToolMaterial.WOOD);
        this.setMaxDamage(0).setNoRepair();
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.reachDistance = reachDistance;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemStack) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            final Vec3d look = entity.getLookVec();
            final EntityReachModifier entityReachModifier = new EntityReachModifier(entity.world, entity, this.reachDistance);

            entityReachModifier.shoot(look.x, look.y, look.z);
            entity.world.spawnEntity(entityReachModifier);
        }

        return false;
    }

    @Override
    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    @Override
    public float getReachDistance() {
        return this.reachDistance;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(final EntityEquipmentSlot slot, final ItemStack itemStack) {
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_UUID, "generic.attackSpeed", this.attackSpeed, ADD), MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_UUID, "generic.attackDamage", this.attackDamage, ADD), MAINHAND);
        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.reachDistance - 3, ADD), MAINHAND);

        return itemStack.getAttributeModifiers(MAINHAND);
    }
}
