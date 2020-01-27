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
import transfarmer.soulweapons.capability.SoulWeapon;
import transfarmer.soulweapons.network.ServerWeaponLevelup;
import transfarmer.soulweapons.network.ServerWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.SoulWeaponType.NONE;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends GuiScreen {
    private final GUIFactory guiFactory = new GUIFactory();
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
        GuiButton levelButton = addButton(guiFactory.autoCenteredButton(3, text));
        levelButton.enabled = player.experienceLevel > level;
        addButton(guiFactory.centeredButton(4, 2 * height / 3, width / 8, "close"));
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
        switch (button.id) {
            case 0:
            case 1:
            case 2:
                Main.CHANNEL.sendToServer(new ServerWeaponType(SoulWeaponType.getType(button.id)));
                this.mc.displayGuiScreen(null);
                break;
            case 3:
                Main.CHANNEL.sendToServer(new ServerWeaponLevelup());
                break;
            case 4:
                this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int white = Integer.parseInt("FFFFFF", 16);

        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format(TITLE), this.width / 2, height / 18, white);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (instance.getCurrentType() == NONE) return;

        this.drawString(this.fontRenderer, "damage: " + (instance.getAttackDamage() + 1), width / 16, height / 8, white);
        this.drawString(this.fontRenderer, "critical: " + instance.getCritical(), width / 16, 3 * height / 16, white);
        this.drawString(this.fontRenderer, "weapon level: " + instance.getLevel(), width / 16, height / 4, white);

        this.drawCenteredString(this.fontRenderer, "efficiency: " + instance.getEfficiency(), width / 2, height / 8, white);
        this.drawCenteredString(this.fontRenderer, "knockback: " + instance.getKnockback(), width / 2, 3 * height / 16, white);

        for (int i = 0; i < instance.getSpecial(); i++) {
            this.drawCenteredString(this.fontRenderer, SoulWeapon.specialNames[instance.getCurrentType().getIndex()][i],
                width - 100, (i + 1) * height / 16, white);
        }
    }

    public class GUIFactory {
        public GuiButton autoCenteredButton(int id, String text) {
            int y = (height - 20) / 2;
            return autoCenteredButton(id, y, text);
        }

        public GuiButton autoCenteredButton(int id, int y, String text) {
            int buttonWidth = (int) Math.floor(text.length() * 5.5) + 8;
            int x = (width - buttonWidth) / 2;
            return new GuiButton(id, x, y, buttonWidth, 20, text);
        }

        public GuiButton centeredButton(int id, int buttonWidth, String text) {
            return centeredButton(id, (width - 20) / 2, buttonWidth, text);
        }

        public GuiButton centeredButton(int id, int y, int buttonWidth, String text) {
            return new GuiButton(id, (width - buttonWidth) / 2, y, buttonWidth, 20, text);
        }
    }
}
