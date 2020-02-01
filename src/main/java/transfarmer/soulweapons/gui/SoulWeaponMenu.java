package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.weapon.SoulWeaponAttribute;
import transfarmer.soulweapons.weapon.SoulWeaponType;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeapon;
import transfarmer.soulweapons.network.ServerAddAttribute;
import transfarmer.soulweapons.network.ServerWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.weapon.SoulWeaponType.NONE;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends GuiScreen {
    private final GUIFactory guiFactory = new GUIFactory();
    private final ISoulWeapon instance = Minecraft.getMinecraft().player.getCapability(CAPABILITY, null);
    private final boolean newWeapon = Minecraft.getMinecraft().player.getHeldItemMainhand().getItem().equals(Items.WOODEN_SWORD)
        || instance.getCurrentType() == NONE;
    private String title;

    public SoulWeaponMenu() {}

    @Override
    public void initGui() {
        if (newWeapon) {
            this.title = I18n.format("menu.soulweapons.weapons");
            showWeapons();
            return;
        }

        this.title = I18n.format("menu.soulweapons.attributes");
        showAttributes();
    }

    public void showAttributes() {
        addButton(guiFactory.centeredButton(3, 3 * height / 4, width / 8, "close"));

        if (instance.getPoints() == 0) return;

        addButton(guiFactory.addPointButton(4, width / 16 + 144, height / 8 - 2));
        addButton(guiFactory.addPointButton(5, width / 16 + 144, 3 * height / 16 + 2));
        addButton(guiFactory.addPointButton(6, width / 16 + 144, height / 4 + 6));

        addButton(guiFactory.addPointButton(7, width / 2 + 72, height / 8 - 2));
        addButton(guiFactory.addPointButton(8, width / 2 + 72, 3 * height / 16 + 2));
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
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                Main.CHANNEL.sendToServer(new ServerAddAttribute(SoulWeaponAttribute.getAttribute(button.id)));
                break;
            default:
                this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int white = Integer.parseInt("FFFFFF", 16);

        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format(title), this.width / 2, height / 18, white);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (newWeapon) return;

        this.drawString(this.fontRenderer, String.format("attack speed: %.1f", instance.getAttackSpeed() + 4), width / 16, height / 8, white);
        this.drawString(this.fontRenderer, "damage: " + (instance.getAttackDamage() + 1), width / 16, 3 * height / 16, white);
        this.drawString(this.fontRenderer, "critical strike chance: " + instance.getCritical() + "%", width / 16, height / 4, white);

        this.drawString(this.fontRenderer, "knockback: " + instance.getKnockback(), width / 2 - 24, height / 8, white);
        this.drawString(this.fontRenderer, "efficiency: " + instance.getEfficiency(), width / 2 - 24, 3 * height / 16, white);

        for (int i = 0; i < instance.getSpecial(); i++) {
            this.drawCenteredString(this.fontRenderer, SoulWeapon.specialNames[instance.getCurrentType().index][i],
                width - 100, (i + 2) * height / 16, white);
        }

        this.renderXPBar();
    }

    public void renderXPBar() {
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        int level = this.instance.getLevel();

        int y0 = height / 2 - 2;
        int x0 = (width - 182) / 2;
        this.drawTexturedModalRect(x0, y0, 0, 74, 182, 5);
        this.drawTexturedModalRect(x0, y0, 0, 79, Math.round((float) instance.getXP() / instance.getNextLevelXP() * 182), 5);

        String levelString = String.format("%d", level);
        int x1 = Math.round((width - this.fontRenderer.getStringWidth(levelString)) / 2F) + 1;
        int y1 = height / 2 - 8;
        this.fontRenderer.drawString(levelString, x1 + 1, y1, 0);
        this.fontRenderer.drawString(levelString, x1 - 1, y1, 0);
        this.fontRenderer.drawString(levelString, x1, y1 + 1, 0);
        this.fontRenderer.drawString(levelString, x1, y1 - 1, 0);
        this.fontRenderer.drawString(levelString, x1, y1, Integer.parseInt("EC33B8", 16));
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

        public GuiButton addPointButton(int id, int x, int y) {
            return new GuiButton(id, x - 10, y - 10, 20, 20, "+");
        }
    }
}
