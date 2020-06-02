package user11681.soulboundarmory.component.soulbound.item;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import user11681.usersmanual.registry.RegistryEntry;

public class StorageType<T> implements RegistryEntry {
    public static final StorageType<DaggerStorage> DAGGER_STORAGE = new StorageType<>("dagger");
    public static final StorageType<SwordStorage> SWORD_STORAGE = new StorageType<>("sword");
    public static final StorageType<GreatswordStorage> GREATSWORD_STORAGE = new StorageType<>("greatsword");
    public static final StorageType<StaffStorage> STAFF_STORAGE = new StorageType<>("staff");
    public static final StorageType<PickStorage> PICK_STORAGE = new StorageType<>("pick");

    protected final Identifier identifier;

    public StorageType(final String identifier) {
        this(new Identifier(Main.MOD_ID, identifier));
    }

    public StorageType(final Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Identifier getIdentifier() {
        return this.identifier;
    }
}
