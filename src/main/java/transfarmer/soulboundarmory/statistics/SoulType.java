package transfarmer.soulboundarmory.statistics;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import java.util.Arrays;
import java.util.List;

import static transfarmer.soulboundarmory.init.ModItems.*;

public class SoulType extends Statistic {
    public static final SoulType GREATSWORD = new SoulType(0, SOULBOUND_GREATSWORD, "greatsword", "charge");
    public static final SoulType SWORD = new SoulType(1, SOULBOUND_SWORD, "sword", "summon lightning");
    public static final SoulType DAGGER = new SoulType(2, SOULBOUND_DAGGER, "dagger", "throwing", "perforation", "return", "sneak return");

    public static final SoulType PICK = new SoulType(0, SOULBOUND_PICK, "pick");

    protected static final SoulType[] TYPES = {
            GREATSWORD,
            SWORD,
            DAGGER,
            PICK
    };
    protected static final List<Item> ITEMS = Arrays.asList(
            SOULBOUND_GREATSWORD,
            SOULBOUND_SWORD,
            SOULBOUND_DAGGER,
            SOULBOUND_PICK
    );

    protected final Item item;
    protected final String[] skills;

    protected SoulType(final int index, final Item item, final String name, final String... skills) {
        super(index, name);
        this.item = item;
        this.skills = skills;
    }

    public static SoulType get(final int index) {
        return get(ITEMS.get(index));
    }

    public static SoulType get(final Item item) {
        return item instanceof ItemSoulWeapon
                ? SoulWeaponType.get(item)
                : item instanceof IItemSoulTool
                ? SoulToolType.get(item)
                : null;
    }

    public static SoulType get(final ItemStack itemStack) {
        return get(itemStack.getItem());
    }

    public static int getAmount() {
        return TYPES.length;
    }

    public static List<Item> getItems() {
        return ITEMS;
    }

    public Item getItem() {
        return this.item;
    }

    public ISoulItem getSoulItem() {
        return (ISoulItem) this.item;
    }

    public String[] getSkills() {
        return this.skills;
    }
}
