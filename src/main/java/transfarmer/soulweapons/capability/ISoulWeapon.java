package transfarmer.soulweapons.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.SoulWeaponType;

import java.util.List;

public interface ISoulWeapon {
    void setAttributes(int[][] attributes);
    int[][] getAttributes();
    void addAttribute(int attributeNumber);

    String getWeaponName();
    String getWeaponName(int index);
    String getAttributeName(int index);

    Item getItem();
    ItemStack getItemStack();
    ItemStack getItemStack(ItemStack itemStack);
    ItemStack getItemStack(SoulWeaponType weaponType);

    List<String> getTooltip(ItemStack itemStack);

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

    int getEfficiency();
    void setEfficiency(int efficiency);
    void addEfficiency(int amount);

    int getKnockback();
    void setKnockback(int knockback);
    void addKnockback(int amount);

    int getAttackDamage();
    int getAttackDamage(SoulWeaponType type);
    void setAttackDamage(int attackDamage);
    void addAttackDamage(int amount);

    int getCritical();
    void setCritical(int critical);
    void addCritical(int amount);

    float getAttackSpeed();
    float getAttackSpeed(SoulWeaponType type);
    void setAttackSpeed(float attackSpeed);
    void addAttackSpeed(float amount);

    SoulWeaponType getCurrentType();
    void setCurrentType(SoulWeaponType type);
    void setCurrentType(int index);

    boolean hasAttributes();

    static boolean hasSoulWeapon(EntityPlayer player) {
        for (final Item WEAPON : SoulWeaponType.getItems()) {
            if (player.inventory.hasItemStack(new ItemStack(WEAPON))) {
                return true;
            }
        }

        return false;
    }

    static boolean isSoulWeaponEquipped(EntityPlayer player) {
        for (final Item WEAPON : SoulWeaponType.getItems()) {
            if (player.inventory.getCurrentItem().isItemEqual(new ItemStack(WEAPON))) return true;
        }

        return false;
    }
}
