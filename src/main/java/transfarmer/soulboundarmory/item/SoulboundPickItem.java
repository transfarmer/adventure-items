package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import nerdhub.cardinal.components.api.component.Component;
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
import transfarmer.farmerlib.math.MathUtil;
import transfarmer.reachentityattributes.ReachEntityAttributes;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.config.MainConfig;

import javax.annotation.Nonnull;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static transfarmer.soulboundarmory.item.ModToolMaterials.SOULBOUND;
import static transfarmer.soulboundarmory.statistics.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.XP;

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
        return ISoulboundItemComponent.get(miner, this).getAttribute(HARVEST_LEVEL) >= state.getBlock().getBlastResistance();
    }

    @Override
    public boolean postMine(final ItemStack stack, final World world, final BlockState state, final BlockPos pos,
                            final LivingEntity miner) {
        if (miner instanceof PlayerEntity && this.canMine(state, world, pos, (PlayerEntity) miner)) {
            final ISoulboundItemComponent<? extends Component> component = ISoulboundItemComponent.get(stack);
            final int xp = MathUtil.roundRandomly(Math.min(state.getHardness(world, pos), 5) + state.getBlock().getBlastResistance(), world.random);

            final MainConfig config = MainConfig.instance();

            if (component.addDatum(XP, xp) && !world.isClient && config.levelupNotifications) {
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
