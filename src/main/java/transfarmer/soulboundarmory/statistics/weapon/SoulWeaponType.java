package transfarmer.soulboundarmory.statistics.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.statistics.IType;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.init.ModItems.*;

public enum SoulWeaponType implements IType {
    GREATSWORD(SOULBOUND_GREATSWORD, 0, "charge"),
    SWORD(SOULBOUND_SWORD, 1, "lightning bolt"),
    DAGGER(SOULBOUND_DAGGER, 2, "throwing", "perforation", "return", "sneak return");

    private final ItemSoulWeapon item;
    private final int index;
    private final String[] SKILLS;
    private static final IType[] TYPES = {GREATSWORD, SWORD, DAGGER};
    private static final List<Item> ITEMS = new ArrayList<>(3);
    static {
        ITEMS.add(GREATSWORD.item);
        ITEMS.add(SWORD.item);
        ITEMS.add(DAGGER.item);
    }

    SoulWeaponType(final ItemSoulWeapon item, final int index, final String ... skills) {
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
        return this.item;
    }

    @Override
    public ISoulItem getSoulItem() {
        return this.item;
    }

    @Override
    public String[] getSkills() {
        return this.SKILLS;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static int getAmount() {
        return TYPES.length;
    }

    public static IType getType(int index) {
        return index == -1 ? null : TYPES[index];
    }

    public static IType getType(Item item) {
        return getType(ITEMS.indexOf(item));
    }

    public static IType getType(ItemStack itemStack) {
        return getType(itemStack.getItem());
    }

    public static IType[] getTypes() {
        return TYPES;
    }

    public static List<Item> getItems() {
        return ITEMS;
    }
}
