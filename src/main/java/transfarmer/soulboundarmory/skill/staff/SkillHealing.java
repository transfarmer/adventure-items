package transfarmer.soulboundarmory.skill.staff;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillHealing extends SkillBaseLevelable {
    public SkillHealing() {
        this(0);
    }

    public SkillHealing(final int level) {
        super("healing", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HEALING), level);
    }

    @Nonnull
    @Override
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 2;
    }
}
