package transfarmer.adventureitems.capability;

public interface ISoulWeapon {
    void setCurrentType(SoulWeapon.WeaponType weaponType);
    SoulWeapon.WeaponType getCurrentType();
}
