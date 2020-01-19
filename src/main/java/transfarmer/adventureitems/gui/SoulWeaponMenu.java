package transfarmer.adventureitems.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.OnlyIn;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.SoulWeapons.WeaponType;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.network.RequestWeaponLevelup;
import transfarmer.adventureitems.network.RequestWeaponType;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static transfarmer.adventureitems.SoulWeapons.WeaponType.*;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.*;

@OnlyIn(CLIENT)
public class SoulWeaponMenu extends Screen {
    private WeaponType weaponType;
    private int level;

    public SoulWeaponMenu(ITextComponent title) {
        super(title);
    }

    public SoulWeaponMenu(ITextComponent title, WeaponType weaponType) {
        super(title);
        this.weaponType = weaponType;
    }

    @Override
    public void init() {
        if (weaponType == null) {
            showWeapons();
            return;
        }

        showAttributes();
    }

    public void showAttributes() {
        PlayerEntity player = Minecraft.getInstance().player;
        player.getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon capability) ->
                level = capability.getLevel() + 1
        );

        int buttonWidth = 288;
        int xCenter = (width - buttonWidth) / 2;
        int yCenter = height / 2;
        int buttonHeight = 20;
        String plural = level > 1 ? "s" : "";
        Button levelButton = addButton(new Button(xCenter, yCenter, buttonWidth, buttonHeight,
        String.format("increase soul %s level for %d experience level%s",
        weaponType.toString().toLowerCase(), level, plural), (ignored) -> {
            Main.CHANNEL.sendToServer(new RequestWeaponLevelup());
            this.minecraft.displayGuiScreen(null);
        }));
        player.getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon capability) ->
                levelButton.active = player.experienceLevel > capability.getLevel());
    }

    public void showWeapons() {
        int buttonWidth = 100;
        int xCenter = (width - buttonWidth) / 2;
        int buttonHeight = 20;
        int ySep = 32;

        addButton(new Button(xCenter, height / 2 - ySep, buttonWidth, buttonHeight, "soul bigsword",
                (ignored) -> requestWeaponType(BIGSWORD)));
        addButton(new Button(xCenter, height / 2, buttonWidth, buttonHeight, "soul sword",
                (ignored) -> requestWeaponType(SWORD)));
        addButton(new Button(xCenter, height / 2 + ySep, buttonWidth, buttonHeight, "soul dagger",
                (ignored) -> requestWeaponType(DAGGER)));
    }

    private void requestWeaponType(final WeaponType WEAPON_TYPE) {
        Main.CHANNEL.sendToServer(new RequestWeaponType(WEAPON_TYPE, true));
        this.minecraft.displayGuiScreen(null);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
}
