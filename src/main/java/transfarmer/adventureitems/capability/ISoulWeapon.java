package transfarmer.adventureitems.capability;

import net.minecraft.entity.player.PlayerEntity;

import static transfarmer.adventureitems.SoulWeapons.WeaponType;


public interface ISoulWeapon {
    WeaponType getCurrentType();
    void setCurrentType(WeaponType weaponType);
    boolean hasSoulWeapon(PlayerEntity player);
}
