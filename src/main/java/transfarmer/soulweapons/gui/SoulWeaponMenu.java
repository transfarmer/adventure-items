package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.network.ServerWeaponLevelup;
import transfarmer.soulweapons.network.ServerWeaponType;

import java.io.IOException;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends GuiScreen {
    private String title;
    private String weaponName;
    int level;

    public SoulWeaponMenu(String title, String weaponName) {
        this.title = title;
        this.weaponName = weaponName;
    }

    public SoulWeaponMenu(String title) {
        this.title = title;
    }

    @Override
    public void initGui() {
        if (weaponName == null) {
            showWeapons();
            return;
        }

        showAttributes();
    }

    public void showAttributes() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        level = player.getCapability(CAPABILITY, null).getLevel() + 1;

        String plural = level > 1 ? "s" : "";
        String text = String.format("increase soul %s level for %d experience level%s", weaponName.toLowerCase(), level, plural);
        int buttonWidth = (int) Math.round(text.length() * 5.25) + 4;
        int buttonHeight = 20;
        int xCenter = (width - buttonWidth) / 2;
        int yCenter = height / 2;
        GuiButton levelButton = addButton(new GuiButton(3, xCenter, yCenter, buttonWidth, buttonHeight, text));
        levelButton.enabled = player.experienceLevel > level;
    }

    public void showWeapons() {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int xCenter = (width - buttonWidth) / 2;
        int ySep = 32;

        addButton(new GuiButton(0, xCenter, height / 2 - ySep, buttonWidth, buttonHeight, "soul bigsword"));
        addButton(new GuiButton(1, xCenter, height / 2, buttonWidth, buttonHeight, "soul sword"));
        addButton(new GuiButton(2,xCenter, height / 2 + ySep, buttonWidth, buttonHeight, "soul dagger"));
    }

    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 0 && button.id <= 2) {
            Main.CHANNEL.sendToServer(new ServerWeaponType(button.id));
        } else {
            Main.CHANNEL.sendToServer(new ServerWeaponLevelup());
        }

        this.mc.displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format(title), this.width / 2, 40, 16777215);
        this.drawCenteredString(this.fontRenderer, String.format("current level: %d", level - 1),
                this.width / 2, this.height / 2 - 24, Integer.parseInt("FFFFFF", 16));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
