package transfarmer.soulboundarmory.skill;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.statistics.Skills;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.StringUtil;

import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class SkillBase implements ISkill {
    protected final String name;
    protected Skills storage;
    protected IItem item;
    protected boolean learned;

    public SkillBase(final String name) {
        this.name = name;

        boolean registered = false;

        for (final ISkill skill : SKILLS) {
            if (skill.getRegistryName().equals(this.getRegistryName())) {
                registered = true;

                break;
            }
        }

        if (!registered) {
            SKILLS.add(this);
        }
    }

    @Override
    public void setStorage(final Skills storage, final IItem item) {
        this.storage = storage;
        this.item = item;
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
    public boolean canBeLearned() {
        for (final ISkill dependency : this.getDependencies()) {
            if (!dependency.isLearned()) {
                return false;
            }
        }

        return !this.learned;
    }

    @Override
    public boolean canBeLearned(final int points) {
        return this.canBeLearned() && points >= this.getCost();
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
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setBoolean("learned", this.learned);

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        this.learned = tag.getBoolean("learned");
    }
}
