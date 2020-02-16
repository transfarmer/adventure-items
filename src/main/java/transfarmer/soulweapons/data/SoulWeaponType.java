package transfarmer.soulweapons.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.item.ItemSoulWeapon;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulweapons.init.ModItems.SOUL_GREATSWORD;
import static transfarmer.soulweapons.init.ModItems.SOUL_DAGGER;
import static transfarmer.soulweapons.init.ModItems.SOUL_SWORD;

public enum SoulWeaponType {
    GREATSWORD(SOUL_GREATSWORD, 0),
    SWORD(SOUL_SWORD, 1),
    DAGGER(SOUL_DAGGER, 2);

    private static final SoulWeaponType[] types = {GREATSWORD, SWORD, DAGGER};
    private static final List<Item> SOUL_WEAPONS = new ArrayList<>(3);
    static {
        SOUL_WEAPONS.add(GREATSWORD.item);
        SOUL_WEAPONS.add(SWORD.item);
        SOUL_WEAPONS.add(DAGGER.item);
    }
    public final ItemSoulWeapon item;
    public final int index;

    SoulWeaponType(Item item, int index) {
        this.item = (ItemSoulWeapon) item;
        this.index = index;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static SoulWeaponType getType(int index) {
        return index == -1 ? null : types[index];
    }

    public static SoulWeaponType getType(Item item) {
        return getType(SOUL_WEAPONS.indexOf(item));
    }

    public static SoulWeaponType getType(ItemStack itemStack) {
        return getType(itemStack.getItem());
    }

    public static SoulWeaponType[] getTypes() {
        return types;
    }

    public static List<Item> getItems() {
        return SOUL_WEAPONS;
    }
}
