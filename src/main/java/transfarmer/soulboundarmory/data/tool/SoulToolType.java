package transfarmer.soulboundarmory.data.tool;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ItemSoulPick;

import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_PICK;

public enum SoulToolType implements IType {
    PICK(SOULBOUND_PICK, 0);

    private final IItemSoulTool item;
    private final int index;
    private final String[] SKILLS;

    private static final SoulToolType[] TYPES = {PICK};

    SoulToolType(final IItemSoulTool item, final int index, final String ... skills) {
        this.item = item;
        this.index = index;
        this.SKILLS = skills;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public Item getItem() {
        return (Item) this.item;
    }

    @Override
    public IItemSoulTool getSoulItem() {
        return this.item;
    }

    @Override
    public String[] getSkills() {
        return this.SKILLS;
    }

    public static SoulToolType getType(final int index) {
        if (index != -1) return TYPES[index];
        return null;
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

    public static int getAmount() {
        return TYPES.length;
    }
}
