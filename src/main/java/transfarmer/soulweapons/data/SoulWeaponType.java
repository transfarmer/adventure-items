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

    private static final List<Item> SOUL_WEAPONS = new ArrayList<>(3);
    static {
        SOUL_WEAPONS.add(GREATSWORD.item);
        SOUL_WEAPONS.add(SWORD.item);
        SOUL_WEAPONS.add(DAGGER.item);
    }
    public ItemSoulWeapon item;
    public int index;

    SoulWeaponType(Item item, int index) {
        this.item = (ItemSoulWeapon) item;
        this.index = index;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStack getItemStack() {
        return new ItemStack(this.item);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static SoulWeaponType getType(int index) {
        switch (index) {
            case 0:
                return GREATSWORD;
            case 1:
                return SWORD;
            case 2:
                return DAGGER;
            default:
                return null;
        }
    }

    public static SoulWeaponType getType(Item item) {
        return getType(SOUL_WEAPONS.indexOf(item));
    }

    public static SoulWeaponType getType(ItemStack itemStack) {
        return getType(itemStack.getItem());
    }

    public static boolean isSoulWeapon(ItemStack itemStack) {
        return SOUL_WEAPONS.contains(itemStack.getItem());
    }

    public static List<Item> getItems() {
        return SOUL_WEAPONS;
    }
}
