package transfarmer.soulboundarmory.skill.greatsword;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import java.util.ArrayList;
import java.util.List;

public class SkillLeaping extends SkillBaseLevelable {
    public SkillLeaping() {
        this(0);
    }

    public SkillLeaping(final int level) {
        super("leaping", new ItemStack(Items.RABBIT_FOOT), level, -1);
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 1;
    }
}
