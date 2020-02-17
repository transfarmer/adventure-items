package transfarmer.soularsenal.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolHelper;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;

import java.util.HashSet;

import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.XP;

public abstract class ItemSoulTool extends ItemTool {
    private final float reachDistance;

    public ItemSoulTool(final float attackDamage, final float attackSpeed, final float efficiency, final float reachDistance) {
        super(attackDamage, attackSpeed, ToolMaterial.WOOD, new HashSet<>());

        this.efficiency = efficiency;
        this.reachDistance = reachDistance;
    }

    public float getDestroySpeed(final ItemStack stack, final IBlockState state, final EntityPlayer player) {
        final ISoulTool capability = SoulToolProvider.get(player);

        for (final String type : this.getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state))
                return capability.getEffectiveEfficiency(capability.getCurrentType());
        }

        return capability.getEffectiveEfficiency(capability.getCurrentType());
    }

    @Override
    public boolean hitEntity(final ItemStack itemStack, final EntityLivingBase target, final EntityLivingBase attacker) {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(final ItemStack itemStack, final World world, final IBlockState blockState,
                                    final BlockPos blockPos, final EntityLivingBase entity) {
        int xp = Math.round(blockState.getBlockHardness(world, blockPos));

        if (entity instanceof EntityPlayer) {
            SoulToolProvider.get(entity).addDatum(xp, XP, SoulToolType.getType(itemStack));
        }

        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean getIsRepairable(final ItemStack itemStackToRepair, final ItemStack material) {
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        final Multimap<String, AttributeModifier> attributeModifiers = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            attributeModifiers.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(SoulToolHelper.REACH_DISTANCE_UUID, "Tool modifier", this.reachDistance, ADD));
        }

        return attributeModifiers;
    }

    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public float getEfficiency() {
        return this.efficiency;
    }

    public float getReachDistance() {
        return this.reachDistance;
    }
}
