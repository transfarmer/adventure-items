package transfarmer.soulboundarmory.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

public interface IItemSoulTool extends ISoulItem {
    boolean isEffectiveAgainst(IBlockState blockState);

    boolean canHarvestBlock(IBlockState blockState, EntityPlayer player);
}
