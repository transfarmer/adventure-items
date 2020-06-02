package user11681.soulboundarmory.component.statistics;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;

public class Category {
    public static final Map<Identifier, Category> CATEGORIES = new HashMap<>();

    public static final Category DATUM = new Category("datum");
    public static final Category ATTRIBUTE = new Category("attribute");
    public static final Category ENCHANTMENT = new Category("enchantment");
    public static final Category SKILL = new Category("skill");

    protected final Identifier identifier;

    public Category(final String path) {
        this(new Identifier(Main.MOD_ID, path));
    }

    public Category(final Identifier identifier) {
        this.identifier = identifier;

        CATEGORIES.put(identifier, this);
    }

    @Override
    public String toString() {
        return this.identifier.toString();
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public static Category valueOf(final String path) {
        for (final Identifier identifier : CATEGORIES.keySet()) {
            if (identifier.getNamespace().equals(Main.MOD_ID) && identifier.getPath().equals(path)) {
                return valueOf(identifier);
            }
        }

        return null;
    }

    public static Category valueOf(final Identifier identifier) {
        return CATEGORIES.get(identifier);
    }
}
