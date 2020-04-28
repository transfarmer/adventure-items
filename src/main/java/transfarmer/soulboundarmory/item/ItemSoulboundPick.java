package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.tool.ITool;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.statistics.base.enumeration.Item;
import transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public class ItemSoulboundPick extends ItemPickaxe implements SoulboundTool {
    private static final String toolClass = "pickaxe";

    public ItemSoulboundPick(final String name) {
        super(ToolMaterial.DIAMOND);

        this.setRegistryName(Main.MOD_ID, name);
        this.setTranslationKey(String.format("%s.%s", Main.MOD_ID, name));

        this.attackDamage = 0;
        this.attackSpeed = -2.4F;
        this.efficiency = 0.5F;
    }

    @Override
    public boolean hitEntity(@Nonnull final ItemStack itemStack, @Nonnull final EntityLivingBase target, @Nonnull final EntityLivingBase attacker) {
        return false;
    }

    @Override
    public boolean isEffectiveAgainst(final IBlockState blockState) {
        final Material material = blockState.getMaterial();

        return material == Material.IRON || material == Material.ANVIL || material == Material.ROCK;
    }

    @Override
    public int getHarvestLevel(@Nullable final ItemStack stack, @Nonnull final String toolClass, @Nullable final EntityPlayer player, @Nullable final IBlockState blockState) {
        if (player != null) {
            return (int) ToolProvider.get(player).getAttribute(Item.PICK, StatisticType.HARVEST_LEVEL);
        }

        return 0;
    }

    @Override
    public boolean canHarvestBlock(final IBlockState blockState, final EntityPlayer player) {
        return this.getHarvestLevel(null, toolClass, player, blockState) >= blockState.getBlock().getHarvestLevel(blockState);
    }

    @Override
    public boolean onBlockDestroyed(@Nonnull final ItemStack itemStack, @Nonnull final World world, @Nonnull final IBlockState blockState, @Nonnull final BlockPos blockPos, @Nonnull final EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && this.canHarvestBlock(blockState, (EntityPlayer) entity)) {
            final ITool capability = ToolProvider.get(entity);
            final IItem type = capability.getItemType(itemStack);
            final int xp = Math.min(Math.round(blockState.getBlockHardness(world, blockPos)), 5) + blockState.getBlock().getHarvestLevel(blockState);

            if (capability.addDatum(type, XP, xp) && !world.isRemote && MainConfig.instance().getLevelupNotifications()) {
                entity.sendMessage(new TextComponentTranslation("message.soulboundarmory.levelup", itemStack.getDisplayName(), capability.getDatum(type, LEVEL)));
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
    public boolean getIsRepairable(@Nonnull final ItemStack itemStackToRepair, @Nonnull final ItemStack material) {
        return false;
    }

    @Override
    @Nonnull
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull final EntityEquipmentSlot equipmentSlot, final ItemStack itemStack) {
        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(SoulItemHelper.REACH_DISTANCE_UUID, "generic.reachDistance", -1, ADD), MAINHAND);

        return itemStack.getAttributeModifiers(MAINHAND);
    }
}
