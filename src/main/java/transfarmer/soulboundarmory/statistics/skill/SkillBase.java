package transfarmer.soulboundarmory.statistics.skill;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class SkillBase implements ISkill {
    protected final String name;

    protected SkillBase(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    @SideOnly(CLIENT)
    public String getName() {
        return I18n.format("skill.soulboundarmory.%s", this);
    }
}
