package transfarmer.soulboundarmory.statistics.tool;

import net.minecraft.item.Item;
import transfarmer.soulboundarmory.statistics.SoulType;

import java.util.Arrays;
import java.util.List;

import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_PICK;

public class SoulToolType extends SoulType {
    public static final SoulType PICK = new SoulToolType(0, SOULBOUND_PICK, "pick", "ambidexterity");

    public static final SoulType[] TYPES = {
            PICK
    };
    public static final List<Item> ITEMS = Arrays.asList(
            SOULBOUND_PICK
    );

    protected SoulToolType(final int index, final Item item, final String name, final String... skills) {
        super(index, item, name, skills);
    }

    public static int getAmount() {
        return TYPES.length;
    }

    public static SoulType get(final int index) {
        return index == -1 ? null : TYPES[index];
    }

    public static SoulType get(final Item item) {
        return get(ITEMS.indexOf(item));
    }
}
