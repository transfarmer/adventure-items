package transfarmer.soulweapons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.item.ItemSoulWeapon;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulweapons.init.ModItems.SOUL_BIGSWORD;
import static transfarmer.soulweapons.init.ModItems.SOUL_DAGGER;
import static transfarmer.soulweapons.init.ModItems.SOUL_SWORD;

public enum SoulWeaponType {
    BIGSWORD(SOUL_BIGSWORD, 0),
    SWORD(SOUL_SWORD, 1),
    DAGGER(SOUL_DAGGER, 2),
    NONE(null, -1);

    private static final List<Item> SOUL_WEAPONS = new ArrayList<>(3);
    static {
        SOUL_WEAPONS.add(BIGSWORD.getItem());
        SOUL_WEAPONS.add(SWORD.getItem());
        SOUL_WEAPONS.add(DAGGER.getItem());
    }
    private Item item;
    private int index;

    SoulWeaponType(Item item, int index) {
        this.item = item;
        this.index = index;
    }

    public ItemSoulWeapon getItem() {
        return (ItemSoulWeapon) this.item;
    }

    public ItemStack getItemStack() {
        return new ItemStack(this.getItem());
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.toString().toLowerCase();
    }

    public static SoulWeaponType getType(int index) {
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
