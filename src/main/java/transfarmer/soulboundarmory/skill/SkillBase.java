package transfarmer.soulboundarmory.skill;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.util.StringUtil;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class SkillBase implements ISkill {
    protected String name;

    protected SkillBase(final String name) {
        this.name = name;
    }

    @Override
    @SideOnly(CLIENT)
    public String getName() {
        return I18n.format("skill.soulboundarmory.%s", StringUtil.macroCaseToCamelCase(this.getRegistryName()));
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

        tag.setString("name", this.name);

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        this.name = tag.getString("name");
    }
}
