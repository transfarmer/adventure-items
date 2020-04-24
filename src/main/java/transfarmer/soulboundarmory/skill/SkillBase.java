package transfarmer.soulboundarmory.skill;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.StringUtil;

import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class SkillBase implements ISkill {
    protected final String name;
    @SideOnly(CLIENT)
    protected List<String> tooltip;

    protected SkillBase(final String name, final String... tooltip) {
        this.name = name;

        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.tooltip = CollectionUtil.arrayList(tooltip);
        }
    }

    @Override
    @SideOnly(CLIENT)
    public String getName() {
        return I18n.format(String.format("skill.soulboundarmory.%s", StringUtil.macroCaseToCamelCase(this.name)));
    }

    @Override
    public List<String> getTooltip() {
        return this.tooltip;
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
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
    }
}
