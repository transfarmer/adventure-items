package transfarmer.soulboundarmory.skill.dagger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillThrowing extends Skill {
    public SkillThrowing() {
        super("throwing", new ItemStack(Items.ARROW));
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 2;
    }
}
