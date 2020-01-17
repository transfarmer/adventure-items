package transfarmer.adventureitems.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import transfarmer.adventureitems.capability.SoulWeapon.WeaponType;
import transfarmer.adventureitems.network.Packet;
import transfarmer.adventureitems.network.PacketHandler;

import static transfarmer.adventureitems.capability.SoulWeapon.WeaponType.*;


public class AttributeScreen extends Screen {
    private WeaponType choice;

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
    }

    public void showWeapons() {
        int buttonWidth = 100;
        int xCenter = (width - buttonWidth) / 2;
        int buttonHeight = 20;
        int ySep = 32;

        addButton(new Button(xCenter, height / 2 - ySep, buttonWidth, buttonHeight, "soul bigsword",
                (ignored) -> sendWeaponType(BIGSWORD)));
        addButton(new Button(xCenter, height / 2, buttonWidth, buttonHeight, "soul sword",
                (ignored) -> sendWeaponType(SWORD)));
        addButton(new Button(xCenter, height / 2 + ySep, buttonWidth, buttonHeight, "soul dagger",
                (ignored) -> sendWeaponType(DAGGER)));
    }

    private void sendWeaponType(final WeaponType WEAPON_TYPE) {
        PacketHandler.INSTANCE.sendToServer(new Packet(WEAPON_TYPE));
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
}
