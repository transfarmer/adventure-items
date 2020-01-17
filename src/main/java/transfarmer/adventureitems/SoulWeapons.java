package transfarmer.adventureitems;

import net.minecraft.item.Item;

import java.util.HashSet;

import static transfarmer.adventureitems.init.ModItems.*;


public class SoulWeapons {
    private static final HashSet<Item> SOUL_WEAPONS = new HashSet<>(3, 1);

    static {
        SOUL_WEAPONS.add(SOUL_BIGSWORD);
        SOUL_WEAPONS.add(SOUL_SWORD);
        SOUL_WEAPONS.add(SOUL_DAGGER);
    }

    public enum WeaponType {
        BIGSWORD(SOUL_BIGSWORD),
        SWORD(SOUL_SWORD),
        DAGGER(SOUL_DAGGER);

        private Item item;

        WeaponType(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return this.item;
        }

        public static WeaponType getItem(String name) {
            switch (name) {
                case "BIGSWORD":
                    return BIGSWORD;
                case "SWORD":
                    return SWORD;
                case "DAGGER":
                    return DAGGER;
            }

            return null;
        }
    }

    public static HashSet<Item> getSoulWeapons() {
        return SOUL_WEAPONS;
    }
}
