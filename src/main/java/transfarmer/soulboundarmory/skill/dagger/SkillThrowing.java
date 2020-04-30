package transfarmer.soulboundarmory.skill.dagger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillThrowing extends SkillBase {
    public SkillThrowing() {
        super("throwing", new ItemStack(Items.ARROW));
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 2;
    }
}
