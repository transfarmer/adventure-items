package transfarmer.soulweapons.capability;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.weapon.SoulWeaponAttribute;
import transfarmer.soulweapons.weapon.SoulWeaponType;

public interface ISoulWeapon {
    float[][] getAttributes();
    void setAttributes(float[][] attributes);

    int[][] getData();
    void setData(int[][] data);

    void addAttribute(int attributeNumber);
    void addAttribute(SoulWeaponAttribute attribute);

    String getWeaponName();
    String getWeaponName(int index);

    Item getItem();
    ItemStack getItemStack();
    ItemStack getItemStack(ItemStack itemStack);
    ItemStack getItemStack(SoulWeaponType weaponType);

    AttributeModifier[] getAttributeModifiers(SoulWeaponType weaponType);

    String[] getTooltip(ItemStack itemStack);

    int getNextLevelXP();
    int getNextLevelXP(SoulWeaponType weaponType);
    int getXP();
    int getXP(SoulWeaponType weaponType);
    int getXP(int index);
    boolean addXP(int xp);

    int getLevel();
    int getLevel(int index);
    int getLevel(SoulWeaponType weaponType);
    void addLevel();
    void addLevel(int index);

    int getPoints();
    void addPoint();

    int getMaxSkills();
    int getSkills();
    void addSpecial();

    float getEfficiency();
    void addEfficiency(float amount);

    float getKnockback();
    void addKnockback(float amount);

    float getAttackDamage();
    float getAttackDamage(SoulWeaponType type);
    void addAttackDamage(float amount);

    float getAttackSpeed();
    float getAttackSpeed(SoulWeaponType type);
    void addAttackSpeed(float amount);

    float getCritical();
    void addCritical(float amount);

    SoulWeaponType getCurrentType();
    void setCurrentType(SoulWeaponType type);
    void setCurrentType(int index);

    int getIndex();

    boolean hasAttributesAndData();

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
