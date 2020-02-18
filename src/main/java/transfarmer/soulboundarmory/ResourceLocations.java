package transfarmer.soulboundarmory;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ResourceLocations {
    public static final ResourceLocation SOULBOUND_WEAPON = new ResourceLocation(Main.MOD_ID, "soulboundweapon");
    public static final ResourceLocation SOULBOUND_TOOL = new ResourceLocation(Main.MOD_ID, "soulboundtool");

    @SideOnly(CLIENT)
    public static final class Client {
        public static final ResourceLocation REACH_MODIFIER = new ResourceLocation(Main.MOD_ID, "textures/entity/reach_modifier.png");
        public static final ResourceLocation THROWN_SOULBOUND_DAGGER = new ResourceLocation(Main.MOD_ID, "textures/item/soulbound_dagger.png");
        public static final ResourceLocation XP_BAR = new ResourceLocation(Main.MOD_ID, "textures/gui/xp_bar.png");
    }
}
