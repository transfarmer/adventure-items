package transfarmer.soulboundarmory.skill.greatsword;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.client.gui.screen.common.ExtendedScreen;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.Skills;

public class FreezingSkill extends Skill {
    public FreezingSkill(final Identifier identifier) {
        super(identifier, Skills.LEAPING);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 2;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(final ExtendedScreen screen, final int level, final int x, final int y, final int blitOffset) {
        screen.withZ(blitOffset, () -> screen.itemRenderer.renderGuiItem(new ItemStack(Items.SNOWBALL), x, y));
    }
}
