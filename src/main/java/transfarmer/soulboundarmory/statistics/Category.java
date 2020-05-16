package transfarmer.soulboundarmory.statistics;

import java.util.HashMap;
import java.util.Map;

public class Category {
    public static final Category DATUM = new Category("datum");
    public static final Category ATTRIBUTE = new Category("attribute");
    public static final Category ENCHANTMENT = new Category("enchantment");
    public static final Category SKILL = new Category("skill");

    public static final Map<String, Category> CATEGORIES = new HashMap<>();

    protected final String name;

    public Category(final String name) {
        this.name = name;

        CATEGORIES.put(this.name, this);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Category valueOf(final String name) {
        return CATEGORIES.get(name);
    }
}
