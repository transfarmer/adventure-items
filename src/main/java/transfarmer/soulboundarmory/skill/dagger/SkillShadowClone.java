package transfarmer.soulboundarmory.skill.dagger;

import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.init.Skills.THROWING;

public class SkillShadowClone extends SkillBase {
    public SkillShadowClone() {
        super("shadow_clone", null);
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillThrowing())
                : CollectionUtil.arrayList(this.storage.get(this.item, THROWING));
    }

    @Override
    public int getCost() {
        return 2;
    }
}
