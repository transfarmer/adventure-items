package transfarmer.adventureitems.capability;

import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public interface ISoulWeapon {
    void setAttributes(int[] bigsword, int[] sword, int[] dagger);
    void addAttribute(int attributeNumber);

    String getName();

    Item getItem();

    int[] getBigswordAttributes();
    void setBigswordAttributes(int[] bigsword);
    int[] getSwordAttributes();
    void setSwordAttributes(int[] sword);
    int[] getDaggerAttributes();
    void setDaggerAttributes(int[] dagger);

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

    int getCurrentTypeIndex();
    void setCurrentTypeIndex(int index);

    static boolean hasSoulWeapon(PlayerEntity player) {
        return player.inventory.hasAny(SoulWeapon.WeaponType.getItems());
    }

    static boolean isSoulWeaponEquipped(PlayerEntity player) {
        for (final Item WEAPON : SoulWeapon.WeaponType.getItems()) {
            if (player.inventory.getCurrentItem().isItemEqual(new ItemStack(WEAPON))) return true;
        }

        return false;
    }
}
