package transfarmer.soulweapons;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulweapons.init.ModItems.SOUL_BIGSWORD;
import static transfarmer.soulweapons.init.ModItems.SOUL_DAGGER;
import static transfarmer.soulweapons.init.ModItems.SOUL_SWORD;

public enum WeaponType {
    BIGSWORD(SOUL_BIGSWORD, 0),
    SWORD(SOUL_SWORD, 1),
    DAGGER(SOUL_DAGGER, 2),
    NONE(null, 3);

    private static final List<Item> SOUL_WEAPONS = new ArrayList<>(3);
    static {
        SOUL_WEAPONS.add(SOUL_BIGSWORD);
        SOUL_WEAPONS.add(SOUL_SWORD);
        SOUL_WEAPONS.add(SOUL_DAGGER);
    }
    private Item item;
    private int index;

    WeaponType(Item item, int index) {
        this.item = item;
        this.index = index;
    }

    public Item getItem() {
        return this.item;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.toString().toLowerCase();
    }

    public static WeaponType getType(int index) {
        switch (index) {
            case 0:
                return BIGSWORD;
            case 1:
                return SWORD;
            case 2:
                return DAGGER;
            default:
                return NONE;
        }
    }

    public static WeaponType getType(Item item) {
        return getType(SOUL_WEAPONS.indexOf(item));
    }

    public static List<Item> getItems() {
        return SOUL_WEAPONS;
    }
}
