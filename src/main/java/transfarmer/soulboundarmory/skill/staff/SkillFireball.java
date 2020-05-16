package transfarmer.soulboundarmory.skill.staff;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillFireball extends Skill {
    public SkillFireball() {
        super("fireball", new ItemStack(Items.FIRE_CHARGE));
    }

    @Nonnull
    @Override
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 1;
    }
}
