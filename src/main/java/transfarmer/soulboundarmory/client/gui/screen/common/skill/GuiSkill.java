package transfarmer.soulboundarmory.client.gui.screen.common.skill;

import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiScreenExtended;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nullable;

public class GuiSkill extends GuiScreenExtended {
    protected final Skill skill;
    protected final ItemStack itemStack;

    public GuiSkill(final Skill skill, @Nullable final ItemStack itemStack) {
        this.skill = skill;
        this.itemStack = itemStack;
    }

    public void render(final int x, final int y, final int color, final float zLevel) {
        final float prev = this.zLevel;

        this.zLevel = zLevel;

        if (this.itemStack == null) {
            this.renderCustom(x, y);
        } else {
            this.renderItem(x, y, color);
        }

        this.zLevel = prev;
    }

    public void renderCustom(final int x, final int y) {
        TEXTURE_MANAGER.bindTexture(skill.getTexture());
        this.drawRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
    }

    @SuppressWarnings("ConstantConditions")
    public void renderItem(final int x, final int y, final int color) {
        this.renderItemModelIntoGUI(this.itemStack, x, y, RENDER_ITEM.getItemModelWithOverrides(this.itemStack, null, null), color);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
