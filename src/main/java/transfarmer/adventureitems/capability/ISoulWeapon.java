package transfarmer.adventureitems.capability;

import net.minecraft.entity.player.PlayerEntity;

import static transfarmer.adventureitems.SoulWeapons.WeaponType;


public interface ISoulWeapon {
    void setData(WeaponType weaponType, int level, int special, int points, int maxSpecial,
                 int hardness, int knockback, int attackDamage, int critical);

    WeaponType getWeaponType();
    void setWeaponType(WeaponType weaponType);

    int getLevel();
    void setLevel(int level);
    void addLevel();

    int getPoints();
    void setPoints(int points);
    void addPoint();

    int getMaxSpecial();
    int getSpecial();
    void setSpecial(int special);
    void addSpecial();


    int getHardness();
    void setHardness(int hardness);
    void addHardness(int amount);

    int getKnockback();
    void setKnockback(int knockback);
    void addKnockback(int amount);

    int getAttackDamage();
    void setAttackDamage(int attackDamage);
    void addAttackDamage(int amount);

    int getCritical();
    void setCritical(int critical);
    void addCritical(int amount);

    void increaseAttribute(int attributeNumber);
    boolean hasSoulWeapon(PlayerEntity player);
    boolean isSoulWeaponEquipped(PlayerEntity player);
}
