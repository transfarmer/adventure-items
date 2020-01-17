package transfarmer.adventureitems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.util.HashSet;

import static transfarmer.adventureitems.init.ModItems.SOUL_BIGSWORD;
import static transfarmer.adventureitems.init.ModItems.SOUL_DAGGER;
import static transfarmer.adventureitems.init.ModItems.SOUL_SWORD;

public class SoulWeapons {
    public static final HashSet<Item> items = new HashSet(3, 1);
    static {
        items.add(SOUL_BIGSWORD);
        items.add(SOUL_SWORD);
        items.add(SOUL_DAGGER);
    }

    public enum WeaponType {
        BIGSWORD("BIGSWORD"),
        SWORD("SWORD"),
        DAGGER("DAGGER");

        private String name;

        WeaponType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static WeaponType get(String name) {
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

    public static boolean isSoulWeaponPresent(PlayerEntity player) {
        return !player.inventory.hasAny(items);
    }
}
