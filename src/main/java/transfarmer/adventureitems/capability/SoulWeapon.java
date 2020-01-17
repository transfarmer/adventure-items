package transfarmer.adventureitems.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import transfarmer.adventureitems.SoulWeapons;

import java.util.HashSet;

import static transfarmer.adventureitems.init.ModItems.SOUL_BIGSWORD;
import static transfarmer.adventureitems.init.ModItems.SOUL_DAGGER;
import static transfarmer.adventureitems.init.ModItems.SOUL_SWORD;


public class SoulWeapon implements ISoulWeapon {
    private SoulWeapons.WeaponType current = null;
    private static final HashSet<Item> SOUL_WEAPONS = new HashSet(3, 1);
    static {
        SOUL_WEAPONS.add(SOUL_BIGSWORD);
        SOUL_WEAPONS.add(SOUL_SWORD);
        SOUL_WEAPONS.add(SOUL_DAGGER);
    }


    @Override
    public SoulWeapons.WeaponType getCurrentType() {
        return current;
    }

    public void setCurrentType(SoulWeapons.WeaponType weaponType) {
        current = weaponType;
    }

    public boolean hasSoulWeapon(PlayerEntity player) {
        return player.inventory.hasAny(SOUL_WEAPONS);
    }
}
