package transfarmer.adventureitems.capability;

import static transfarmer.adventureitems.capability.SoulWeapon.WeaponType;

public interface ISoulWeapon {
    void setCurrentType(WeaponType weaponType);
    WeaponType getCurrentType();
}
