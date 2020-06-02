package user11681.soulboundarmory.item;

import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;
import user11681.usersmanual.math.MathUtil;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.config.IConfigComponent;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static user11681.soulboundarmory.component.statistics.StatisticType.MINING_LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.XP;
import static user11681.soulboundarmory.item.ModToolMaterials.SOULBOUND;

public class SoulboundPickItem extends PickaxeItem implements SoulboundToolItem {
    public SoulboundPickItem() {
        super(SOULBOUND, 0, -2.4F, new Settings());
    }

    @Override
    public boolean isEffectiveOn(final BlockState blockState) {
        final Material material = blockState.getMaterial();

        return material == Material.METAL || material == Material.ANVIL || material == Material.STONE;
    }

    @Override
    public boolean canMine(final BlockState state, final World world, final BlockPos pos, final PlayerEntity miner) {
        return PickStorage.get(miner, this).getAttribute(MINING_LEVEL) >= state.getBlock().getBlastResistance();
    }

    @Override
    public boolean postMine(final ItemStack stack, final World world, final BlockState state, final BlockPos pos,
                            final LivingEntity miner) {
        if (miner instanceof PlayerEntity && this.canMine(state, world, pos, (PlayerEntity) miner)) {
            final PickStorage component = PickStorage.get(miner);
            final int xp = MathUtil.roundRandomly(Math.min(state.getHardness(world, pos), 5) + state.getBlock().getBlastResistance(), world.random);

            if (component.addDatum(XP, xp) && !world.isClient && IConfigComponent.get(miner).getLevelupNotifications()) {
                miner.sendMessage(new TranslatableText(Mappings.MESSAGE_LEVEL_UP.getKey(), stack.getName(), component.getDatum(LEVEL)));
            }

//            component.sync();
        }

        return true;
    }

    @Override
    @Nonnull
    public Multimap<String, EntityAttributeModifier> getModifiers(@Nonnull final EquipmentSlot slot) {
        final Multimap<String, EntityAttributeModifier> modifiers = super.getModifiers(slot);

        if (slot == MAINHAND) {
            modifiers.put(ReachEntityAttributes.REACH.getId(), new EntityAttributeModifier(SoulboundItemUtil.REACH_DISTANCE_UUID, "generic.reachDistance", -1, ADDITION));
        }
        return modifiers;
    }
}
