package transfarmer.soularsenal;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ResourceLocations {
    public static final ResourceLocation SOUL_TOOL = new ResourceLocation(Main.MOD_ID, "soultool");

    @SideOnly(CLIENT)
    public static final class Client {
        public static final ResourceLocation REACH_MODIFIER = new ResourceLocation(Main.MOD_ID, "textures/entity/reach_modifier.png");
        public static final ResourceLocation THROWN_SOUL_DAGGER = new ResourceLocation(Main.MOD_ID, "textures/item/soul_dagger.png");
        public static final ResourceLocation XP_BAR = new ResourceLocation(Main.MOD_ID, "textures/gui/xp_bar.png");
    }
}
