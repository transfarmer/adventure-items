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
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.entity.EntityReachModifier;

import javax.annotation.Nonnull;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;

public abstract class ItemSoulboundMeleeWeapon extends ItemSword implements SoulboundWeapon {
    private final float attackDamage;
    private final float attackSpeed;
    private final float reachDistance;

    public ItemSoulboundMeleeWeapon(final int attackDamage, final float attackSpeed, final float reachDistance, final String name) {
        super(ToolMaterials.SOULBOUND);

        this.setRegistryName(Main.MOD_ID, name);
        this.setTranslationKey(String.format("%s.%s", Main.MOD_ID, name));

        this.setNoRepair();
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.reachDistance = reachDistance;
    }

    @Override
    public boolean onEntitySwing(final EntityLivingBase entity, @Nonnull final ItemStack itemStack) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            final Vec3d look = entity.getLookVec();
            final EntityReachModifier entityReachModifier = new EntityReachModifier(entity.world, entity, 4 + this.reachDistance);

            entityReachModifier.shoot(look.x, look.y, look.z);
            entity.world.spawnEntity(entityReachModifier);
        }

        return false;
    }

    @Override
    @NotNull
    public Multimap<String, AttributeModifier> getAttributeModifiers(@NotNull final EntityEquipmentSlot slot, final ItemStack itemStack) {
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SoulboundItemUtil.ATTACK_SPEED_UUID, "generic.attackSpeed", this.attackSpeed, ADD), MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(SoulboundItemUtil.ATTACK_DAMAGE_UUID, "generic.attackDamage", this.attackDamage, ADD), MAINHAND);
        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(SoulboundItemUtil.REACH_DISTANCE_UUID, "generic.reachDistance", this.reachDistance, ADD), MAINHAND);

        return itemStack.getAttributeModifiers(MAINHAND);
    }
}
