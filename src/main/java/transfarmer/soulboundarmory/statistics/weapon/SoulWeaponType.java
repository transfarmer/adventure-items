package transfarmer.soulboundarmory.statistics.weapon;

import net.minecraft.item.Item;
import transfarmer.soulboundarmory.statistics.SoulType;

import java.util.Arrays;
import java.util.List;

import static transfarmer.soulboundarmory.init.ModItems.*;

public class SoulWeaponType extends SoulType {
    public static final SoulType GREATSWORD = new SoulWeaponType(0, SOULBOUND_GREATSWORD, "greatsword", "charge");
    public static final SoulType SWORD = new SoulWeaponType(1, SOULBOUND_SWORD, "sword", "summon lightning");
    public static final SoulType DAGGER = new SoulWeaponType(2, SOULBOUND_DAGGER, "dagger", "throwing", "perforation", "return", "sneak return");

    public static final SoulType[] TYPES = {
            GREATSWORD,
            SWORD,
            DAGGER
    };
    protected static final List<Item> ITEMS = Arrays.asList(
            SOULBOUND_GREATSWORD,
            SOULBOUND_SWORD,
            SOULBOUND_DAGGER
    );


    protected SoulWeaponType(final int index, final Item item, final String name, final String... skills) {
        super(index, item, name, skills);
    }

    public static int getAmount() {
        return TYPES.length;
    }

    public static SoulType get(final int index) {
        return index == -1 ? null : TYPES[index];
    }

    public static SoulType get(final Item item) {
        return TYPES[ITEMS.indexOf(item)];
    }
}
