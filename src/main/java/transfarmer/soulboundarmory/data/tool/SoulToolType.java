package transfarmer.soulboundarmory.data.tool;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.item.ItemSoulPick;
import transfarmer.soulboundarmory.item.IItemSoulTool;

import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_PICK;

public enum SoulToolType {
    PICK(SOULBOUND_PICK, 0);

    private final IItemSoulTool item;
    public final int index;

    private static final SoulToolType[] TYPES = {PICK};

    SoulToolType(final IItemSoulTool item, final int index) {
        this.item = item;
        this.index = index;
    }

    public static SoulToolType getType(final int index) {
        if (index != -1) return TYPES[index];
        return null;
    }

    public Item getItem() {
        return (Item) this.item;
    }

    public IItemSoulTool getSoulTool() {
        return this.item;
    }

    public static SoulToolType getType(final ItemStack itemStack) {
        return getType(itemStack.getItem());
    }

    public static SoulToolType getType(final Item item) {
        if (item instanceof ItemSoulPick) {
            return PICK;
        } else return null;
    }

    public static SoulToolType[] getTypes() {
        return TYPES;
    }
}
