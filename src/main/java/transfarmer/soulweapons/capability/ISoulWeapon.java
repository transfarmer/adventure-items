package transfarmer.soulweapons.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.WeaponType;

public interface ISoulWeapon {
    void setAttributes(int[][] attributes);
    int[][] getAttributes();
    void addAttribute(int attributeNumber);

    String getWeaponName();
    String getWeaponName(int index);
    String getAttributeName(int index);

    Item getItem();

    int getLevel();
    int getLevel(int index);
    void setLevel(int level);
    void addLevel();
    void addLevel(int index);

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
    void setCurrentType(int index);

    WeaponType getCurrentType();
    void setCurrentType(WeaponType type);

    boolean hasAttributes();

    static boolean hasSoulWeapon(EntityPlayer player) {
        for (final Item WEAPON : WeaponType.getItems()) {
            if (player.inventory.hasItemStack(new ItemStack(WEAPON))) {
                return true;
            }
        }

        return false;
    }

    static boolean isSoulWeaponEquipped(EntityPlayer player) {
        for (final Item WEAPON : WeaponType.getItems()) {
            if (player.inventory.getCurrentItem().isItemEqual(new ItemStack(WEAPON))) return true;
        }

        return false;
    }
}
