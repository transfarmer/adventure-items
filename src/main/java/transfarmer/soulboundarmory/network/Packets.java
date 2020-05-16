package transfarmer.soulboundarmory.network;

import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.Main;

public final class Packets {
    public static final Identifier C2S_ATTRIBUTE = new Identifier(Main.MOD_ID, "server_attribute");
    public static final Identifier C2S_BIND_SLOT = new Identifier(Main.MOD_ID, "server_bind_slot");
    public static final Identifier C2S_CONFIG = new Identifier(Main.MOD_ID, "server_config");
    public static final Identifier C2S_ENCHANT = new Identifier(Main.MOD_ID, "server_enchant");
    public static final Identifier C2S_ITEM_TYPE = new Identifier(Main.MOD_ID, "server_item_type");
    public static final Identifier C2S_RESET = new Identifier(Main.MOD_ID, "server_reset");
    public static final Identifier C2S_SKILL = new Identifier(Main.MOD_ID, "server_skill");
    public static final Identifier C2S_SYNC = new Identifier(Main.MOD_ID, "server_sync");

    public static final Identifier S2C_CONFIG = new Identifier(Main.MOD_ID, "client_config");
    public static final Identifier S2C_REFRESH = new Identifier(Main.MOD_ID, "client_refresh");
    public static final Identifier S2C_ENCHANT = new Identifier(Main.MOD_ID, "client_enchant");
    public static final Identifier S2C_ITEM_TYPE = new Identifier(Main.MOD_ID, "client_item_type");
    public static final Identifier S2C_OPEN_GUI = new Identifier(Main.MOD_ID, "client_open_gui");
    public static final Identifier S2C_SYNC = new Identifier(Main.MOD_ID, "client_sync");
}
