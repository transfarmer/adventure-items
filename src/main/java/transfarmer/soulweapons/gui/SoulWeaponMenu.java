package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.SoulWeaponType;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.network.ServerWeaponLevelup;
import transfarmer.soulweapons.network.ServerWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.SoulWeaponType.NONE;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends GuiScreen {
    private final ISoulWeapon instance = Minecraft.getMinecraft().player.getCapability(CAPABILITY, null);
    private final String TITLE;

    public SoulWeaponMenu(final String title) {
        this.TITLE = title;
    }

    @Override
    public void initGui() {
        if (instance.getCurrentType() == NONE) {
            showWeapons();
            return;
        }

        showAttributes();
    }

    public void showAttributes() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        int level = instance.getLevel();

        String plural = level > 1 ? "s" : "";
        String text = String.format("upgrade soul %s for %d experience level%s",
            instance.getCurrentType().getName(), level + 1, plural);
        int buttonWidth = (int) Math.floor(text.length() * 5.5) + 8;
        int buttonHeight = 20;
        int xCenter = (width - buttonWidth) / 2;
        int yCenter = (height - buttonHeight) / 2;
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

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 3) {
            Main.CHANNEL.sendToServer(new ServerWeaponLevelup());
        } else {
            Main.CHANNEL.sendToServer(new ServerWeaponType(SoulWeaponType.getType(button.id)));
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format(TITLE), this.width / 2, 40, 16777215);
        this.drawCenteredString(this.fontRenderer, String.format("current level: %d", instance.getLevel()),
                this.width / 2, this.height / 2 - 24, Integer.parseInt("FFFFFF", 16));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
