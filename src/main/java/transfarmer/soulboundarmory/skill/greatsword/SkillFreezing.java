package transfarmer.soulboundarmory.skill.greatsword;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;
import transfarmer.farmerlib.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.init.Skills.LEAPING;

public class SkillFreezing extends SkillBaseLevelable {
    public SkillFreezing() {
        this(0);
    }

    public SkillFreezing(final int level) {
        super("freezing", new ItemStack(Items.SNOWBALL), level);
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillLeaping())
                : CollectionUtil.arrayList(this.storage.get(this.item, LEAPING));
    }

    @Override
    public int getCost() {
        return 2;
    }
}
