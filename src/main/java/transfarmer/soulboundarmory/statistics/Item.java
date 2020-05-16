package transfarmer.soulboundarmory.statistics;

import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.farmerlib.string.StringUtil;

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
