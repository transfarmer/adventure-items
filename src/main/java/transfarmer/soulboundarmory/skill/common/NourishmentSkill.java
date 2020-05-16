package transfarmer.soulboundarmory.skill.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.client.gui.screen.common.ExtendedScreen;
import transfarmer.soulboundarmory.skill.Skill;

public class NourishmentSkill extends Skill {
    public NourishmentSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return !learned ? 3 : level + 1;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(final ExtendedScreen screen, final int level, final int x, final int y, final int blitOffset) {
        screen.withZ(blitOffset, () -> screen.itemRenderer.renderGuiItem(new ItemStack(Items.COOKED_BEEF), x, y));
    }
}
