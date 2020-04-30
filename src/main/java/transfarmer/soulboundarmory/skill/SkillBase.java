package transfarmer.soulboundarmory.skill;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.gui.screen.common.skill.GuiSkill;
import transfarmer.soulboundarmory.statistics.Skills;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.StringUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class SkillBase implements Skill, Cloneable {
    protected final String name;
    protected final GuiSkill gui;
    protected Skills storage;
    protected IItem item;
    protected boolean learned;

    public SkillBase(final String name, @Nullable final ItemStack icon) {
        this.name = name;

        this.gui = FMLCommonHandler.instance().getSide() == CLIENT ? new GuiSkill(this, icon) : null;
    }

    @Override
    public void setStorage(final Skills storage, final IItem item) {
        this.storage = storage;
        this.item = item;
    }

    @Override
    @SideOnly(CLIENT)
    public String getName() {
        final String name = I18n.format(String.format("skill.%s.%s", this.getModID(), StringUtil.macroCaseToCamelCase(this.name)));

        return name.replaceFirst("[A-Za-z]", String.valueOf(name.charAt(0)).toUpperCase());
    }

    @Override
    @SideOnly(CLIENT)
    public List<String> getTooltip() {
        return Arrays.asList(I18n.format(String.format("skill.%s.%s.desc", this.getModID(), StringUtil.macroCaseToCamelCase(this.name))).split("\\\\n"));
    }

    @Override
    public boolean hasDependencies() {
        return !this.getDependencies().isEmpty();
    }

    @Override
    public final int getTier() {
        int tier = this.hasDependencies() ? 1 : 0;

        for (final Skill dependency : this.getDependencies()) {
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
        for (final Skill dependency : this.getDependencies()) {
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
    public GuiSkill getGUI() {
        return this.gui;
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

    @Override
    public Skill clone() {
        try {
            return (Skill) super.clone();
        } catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
