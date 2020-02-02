package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Configuration;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeapon;
import transfarmer.soulweapons.network.ServerAddAttribute;
import transfarmer.soulweapons.network.ServerWeaponType;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.EFFICIENCY;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.KNOCKBACK;
import static transfarmer.soulweapons.weapon.SoulWeaponType.DAGGER;
import static transfarmer.soulweapons.weapon.SoulWeaponType.GREATSWORD;
import static transfarmer.soulweapons.weapon.SoulWeaponType.NONE;
import static transfarmer.soulweapons.weapon.SoulWeaponType.SWORD;

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

        if (instance.getPoints() > 0) {
            addButton(guiFactory.addPointButton(4, width / 16 + 144, height / 8 - 2));
            addButton(guiFactory.addPointButton(5, width / 16 + 144, 3 * height / 16 + 2));
            addButton(guiFactory.addPointButton(6, width / 16 + 144, height / 4 + 6));

            addButton(guiFactory.addPointButton(7, width / 2 + 72, height / 8 - 2));
            addButton(guiFactory.addPointButton(8, width / 2 + 72, 3 * height / 16 + 2));
        }
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
                Main.CHANNEL.sendToServer(new ServerWeaponType(GREATSWORD));
                this.mc.displayGuiScreen(null);
                break;
            case 1:
                Main.CHANNEL.sendToServer(new ServerWeaponType(SWORD));
                this.mc.displayGuiScreen(null);
                break;
            case 2:
                Main.CHANNEL.sendToServer(new ServerWeaponType(DAGGER));
                this.mc.displayGuiScreen(null);
                break;
            case 3:
                this.mc.displayGuiScreen(null);
                break;
            case 4:
                Main.CHANNEL.sendToServer(new ServerAddAttribute(ATTACK_SPEED));
                break;
            case 5:
                Main.CHANNEL.sendToServer(new ServerAddAttribute(ATTACK_DAMAGE));
                break;
            case 6:
                Main.CHANNEL.sendToServer(new ServerAddAttribute(CRITICAL));
                break;
            case 7:
                Main.CHANNEL.sendToServer(new ServerAddAttribute(KNOCKBACK));
                break;
            case 8:
                Main.CHANNEL.sendToServer(new ServerAddAttribute(EFFICIENCY));
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final Renderer RENDERER = new Renderer();

        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format(title), this.width / 2, height / 18, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (newWeapon) return;

        RENDERER.drawLeftAttribute("attack speed: %s", instance.getAttackSpeed() + 4, 0);
        RENDERER.drawLeftAttribute("attack damage: %s", instance.getAttackDamage() + 1, 1);
        RENDERER.drawLeftAttribute("critical strike chance: %s%%", instance.getCritical(), 2);

        RENDERER.drawMiddleAttribute("knockback: %s", instance.getKnockback(), 0);
        RENDERER.drawMiddleAttribute("efficiency: %s", instance.getEfficiency(), 1);

        for (int i = 0; i < instance.getSkills(); i++) {
            this.drawCenteredString(this.fontRenderer, SoulWeapon.skillNames[instance.getCurrentType().index][i],
                width - 100, (i + 2) * height / 16, 0xFFFFFF);
        }

        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        int level = this.instance.getLevel();

        int barLeftX = (width - 182) / 2;
        int barTopY = (height - 4) / 2;
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 74, 182, 5);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 79, Math.round((float) instance.getXP() / instance.getNextLevelXP() * 182), 5);

        String levelString = String.format("%d", level);
        int levelLeftX = Math.round((width - this.fontRenderer.getStringWidth(levelString)) / 2F) + 1;
        int levelTopY = height / 2 - 8;
        this.fontRenderer.drawString(levelString, levelLeftX + 1, levelTopY, 0);
        this.fontRenderer.drawString(levelString, levelLeftX - 1, levelTopY, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY + 1, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY - 1, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY, Integer.parseInt("EC33B8", 16));

        if (mouseX >= levelLeftX && mouseX <= levelLeftX + this.fontRenderer.getStringWidth(levelString)
            && mouseY >= levelTopY && mouseY <= levelTopY + this.fontRenderer.FONT_HEIGHT) {
            this.drawHoveringText(String.format("%d/%d", instance.getLevel(), Configuration.maxLevel), mouseX, mouseY);
        } else if (mouseX >= (width - 182) / 2 && mouseX <= barLeftX + 182 && mouseY >= barTopY && mouseY <= barTopY + 4) {
            this.drawHoveringText(String.format("%d/%d", instance.getXP(), instance.getNextLevelXP()), mouseX, mouseY);
        }
    }

    public class GUIFactory {
        public GuiButton centeredButton(int id, int y, int buttonWidth, String text) {
            return new GuiButton(id, (width - buttonWidth) / 2, y, buttonWidth, 20, text);
        }

        public GuiButton addPointButton(int id, int x, int y) {
            return new GuiButton(id, x - 10, y - 10, 20, 20, "+");
        }
    }

    public class Renderer {
        private final NumberFormat FORMAT = DecimalFormat.getInstance();
        private final int white = 0xFFFFFF;

        public void drawLeftAttribute(String format, float value, int row) {
            drawString(fontRenderer, String.format(format, FORMAT.format(value)), width / 16, (row + 2) * height / 16, white);
        }

        public void drawMiddleAttribute(String format, float value, int row) {
            drawString(fontRenderer, String.format(format, FORMAT.format(value)), width / 2 - 24, (row + 2) * height / 16, white);
        }
    }
}
