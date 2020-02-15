package transfarmer.soulweapons.capability;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponDatum;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;

import java.util.SortedMap;

public interface ISoulWeapon {
    void set(int[][] data, float[][] attributes, int[][] enchantments);
    void setData(int[][] data);
    void setAttributes(float[][] attributes);
    void setEnchantments(int[][] enchantments);
    int[][] getData();
    float[][] getAttributes();
    int[][] getEnchantments();

    void setAttributes(float[] attributes, SoulWeaponType type);
    void setEnchantments(int[] enchantments, SoulWeaponType type);

    float getAttribute(SoulWeaponAttribute attribute, SoulWeaponType type);
    void setAttribute(float value, SoulWeaponAttribute attribute, SoulWeaponType type);
    void addAttribute(int amount, SoulWeaponAttribute attribute, SoulWeaponType type);

    float getAttackSpeed(SoulWeaponType type);
    float getAttackDamage(SoulWeaponType type);

    ItemStack getItemStack(ItemStack itemStack);
    ItemStack getItemStack(SoulWeaponType weaponType);

    AttributeModifier[] getAttributeModifiers(SoulWeaponType weaponType);

    SortedMap<SoulWeaponEnchantment, Integer> getEnchantments(SoulWeaponType weaponType);

    String[] getTooltip(SoulWeaponType weaponType);

    int getNextLevelXP(SoulWeaponType weaponType);

    void setDatum(int value, SoulWeaponDatum datum, SoulWeaponType type);
    boolean addDatum(int amount, SoulWeaponDatum datum, SoulWeaponType type);
    int getDatum(SoulWeaponDatum datum, SoulWeaponType weaponType);

    void addEnchantment(int amount, SoulWeaponEnchantment enchantment, SoulWeaponType type);
    int getEnchantment(SoulWeaponEnchantment enchantment, SoulWeaponType weaponType);

    void setCurrentType(SoulWeaponType type);
    void setCurrentType(int index);
    SoulWeaponType getCurrentType();

    void setCurrentTab(int tab);
    int getCurrentTab();

    void setCooldown(int ticks);
    void resetCooldown(SoulWeaponType type);
    void addCooldown(int ticks);
    int getCooldown();
    int getCooldown(SoulWeaponType type);
    float getAttackRatio(SoulWeaponType type);

    int getBoundSlot();
    void setBoundSlot(int boundSlot);
    void unbindSlot();
}
