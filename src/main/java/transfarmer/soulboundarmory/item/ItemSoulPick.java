package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.CToolLevelupMessage;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolDatum.LEVEL;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolDatum.XP;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolType.PICK;

public class ItemSoulPick extends ItemPickaxe implements IItemSoulTool {
    private final float reachDistance;

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
    public boolean onBlockDestroyed(final ItemStack itemStack, final World world, final IBlockState blockState,
                                    final BlockPos blockPos, final EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && this.isEffectiveAgainst(blockState)
                && this.canHarvestBlock(blockState, (EntityPlayer) entity)) {
            final ISoulTool capability = SoulToolProvider.get(entity);
            final IType type = SoulToolType.getType(itemStack);
            final int xp = Math.min(Math.round(blockState.getBlockHardness(world, blockPos)), 5);

            if (capability.addDatum(xp, XP, type) && !world.isRemote && Configuration.levelupNotifications) {
                Main.CHANNEL.sendTo(new CToolLevelupMessage(itemStack, capability.getDatum(LEVEL, type)), (EntityPlayerMP) entity);
            }
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
        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(SoulToolHelper.REACH_DISTANCE_UUID, "generic.reachDistance", this.reachDistance, ADD), MAINHAND);

        return itemStack.getAttributeModifiers(MAINHAND);
    }

    @Override
    public boolean canHarvestBlock(final IBlockState blockState, final EntityPlayer player) {
        final Block block = blockState.getBlock();
        final float harvestLevel = SoulToolProvider.get(player).getAttribute(HARVEST_LEVEL, PICK);

        if (block == Blocks.OBSIDIAN) {
            return harvestLevel >= 3;
        } else if (block != Blocks.DIAMOND_BLOCK && block != Blocks.DIAMOND_ORE) {
            if (block != Blocks.EMERALD_ORE && block != Blocks.EMERALD_BLOCK) {
                if (block != Blocks.GOLD_BLOCK && block != Blocks.GOLD_ORE) {
                    if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE) {
                        if (block != Blocks.LAPIS_BLOCK && block != Blocks.LAPIS_ORE) {
                            if (block != Blocks.REDSTONE_ORE && block != Blocks.LIT_REDSTONE_ORE) {
                                final Material material = blockState.getMaterial();

                                if (material == Material.ROCK) {
                                    return true;
                                } else if (material == Material.IRON) {
                                    return true;
                                } else {
                                    return material == Material.ANVIL;
                                }
                            } else {
                                return harvestLevel >= 2;
                            }
                        } else {
                            return harvestLevel >= 1;
                        }
                    } else {
                        return harvestLevel >= 1;
                    }
                } else {
                    return harvestLevel >= 2;
                }
            } else {
                return harvestLevel >= 2;
            }
        } else {
            return harvestLevel >= 2;
        }
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
