package transfarmer.soulboundarmory.statistics;

import net.minecraft.item.Item;
import transfarmer.soulboundarmory.item.ISoulItem;

public interface IType {
    int getIndex();

    Item getItem();

    ISoulItem getSoulItem();

    String[] getSkills();
}
