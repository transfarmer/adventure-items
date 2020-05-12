package transfarmer.soulboundarmory.skill.pick;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillTeleportation extends SkillBase {
    public SkillTeleportation() {
        super("teleportation", new ItemStack(Items.ENDER_PEARL));
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 3;
    }
}
