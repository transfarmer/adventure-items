package transfarmer.soulboundarmory.skill.greatsword;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillLeaping extends Skill {
    public SkillLeaping() {
        this(0);
    }

    public SkillLeaping(final int level) {
        super("leaping", new ItemStack(Items.RABBIT_FOOT), level, -1);
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 1;
    }
}
