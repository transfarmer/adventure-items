package transfarmer.soulboundarmory.skill;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.util.StringUtil;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class SkillBase implements ISkill {
    protected final String name;

    protected SkillBase(final String name) {
        this.name = name;
    }

    @Override
    public String getRegistryName() {
        return this.name;
    }

    @Override
    @SideOnly(CLIENT)
    public String getName() {
        return I18n.format("skill.soulboundarmory.%s", StringUtil.macroCaseToCamelCase(this.getRegistryName()));
    }

    @Override
    @NotNull
    public String getModID() {
        return Main.MOD_ID;
    }
}
