package transfarmer.soulboundarmory.client.gui.screen.common.skill;

import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.client.gui.screen.common.ExtendedScreen;

import javax.annotation.Nullable;
import java.util.Map;

public class SkillGUI extends ExtendedScreen {
    public static final Map<Skill,>

    protected final Skill skill;
    protected final ItemStack itemStack;

    public SkillGUI(final Skill skill, @Nullable final ItemStack itemStack) {
        super();

        this.skill = skill;
        this.itemStack = itemStack;
    }

    public void render(final int x, final int y, final int zLevel) {

        this.withZ(zLevel, () -> {
            if (this.itemStack == null) {
                this.renderCustom(x, y);
            } else {
                this.renderItem(x, y);
            }
        });
    }

    public void renderCustom(final int x, final int y) {
        TEXTURE_MANAGER.bindTexture(skill.getTexture());
        this.blit(x, y, 0, 0, 16, 16);
    }

    public void renderItem(final int x, final int y) {
        this.itemRenderer.renderGuiItem(this.itemStack, x, y);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
