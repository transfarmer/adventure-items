package transfarmer.adventureitems.items;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import transfarmer.adventureitems.ModItemTier;
import transfarmer.adventureitems.init.ModItemGroups;

public class SoulBigsword extends SwordItem {
    public SoulBigsword() {
        super(ModItemTier.SOUL, 3, -2.8F, new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP));
    }
}
