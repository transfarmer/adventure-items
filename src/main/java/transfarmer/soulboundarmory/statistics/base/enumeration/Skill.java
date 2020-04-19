package transfarmer.soulboundarmory.statistics.base.enumeration;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.statistics.base.iface.ISkill;
import transfarmer.soulboundarmory.util.StringUtil;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public enum Skill implements ISkill {
    LEAPING,

    SUMMON_LIGHTNING,

    THROWING,
    SHADOW_CLONE,
    RETURN,
    SNEAK_RETURN,

    TELEPORTATION,
    AMBIDEXTERITY;

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }

    @Override
    @SideOnly(CLIENT)
    public
    String getName() {
        return I18n.format(String.format("skill.soulboundarmory.%s", this.toString()));
    }
}
