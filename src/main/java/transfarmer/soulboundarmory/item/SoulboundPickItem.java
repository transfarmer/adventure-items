package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
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
import transfarmer.farmerlib.util.MathUtil;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.component.soulbound.tool.IToolComponent;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.statistics.Item;
import transfarmer.soulboundarmory.statistics.StatisticType;
import transfarmer.soulboundarmory.statistics.IItem;

import javax.annotation.Nonnull;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static transfarmer.soulboundarmory.item.ModToolMaterials.SOULBOUND;
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
        return IToolComponent.get(miner).getAttribute(Item.PICK, StatisticType.HARVEST_LEVEL) >= blockState.getBlock().getHarvestLevel(blockState);
    }

    @Override
    public boolean postMine(final ItemStack stack, final World world, final BlockState state, final BlockPos pos,
                            final LivingEntity miner) {
        if (miner instanceof PlayerEntity && this.canMine(state, world, pos, (PlayerEntity) miner)) {
            final IToolComponent component = IToolComponent.get(miner);
            final IItem type = component.getItemType(stack);
            int xp = Math.min(state.getHardness(world, pos), 5) + state.getBlock().getHarvestLevel(state);

            if (miner.getRandom().nextDouble() >= 0.5) {
                xp = MathUtil.ceil(xp);
            }

            if (component.addDatum(type, XP, xp) && !world.isClient && MainConfig.instance().getLevelupNotifications()) {
                miner.sendMessage(new TranslatableText("message.soulboundarmory.levelup", stack.getName(), component.getDatum(type, LEVEL)));
            }

            component.sync();
        }

        return true;
    }

    @Override
    @Nonnull
    public Multimap<String, EntityAttributeModifier> getModifiers(@Nonnull final EquipmentSlot slot) {
        final Multimap<String, EntityAttributeModifier> modifiers = super.getModifiers(slot);

        if (slot == MAINHAND) {
            modifiers.put(PlayerEntity.REACH_DISTANCE.getName(), new EntityAttributeModifier(SoulboundItemUtil.REACH_DISTANCE_UUID, "generic.reachDistance", -1, ADDITION));
        }
        return modifiers;
    }
}
