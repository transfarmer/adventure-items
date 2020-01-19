package transfarmer.adventureitems.capability;

import static transfarmer.adventureitems.SoulWeapons.WeaponType;

public class SoulWeaponData {
    public WeaponType weaponType;
    public int level;
    public int points;
    public int special;
    public int maxSpecial;
    public int hardness;
    public int knockback;
    public int attackDamage;
    public int critical;

    public SoulWeaponData(WeaponType weaponType, int level, int points, int special, int maxSpecial,
                          int hardness, int knockback, int attackDamage, int critical) {
        this.weaponType = weaponType;
        this.level = level;
        this.points = points;
        this.special = special;
        this.maxSpecial = maxSpecial;
        this.hardness = hardness;
        this.knockback = knockback;
        this.attackDamage = attackDamage;
        this.critical = critical;
    }
}
