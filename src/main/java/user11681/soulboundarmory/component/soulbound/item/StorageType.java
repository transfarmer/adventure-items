package user11681.soulboundarmory.component.soulbound.item;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.registry.Registries;
import user11681.usersmanual.registry.AbstractRegistryEntry;

public class StorageType<T> extends AbstractRegistryEntry {
    public static final StorageType<DaggerStorage> DAGGER_STORAGE = Registries.STORAGE_TYPE.register(new StorageType<>(new Identifier(Main.MOD_ID, "dagger")));
    public static final StorageType<SwordStorage> SWORD_STORAGE = Registries.STORAGE_TYPE.register(new StorageType<>(new Identifier(Main.MOD_ID, "sword")));
    public static final StorageType<GreatswordStorage> GREATSWORD_STORAGE = Registries.STORAGE_TYPE.register(new StorageType<>(new Identifier(Main.MOD_ID, "greatsword")));
    public static final StorageType<StaffStorage> STAFF_STORAGE = Registries.STORAGE_TYPE.register(new StorageType<>(new Identifier(Main.MOD_ID, "staff")));
    public static final StorageType<PickStorage> PICK_STORAGE = Registries.STORAGE_TYPE.register(new StorageType<>(new Identifier(Main.MOD_ID, "pick")));

    public StorageType(final Identifier identifier) {
        super(identifier);
    }
}
