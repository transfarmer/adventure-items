package transfarmer.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.component.soulbound.tool.IToolComponent;
import transfarmer.soulboundarmory.component.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.statistics.base.enumeration.Item;
import transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.EntityAttributeModifierOperation.ADD;
import static transfarmer.soulboundarmory.item.ModToolMaterials.SOULBOUND;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public class SoulboundPickItem extends PickaxeItem implements SoulboundTool {
    private static final String toolClass = "pickaxe";

    public SoulboundPickItem(final String name) {
        super(SOULBOUND, 0, -2.4F, new Settings());

        this.setRegistryName(Main.MOD_ID, name);
    }

    @Override
    public boolean postHit(@Nonnull final ItemStack itemStack, @Nonnull final LivingEntity target,
                             @Nonnull final LivingEntity attacker) {
        return false;
    }

    @Override
    public boolean isEffectiveOn(final BlockState blockState) {
        final Material material = blockState.getMaterial();

        return material == Material.METAL || material == Material.ANVIL || material == Material.STONE;
    }

    @Override
    public int getHarvestLevel(@Nullable final ItemStack stack, @Nonnull final String toolClass,
                               @Nullable final PlayerEntity player, @Nullable final BlockState blockState) {
        if (player != null) {
            return (int) ToolProvider.get(player).getAttribute(Item.PICK, StatisticType.HARVEST_LEVEL);
        }

        return 0;
    }

    @Override
    public boolean canHarvestBlock(final IBlockState blockState, final PlayerEntity player) {
        return this.getHarvestLevel(null, toolClass, player, blockState) >= blockState.getBlock().getHarvestLevel(blockState);
    }

    @Override
    public boolean onBlockDestroyed(@Nonnull final ItemStack itemStack, @Nonnull final World world,
                                    @Nonnull final IBlockState blockState, @Nonnull final BlockPos blockPos,
                                    @Nonnull final EntityLivingBase entity) {
        if (entity instanceof PlayerEntity && this.canHarvestBlock(blockState, (PlayerEntity) entity)) {
            final IToolComponent capability = ToolProvider.get(entity);
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
    @Nonnull
    public Multimap<String, EntityAttributeModifier> getEntityAttributeModifiers(@Nonnull final EntityEquipmentSlot slot,
                                                                     @NotNull final ItemStack itemStack) {
        final Multimap<String, EntityAttributeModifier> modifiers = HashMultimap.create();

        if (slot == MAINHAND)
            modifiers.put(PlayerEntity.REACH_DISTANCE.getName(), new EntityAttributeModifier(SoulboundItemUtil.REACH_DISTANCE_UUID, "generic.reachDistance", -1, ADD));

        return modifiers;
    }
}
