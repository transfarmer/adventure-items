package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.i18n.Mappings;

import java.util.Set;

import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.data.tool.SoulToolAttribute.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.data.tool.SoulToolDatum.LEVEL;
import static transfarmer.soulboundarmory.data.tool.SoulToolDatum.XP;
import static transfarmer.soulboundarmory.data.tool.SoulToolType.PICK;

public class ItemSoulPick extends ItemPickaxe implements IItemSoulTool {
    private final float reachDistance;
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE);

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
            final SoulToolType type = SoulToolType.getType(itemStack);
            final int xp = Math.round(blockState.getBlockHardness(world, blockPos));

            if (capability.addDatum(xp, XP, type) && !world.isRemote && FMLCommonHandler.instance().getSide().isClient()) {
                entity.sendMessage(new TextComponentString(String.format(Mappings.MESSAGE_LEVEL_UP, itemStack.getDisplayName(), capability.getDatum(LEVEL, type))));
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
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        final Multimap<String, AttributeModifier> attributeModifiers = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            attributeModifiers.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(SoulToolHelper.REACH_DISTANCE_UUID, "generic.reachDistance", this.reachDistance, ADD));
        }

        return attributeModifiers;
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
    public float getAttackDamage() {
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
