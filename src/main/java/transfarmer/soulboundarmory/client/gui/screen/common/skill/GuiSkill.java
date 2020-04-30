package transfarmer.soulboundarmory.client.gui.screen.common.skill;

import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiExtended;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nullable;

public class GuiSkill extends Gui implements GuiExtended {
    protected final Skill skill;
    protected final ItemStack itemStack;

    public GuiSkill(final Skill skill, @Nullable final ItemStack itemStack) {
        this.skill = skill;
        this.itemStack = itemStack;
    }

    public void render(final int x, final int y) {
        this.render(x, y, 0);
    }

    public void render(final int x, final int y, final float zLevel) {
        if (this.itemStack == null) {
            this.renderCustom(x, y, zLevel);
        } else {
            this.renderItem(x, y, zLevel);
        }
    }

    public void renderCustom(final int x, final int y, final float zLevel) {
        TEXTURE_MANAGER.bindTexture(skill.getTexture());
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
    }

    public void renderItem(final int x, final int y, final float zLevel) {
        GuiExtended.renderItemModelIntoGUI(this.itemStack, x, y, RENDER_ITEM.getItemModelWithOverrides(this.itemStack, null, null), zLevel);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
