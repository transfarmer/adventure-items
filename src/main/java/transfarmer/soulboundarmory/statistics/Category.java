package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.util.StringUtil;

public enum Category implements ICategory {
    DATUM,
    ATTRIBUTE;

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
