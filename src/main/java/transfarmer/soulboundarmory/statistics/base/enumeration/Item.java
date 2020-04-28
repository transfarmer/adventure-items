package transfarmer.soulboundarmory.statistics.base.enumeration;

import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.StringUtil;

public enum Item implements IItem {
    DAGGER,
    GREATSWORD,
    SWORD,
    STAFF,

    PICK;

    static {
        CollectionUtil.addAll(ITEMS,
                DAGGER,
                GREATSWORD,
                SWORD,
                STAFF,

                PICK
        );
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
