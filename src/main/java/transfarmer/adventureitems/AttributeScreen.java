package transfarmer.adventureitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import transfarmer.adventureitems.capabilities.ISoulWeapon;
import transfarmer.adventureitems.capabilities.SoulWeapon;
import transfarmer.adventureitems.capabilities.SoulWeapon.Type;
import transfarmer.adventureitems.capabilities.Provider;


public class AttributeScreen extends Screen {
    private final ISoulWeapon capability = (ISoulWeapon) Minecraft.getInstance().player.getCapability(Provider.TYPE);

    public AttributeScreen(ITextComponent title) {
        super(title);
    }

    public void init() {
        if (title.equals(new TranslationTextComponent("menu.adventureitems.attributes"))) {
            showAttributes();
        } else {
            showWeapons();
        }
    }

    public void showAttributes() {
        Minecraft.getInstance().player.sendChatMessage("work in progress");
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    public void showWeapons() {
        Minecraft.getInstance().player.sendChatMessage("method 1");
        int buttonWidth = 100;
        int xCenter = (width - buttonWidth) / 2;
        int buttonHeight = 20;
        int ySep = 32;

        addButton(new Button(xCenter, height / 2 - ySep, buttonWidth, buttonHeight, "soul bigsword", (ignored) -> capability.setCurrentType(SoulWeapon.Type.BIGSWORD)));
        addButton(new Button(xCenter, height / 2, buttonWidth, buttonHeight, "soul sword", (ignored) -> capability.setCurrentType(Type.SWORD)));
        addButton(new Button(xCenter, height / 2 + ySep, buttonWidth, buttonHeight, "soul dagger", (ignored) -> capability.setCurrentType(Type.DAGGER)));

    }
}
