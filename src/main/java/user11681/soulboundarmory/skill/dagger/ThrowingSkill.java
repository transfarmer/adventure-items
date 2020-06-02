package user11681.soulboundarmory.skill.dagger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.client.gui.screen.common.ExtendedScreen;
import user11681.soulboundarmory.skill.Skill;

public class ThrowingSkill extends Skill {
    public ThrowingSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 2;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(final ExtendedScreen screen, final int level, final int x, final int y, final int blitOffset) {
        screen.renderGuiItem(Items.ARROW.getStackForRender(), x, y, blitOffset);
    }
}
