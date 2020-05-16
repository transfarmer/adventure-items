package transfarmer.soulboundarmory.skill.dagger;

import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.farmerlib.util.CollectionUtil;

import javax.annotation.Nonnull;
import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.THROWING;

public class SkillShadowClone extends Skill {
    public SkillShadowClone() {
        super("shadow_clone", null);
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillThrowing())
                : CollectionUtil.arrayList(this.storage.get(this.item, THROWING));
    }

    @Override
    public int getCost() {
        return 2;
    }
}
