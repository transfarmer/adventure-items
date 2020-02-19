package transfarmer.soulboundarmory.data;

import net.minecraft.item.Item;
import transfarmer.soulboundarmory.item.ISoulItem;

public interface IType {
    int getIndex();

    Item getItem();

    ISoulItem getSoulItem();

    String[] getSkills();
}
