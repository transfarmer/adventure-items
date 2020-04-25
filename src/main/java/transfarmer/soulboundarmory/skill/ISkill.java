package transfarmer.soulboundarmory.skill;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public interface ISkill extends INBTSerializable<NBTTagCompound> {
    Set<ISkill> SKILLS = new HashSet<>();

    static ISkill get(final String name) {
        for (final ISkill skill : SKILLS) {
            if (skill.getRegistryName().equals(name)) {
                return skill;
            }
        }

        return null;
    }

    List<ISkill> getDependencies();

    boolean hasDependencies();

    int getTier();

    boolean isLearned();

    void learn();

    ResourceLocation getTexture();

    @NotNull
    String getModID();

    String getRegistryName();

    @SideOnly(CLIENT)
    String getName();

    @SideOnly(CLIENT)
    List<String> getTooltip();
}
