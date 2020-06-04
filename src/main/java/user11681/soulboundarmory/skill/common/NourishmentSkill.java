package user11681.soulboundarmory.skill.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;
import user11681.usersmanual.client.gui.screen.ExtendedScreen;

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
    public void render(final ExtendedScreen screen, final int level, final int x, final int y, final int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.COOKED_BEEF), x, y, zOffset);
    }
}
