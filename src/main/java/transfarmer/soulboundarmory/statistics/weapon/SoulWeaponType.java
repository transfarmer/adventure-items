package transfarmer.soulboundarmory.statistics.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.init.ModItems.*;

public enum SoulWeaponType implements IType {
    GREATSWORD(SOULBOUND_GREATSWORD, 0, "charge"),
    SWORD(SOULBOUND_SWORD, 1, "lightning bolt"),
    DAGGER(SOULBOUND_DAGGER, 2, "throwing", "perforation", "return", "sneak return");

    private final ItemSoulWeapon item;
    private final int index;
    private final String[] SKILLS;
    private static final SoulWeaponType[] TYPES = {GREATSWORD, SWORD, DAGGER};
    private static final List<Item> SOUL_WEAPONS = new ArrayList<>(3);
    static {
        SOUL_WEAPONS.add(GREATSWORD.item);
        SOUL_WEAPONS.add(SWORD.item);
        SOUL_WEAPONS.add(DAGGER.item);
    }

    SoulWeaponType(final ItemSoulWeapon item, final int index, final String ... skills) {
        this.item = item;
        this.index = index;
        this.SKILLS = skills;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public Item getItem() {
        return this.item;
    }

    @Override
    public ISoulItem getSoulItem() {
        return this.item;
    }

    @Override
    public String[] getSkills() {
        return this.SKILLS;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static int getAmount() {
        return TYPES.length;
    }

    public static SoulWeaponType getType(int index) {
        return index == -1 ? null : TYPES[index];
    }

    public static SoulWeaponType getType(Item item) {
        return getType(SOUL_WEAPONS.indexOf(item));
    }

    public static SoulWeaponType getType(ItemStack itemStack) {
        return getType(itemStack.getItem());
    }

    public static SoulWeaponType[] getTypes() {
        return TYPES;
    }

    public static List<Item> getItems() {
        return SOUL_WEAPONS;
    }
}
