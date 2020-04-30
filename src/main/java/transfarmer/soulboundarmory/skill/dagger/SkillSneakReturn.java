package transfarmer.soulboundarmory.skill.dagger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.RETURN;

public class SkillSneakReturn extends SkillBase {
    public SkillSneakReturn() {
        super("sneak_return", new ItemStack(Items.LEAD));
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillReturn())
                : CollectionUtil.arrayList(this.storage.get(this.item, RETURN));
    }

    @Override
    public int getCost() {
        return 1;
    }
}
