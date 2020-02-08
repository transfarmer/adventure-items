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
    void setAttributes(float[] attributes, SoulWeaponType type);
    void setEnchantments(int[][] enchantments);
    void setEnchantments(int[] enchantments, SoulWeaponType type);
    int[][] getData();
    float[][] getAttributes();
    int[][] getEnchantments();

    float getAttribute(SoulWeaponAttribute attribute, SoulWeaponType weaponType);
    void setAttribute(float value, SoulWeaponAttribute attribute, SoulWeaponType type);
    void addAttribute(SoulWeaponAttribute attribute, SoulWeaponType weaponType);
    void addAttribute(float amount, SoulWeaponAttribute attribute, SoulWeaponType weaponType);
    void resetAttributes(SoulWeaponType type);

    ItemStack getItemStack(ItemStack itemStack);
    ItemStack getItemStack(SoulWeaponType weaponType);

    AttributeModifier[] getAttributeModifiers(SoulWeaponType weaponType);

    SortedMap<SoulWeaponEnchantment, Integer> getEnchantments(SoulWeaponType weaponType);

    String[] getTooltip(SoulWeaponType weaponType);

    int getNextLevelXP(SoulWeaponType weaponType);

    int getMaxSkills(SoulWeaponType weaponType);

    void setDatum(int value, SoulWeaponDatum datum, SoulWeaponType type);
    boolean addDatum(int amount, SoulWeaponDatum datum, SoulWeaponType type);
    int getDatum(SoulWeaponDatum datum, SoulWeaponType weaponType);

    void addEnchantment(SoulWeaponEnchantment enchantment, SoulWeaponType weaponType);
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
}
