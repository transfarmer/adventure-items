package transfarmer.adventureitems.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.adventureitems.SoulWeapons;

public class SoulWeapon implements ISoulWeapon {
    private SoulWeapons.WeaponType current = null;

    @Override
    public SoulWeapons.WeaponType getCurrentType() {
        return current;
    }

    public void setCurrentType(SoulWeapons.WeaponType weaponType) {
        current = weaponType;
    }

    public boolean hasSoulWeapon(PlayerEntity player) {
        return player.inventory.hasAny(SoulWeapons.getSoulWeapons());
    }

    public boolean isSoulWeaponEquipped(PlayerEntity player) {
        for (final Item WEAPON : SoulWeapons.getSoulWeapons()) {
            if (player.inventory.getCurrentItem().isItemEqual(new ItemStack(WEAPON))) {
                return true;
            }
        }

        return false;
    }
}
