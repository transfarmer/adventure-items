package transfarmer.soulboundarmory.skill;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public interface ISkill {
    @SideOnly(CLIENT)
    String getName();

    String getRegistryName();

    void apply(ExtendedPacketBuffer context);

    @NotNull
    String getModID();

    default ResourceLocation getTexture() {
        return new ResourceLocation(getModID(), String.format("textures/skill/%s.png", this.getRegistryName()));
    }
}
