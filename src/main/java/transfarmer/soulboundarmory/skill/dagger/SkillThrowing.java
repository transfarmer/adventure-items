package transfarmer.soulboundarmory.skill.dagger;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillThrowing extends SkillBase {
    public SkillThrowing() {
        super("throwing");
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/arrow.png");
    }
}
