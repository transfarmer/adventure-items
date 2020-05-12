package transfarmer.soulboundarmory.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.client.gui.screen.common.skill.GuiSkill;
import transfarmer.soulboundarmory.statistics.Skills;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public interface Skill extends INBTSerializable<CompoundTag> {
    @Nonnull
    List<Skill> getDependencies();

    boolean hasDependencies();

    int getTier();

    int getCost();

    boolean isLearned();

    boolean canBeLearned();

    boolean canBeLearned(int points);

    void learn();

    Identifier getTexture();

    GuiSkill getGUI();

    @NotNull
    String getModID();

    String getRegistryName();

    void setStorage(Skills storage, IItem item);

    @Environment(CLIENT)
    String getName();

    @Environment(CLIENT)
    List<String> getTooltip();
}
