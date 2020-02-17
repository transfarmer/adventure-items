package transfarmer.soularsenal.data.tool;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soularsenal.item.ItemSoulPick;
import transfarmer.soularsenal.item.ItemSoulTool;

import static transfarmer.soularsenal.init.ModItems.SOUL_PICK;

public enum SoulToolType {
    PICK(SOUL_PICK, 0);

    private final ItemSoulTool item;
    public final int index;

    private static final SoulToolType[] TYPES = {PICK};

    SoulToolType(final ItemSoulTool item, final int index) {
        this.item = item;
        this.index = index;
    }

    public static SoulToolType getType(final int index) {
        if (index != -1) return TYPES[index];
        return null;
    }

    public ItemSoulTool getItem() {
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
