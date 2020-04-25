package transfarmer.soulboundarmory.skill;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.util.StringUtil;

import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class SkillBase implements ISkill {
    protected final String name;
    protected boolean learned;

    protected SkillBase(final String name) {
        this.name = name;
    }

    @Override
    @SideOnly(CLIENT)
    public String getName() {
        final String name = I18n.format(String.format("skill.soulboundarmory.%s", StringUtil.macroCaseToCamelCase(this.name)));

        return name.replaceFirst("[A-Za-z]", String.valueOf(name.charAt(0)).toUpperCase());
    }

    @Override
    @SideOnly(CLIENT)
    public List<String> getTooltip() {
        return Arrays.asList(I18n.format(String.format("skill.soulboundarmory.%s.desc", StringUtil.macroCaseToCamelCase(this.name))).split("\\\\n"));
    }

    @Override
    public boolean hasDependencies() {
        return !this.getDependencies().isEmpty();
    }

    @Override
    public int getTier() {
        int tier = this.hasDependencies() ? 1 : 0;

        for (final ISkill dependency : this.getDependencies()) {
            final int previous = dependency.getTier();

            if (previous > 0) {
                tier++;
            }
        }

        return tier;
    }

    @Override
    public boolean isLearned() {
        return this.learned;
    }

    @Override
    public void learn() {
        this.learned = true;
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(this.getModID(), String.format("textures/skill/%s.png", this.getRegistryName()));
    }

    @Override
    @NotNull
    public String getModID() {
        return Main.MOD_ID;
    }

    @Override
    public String getRegistryName() {
        return this.name;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
    }
}
