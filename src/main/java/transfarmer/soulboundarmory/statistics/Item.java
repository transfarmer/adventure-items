package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.util.StringUtil;

public enum Item implements IItem {
    DAGGER,
    GREATSWORD,
    SWORD,

    PICK;

    private static final IItem[] ITEM_TYPES = {
            DAGGER,
            GREATSWORD,
            SWORD,

            PICK
    };

    public static IItem get(final String name) {
        for (final IItem type : ITEM_TYPES) {
            if (type.name().equals(name)) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
