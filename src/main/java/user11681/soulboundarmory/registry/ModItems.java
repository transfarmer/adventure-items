package user11681.soulboundarmory.registry;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.item.SoulboundDaggerItem;
import user11681.soulboundarmory.item.SoulboundGreatswordItem;
import user11681.soulboundarmory.item.SoulboundPickItem;
import user11681.soulboundarmory.item.SoulboundStaffItem;
import user11681.soulboundarmory.item.SoulboundSwordItem;

public interface ModItems {
    SoulboundDaggerItem SOULBOUND_DAGGER = new SoulboundDaggerItem();
    SoulboundSwordItem SOULBOUND_SWORD = new SoulboundSwordItem();
    SoulboundGreatswordItem SOULBOUND_GREATSWORD = new SoulboundGreatswordItem();
    SoulboundStaffItem SOULBOUND_STAFF = new SoulboundStaffItem();
    SoulboundPickItem SOULBOUND_PICK = new SoulboundPickItem();

    static void register(final Identifier identifier, final Item item) {
        Registry.register(Registry.ITEM, identifier, item);
    }

    static void register() {
        register(new Identifier(Main.MOD_ID, "soulbound_dagger"), SOULBOUND_DAGGER);
        register(new Identifier(Main.MOD_ID, "soulbound_sword"), SOULBOUND_SWORD);
        register(new Identifier(Main.MOD_ID, "soulbound_greatsword"), SOULBOUND_GREATSWORD);
        register(new Identifier(Main.MOD_ID, "soulbound_staff"), SOULBOUND_STAFF);
        register(new Identifier(Main.MOD_ID, "soulbound_pick"), SOULBOUND_PICK);
    }
}
