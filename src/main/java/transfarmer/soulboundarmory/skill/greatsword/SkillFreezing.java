package transfarmer.soulboundarmory.skill.greatsword;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.farmerlib.util.CollectionUtil;

import javax.annotation.Nonnull;
import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.LEAPING;

public class SkillFreezing extends Skill {
    public SkillFreezing() {
        this(0);
    }

    public SkillFreezing(final int level) {
        super("freezing", new ItemStack(Items.SNOWBALL), level);
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillLeaping())
                : CollectionUtil.arrayList(this.storage.get(this.item, LEAPING));
    }

    @Override
    public int getCost() {
        return 2;
    }
}
