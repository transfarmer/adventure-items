package transfarmer.soulboundarmory.skill.pick;

import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillAmbidexterity extends Skill {
    public SkillAmbidexterity() {
        super("ambidexterity", null);
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 5;
    }
}
