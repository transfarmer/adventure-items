package user11681.soulboundarmory.registry;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.network.C2S.C2SAttribute;
import user11681.soulboundarmory.network.C2S.C2SBindSlot;
import user11681.soulboundarmory.network.C2S.C2SConfig;
import user11681.soulboundarmory.network.C2S.C2SEnchant;
import user11681.soulboundarmory.network.C2S.C2SItemType;
import user11681.soulboundarmory.network.C2S.C2SReset;
import user11681.soulboundarmory.network.C2S.C2SSkill;
import user11681.soulboundarmory.network.C2S.C2SSync;
import user11681.soulboundarmory.network.S2C.S2CConfig;
import user11681.soulboundarmory.network.S2C.S2CEnchant;
import user11681.soulboundarmory.network.S2C.S2CItemType;
import user11681.soulboundarmory.network.S2C.S2COpenGUI;
import user11681.soulboundarmory.network.S2C.S2CRefresh;
import user11681.soulboundarmory.network.S2C.S2CSync;
import user11681.soulboundarmory.network.common.Packet;

public class Packets {
    public static final Identifier C2S_ATTRIBUTE = Registries.PACKET.register(new C2SAttribute()).getIdentifier();
    public static final Identifier C2S_BIND_SLOT = Registries.PACKET.register(new C2SBindSlot()).getIdentifier();
    public static final Identifier C2S_CONFIG = Registries.PACKET.register(new C2SConfig()).getIdentifier();
    public static final Identifier C2S_ENCHANT = Registries.PACKET.register(new C2SEnchant()).getIdentifier();
    public static final Identifier C2S_ITEM_TYPE = Registries.PACKET.register(new C2SItemType()).getIdentifier();
    public static final Identifier C2S_RESET = Registries.PACKET.register(new C2SReset()).getIdentifier();
    public static final Identifier C2S_SKILL = Registries.PACKET.register(new C2SSkill()).getIdentifier();
    public static final Identifier C2S_SYNC = Registries.PACKET.register(new C2SSync()).getIdentifier();

    public static final Identifier S2C_CONFIG = Registries.PACKET.register(new S2CConfig()).getIdentifier();
    public static final Identifier S2C_ENCHANT = Registries.PACKET.register(new S2CEnchant()).getIdentifier();
    public static final Identifier S2C_ITEM_TYPE = Registries.PACKET.register(new S2CItemType()).getIdentifier();
    public static final Identifier S2C_OPEN_GUI = Registries.PACKET.register(new S2COpenGUI()).getIdentifier();
    public static final Identifier S2C_REFRESH = Registries.PACKET.register(new S2CRefresh()).getIdentifier();
    public static final Identifier S2C_SYNC = Registries.PACKET.register(new S2CSync()).getIdentifier();

    @SuppressWarnings("VariableUseSideOnly")
    public static void register() {
        if (Main.ENVIRONMENT == EnvType.CLIENT) {
            for (final Map.Entry<Identifier, Packet> entry : Registries.PACKET.entries()) {
                MainClient.PACKET_REGISTRY.register(entry.getKey(), entry.getValue());
            }
        }

        for (final Map.Entry<Identifier, Packet> entry : Registries.PACKET.entries()) {
            Main.PACKET_REGISTRY.register(entry.getKey(), entry.getValue());
        }
    }
}
