package transfarmer.soulboundarmory.skill.pick;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillTeleportation extends Skill {
    public SkillTeleportation() {
        super("teleportation", new ItemStack(Items.ENDER_PEARL));
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 3;
    }
}
