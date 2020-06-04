package user11681.soulboundarmory.skill.greatsword;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.registry.Skills;
import user11681.usersmanual.client.gui.screen.ExtendedScreen;

public class FreezingSkill extends Skill {
    public FreezingSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.LEAPING);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 2;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(final ExtendedScreen screen, final int level, final int x, final int y, final int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.SNOWBALL), x, y, zOffset);
    }
}
