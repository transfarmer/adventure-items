package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.CLevelupMessage;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import javax.annotation.Nullable;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolType.PICK;

public class ItemSoulPick extends ItemPickaxe implements IItemSoulTool {
    private final float reachDistance;
    private final String toolClass = "pickaxe";

    public ItemSoulPick() {
        super(ToolMaterial.DIAMOND);

        this.attackDamage = 0;
        this.attackSpeed = -2.4F;
        this.efficiency = 0.5F;
        this.reachDistance = -1;
    }

    @Override
    public boolean hitEntity(final ItemStack itemStack, final EntityLivingBase target, final EntityLivingBase attacker) {
        return false;
    }

    @Override
    public boolean isEffectiveAgainst(final IBlockState blockState) {
        final Material material = blockState.getMaterial();

        return material == Material.IRON || material == Material.ANVIL || material == Material.ROCK;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        if (player != null) {
            return (int) SoulToolProvider.get(player).getAttribute(HARVEST_LEVEL, PICK);
        }

        return 0;
    }

    @Override
    public boolean canHarvestBlock(final IBlockState blockState, final EntityPlayer player) {
        return this.getHarvestLevel(null, this.toolClass, player, blockState) >= blockState.getBlock().getHarvestLevel(blockState);
    }

    @Override
    public boolean onBlockDestroyed(final ItemStack itemStack, final World world, final IBlockState blockState,
                                    final BlockPos blockPos, final EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && this.isEffectiveAgainst(blockState)
                && this.canHarvestBlock(blockState, (EntityPlayer) entity)) {
            final ISoulCapability capability = SoulToolProvider.get(entity);
            final SoulType type = SoulToolType.get(itemStack);
            final int xp = Math.min(Math.round(blockState.getBlockHardness(world, blockPos)), 5);

            if (capability.addDatum(xp, DATA.xp, type) && !world.isRemote && MainConfig.instance().getLevelupNotifications()) {
                Main.CHANNEL.sendTo(new CLevelupMessage(itemStack, capability.getDatum(DATA.level, type)), (EntityPlayerMP) entity);
            }

            capability.sync();
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
    public Multimap<String, AttributeModifier> getAttributeModifiers(final EntityEquipmentSlot equipmentSlot, final ItemStack itemStack) {
        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(SoulItemHelper.REACH_DISTANCE_UUID, "generic.reachDistance", this.reachDistance, ADD), MAINHAND);

        return itemStack.getAttributeModifiers(MAINHAND);
    }

    @Override
    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    @Override
    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public float getEfficiency() {
        return this.efficiency;
    }

    @Override
    public float getReachDistance() {
        return this.reachDistance;
    }
}
