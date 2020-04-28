package transfarmer.soulboundarmory.item;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
public class ToolMaterials {
    @Nonnull
    public static final ToolMaterial SOULBOUND = EnumHelper.addToolMaterial("soulbound", 0, 0, 0.5F, 0, 0).setRepairItem(null);
}
