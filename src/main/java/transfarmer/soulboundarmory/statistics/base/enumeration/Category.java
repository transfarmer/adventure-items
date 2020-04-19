package transfarmer.soulboundarmory.statistics.base.enumeration;

import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.StringUtil;

public enum Category implements ICategory {
    DATUM,
    ATTRIBUTE,
    ENCHANTMENT;

    static {
        CollectionUtil.addAll(CATEGORIES, DATUM, ATTRIBUTE, ENCHANTMENT);
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
