package transfarmer.soulweapons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class Client {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final ResourceLocation XP_BAR = new ResourceLocation(Main.MODID, "textures/gui/xp_bar.png");
}
