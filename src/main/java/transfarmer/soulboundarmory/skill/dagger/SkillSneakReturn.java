package transfarmer.soulboundarmory.skill.dagger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.farmerlib.util.CollectionUtil;

import javax.annotation.Nonnull;
import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.RETURN;

public class SkillSneakReturn extends Skill {
    public SkillSneakReturn() {
        super("sneak_return", new ItemStack(Items.LEAD));
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillReturn())
                : CollectionUtil.arrayList(this.storage.get(this.item, RETURN));
    }

    @Override
    public int getCost() {
        return 1;
    }
}
