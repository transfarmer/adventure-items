package transfarmer.soulboundarmory.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

public interface IItemSoulboundTool extends ISoulboundItem {
    boolean isEffectiveAgainst(IBlockState blockState);

    boolean canHarvestBlock(IBlockState blockState, EntityPlayer player);
}
