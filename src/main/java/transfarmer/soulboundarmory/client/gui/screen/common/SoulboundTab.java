package transfarmer.soulboundarmory.client.gui.screen.common;

import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.renderer.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import transfarmer.farmerlib.util.ItemUtil;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.KeyBindings;
import transfarmer.soulboundarmory.client.gui.RGBASlider;
import transfarmer.soulboundarmory.client.gui.ExtendedButtonWidget;
import transfarmer.soulboundarmory.client.gui.GuiXPBar;
import transfarmer.soulboundarmory.client.gui.GuiXPBar.Style;
import transfarmer.soulboundarmory.client.gui.Slider;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.network.C2S.C2SBindSlot;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.MainClient.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@Environment(EnvType.CLIENT)
public abstract class SoulboundTab extends ScreenTab {
    protected static final NumberFormat FORMAT = DecimalFormat.getInstance();

    protected final ComponentType<? extends ISoulboundComponent> componentType;
    @Nonnull
    protected final PlayerEntity player;
    protected final List<ButtonWidget> options;

    protected ISoulboundComponent component;
    protected IItem item;
    protected GuiXPBar xpBar;
    protected List<RGBASlider> rgbaSliders;
    protected ExtendedButtonWidget styleButton;
    protected int slot;

    public SoulboundTab(final Text title, final ComponentType<? extends ISoulboundComponent> componentType, final List<ScreenTab> tabs) {
        super(title, tabs);

        this.componentType = componentType;
        //noinspection ConstantConditions
        this.player = CLIENT.player;
        this.options = new ArrayList<>(4);
    }

    @Override
    public void init() {
        this.displayTabs = ItemUtil.getEquippedItemStack(player.inventory, ItemSoulbound.class) != null;

        super.init();

        this.component = ComponentProvider.fromEntity(this.player).getComponent(this.componentType);
        this.component.setCurrentTab(this.index);
        this.xpBar = new GuiXPBar(this.component);
        this.item = component.getItemType();
        this.slot = ItemUtil.getSlotFor(this.player.inventory, component.getEquippedItemStack());

        this.initOptions();

        if (this.displayTabs) {
            final String text = this.slot != component.getBoundSlot()
                    ? Mappings.MENU_BUTTON_BIND
                    : Mappings.MENU_BUTTON_UNBIND;
            final int width = Math.max(this.button.getWidth(), TEXT_RENDERER.getStringWidth(text) + 8);
            final int x = this.button.endX - width;

            this.addButton(new ButtonWidget(x, this.height - this.height / 16 - 20, width, 20, text, new )
            );
        }
    }

    protected void initOptions() {
        if (ClientConfig.getDisplayOptions() && this.displayXPBar()) {
            this.options.add(this.addButton(this.redSlider = this.colorSlider(1000, 0, ClientConfig.getRed(), Mappings.RED + ": ")));
            this.options.add(this.addButton(this.greenSlider = this.colorSlider(1001, 1, ClientConfig.getGreen(), Mappings.GREEN + ": ")));
            this.options.add(this.addButton(this.blueSlider = this.colorSlider(1002, 2, ClientConfig.getBlue(), Mappings.BLUE + ": ")));
            this.options.add(this.addButton(this.alphaSlider = this.colorSlider(1003, 3, ClientConfig.getAlpha(), Mappings.ALPHA + ": ")));
            this.options.add(this.addButton(this.styleButton = this.optionButton(1004, 4, String.format("%s: %s", Mappings.XP_BAR_STYLE, ClientConfig.getStyle().toString()))));
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.blitOffset -= 500;
        this.drawDefaultBackground();
        this.blitOffset += 500;

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.displayXPBar()) {
            this.drawXPBar(mouseX, mouseY);
        }
    }

    protected void drawXPBar(final int mouseX, final int mouseY) {
        final int xp = component.getDatum(this.item, XP);

        this.xpBar.drawXPBar(this.getXPBarX(), this.getXPBarY(), 182);

        if (this.isMouseOverLevel(mouseX, mouseY) && MainConfig.instance().getMaxLevel() >= 0) {
            this.drawHoveringText(String.format("%d/%d", this.component.getDatum(LEVEL), MainConfig.instance().getMaxLevel()), mouseX, mouseY);
        } else if (this.isMouseOverXPBar(mouseX, mouseY)) {
            this.drawHoveringText(this.component.canLevelUp(this.item)
                    ? String.format("%d/%d", xp, component.getNextLevelXP(this.item))
                    : String.format("%d", xp), mouseX, mouseY);
        }

        RenderSystem.disableLighting();
    }

    protected boolean displayXPBar() {
        return this.item != null;
    }

    protected boolean isMouseOverXPBar(final int mouseX, final int mouseY) {
        final int barX = this.getXPBarX();
        final int barY = this.getXPBarY();

        return this.displayXPBar() && mouseX >= barX && mouseX <= barX + 182 && mouseY >= barY && mouseY <= barY + 4;
    }

    protected int getXPBarY() {
        return this.height - 29;
    }

    protected int getXPBarX() {
        return (this.width - 182) / 2;
    }

    protected boolean isMouseOverLevel(final int mouseX, final int mouseY) {
        final String levelString = "" + this.component.getDatum(this.item, LEVEL);

        final int levelLeftX = (this.width - TEXT_RENDERER.getStringWidth(levelString)) / 2;
        final int levelTopY = height - 35;

        return mouseX >= levelLeftX && mouseX <= levelLeftX + TEXT_RENDERER.getStringWidth(levelString)
                && mouseY >= levelTopY && mouseY <= levelTopY + TEXT_RENDERER.fontHeight;
    }

    protected RGBASlider sliderMousedOver(final double mouseX, final double mouseY) {
        for (int slider = 0; slider < 4; slider++) {
            if (mouseX >= this.getOptionX() && mouseX <= this.getOptionX() + 100
                    && mouseY >= this.getOptionY(slider) && mouseY <= this.getOptionY(slider) + 20) {
                return this.rgbaSliders.get(0);
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (this.isMouseOverXPBar(mouseX, mouseY)) {
            if (!this.buttonList.contains(this.alphaSlider)) {
                this.buttonList.addAll(this.options);
                ClientConfig.setDisplayOptions(true);
            } else {
                this.buttonList.removeAll(this.options);
                ClientConfig.setDisplayOptions(false);
            }

            ClientConfig.instance().save();
            this.refresh();
        } else if (this.styleButton != null && this.styleButton.isMouseHoveringOver()) {
            this.cycleStyle(-1);
        }
        return false;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (scanCode == KeyBindings.MENU_KEY.getKeyCode()) {
            this.onClose();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double dWheel) {
        final RGBASlider slider = this.sliderMousedOver(x, y);

        if (slider != null) {
            final String key = slider.getColor();
            final int value = MathHelper.clamp(ClientConfig.getRGBA(key), 0, 255);
            ClientConfig.setColor(key, value);

            slider.setValue(value);
            slider.updateSlider();
        }

        return super.mouseScrolled(x, y, dWheel);
    }

    @Override
    public void actionPerformed(@NotNull ButtonWidget button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 16:
            case 17:
            case 18:
            case 19:
                this.setTab(button.id - 16);
                break;
            case 22:
                Main.CHANNEL.sendToServer(new C2SBindSlot(this.component.getType(), this.slot));
                break;
            case 1000:
            case 1001:
            case 1002:
            case 1003:
                this.updateSettings();
                break;
            case 1004:
                this.cycleStyle(1);
        }
    }

    protected void cycleStyle(final int change) {
        int index = (Style.STYLES.indexOf(ClientConfig.getStyle()) + change) % Style.AMOUNT;

        if (index < 0) {
            this.cycleStyle(Style.AMOUNT + index);
        } else {
            ClientConfig.setStyle(Style.STYLES.get(index));
            this.refresh();
        }
    }

    private void updateSettings() {
        ClientConfig.setRed(this.redSlider.getValueInt());
        ClientConfig.setGreen(this.greenSlider.getValueInt());
        ClientConfig.setBlue(this.blueSlider.getValueInt());
        ClientConfig.setAlpha(this.alphaSlider.getValueInt());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    public ButtonWidget centeredButton(final int y, final int buttonWidth, final String text) {
        return new ButtonWidget((this.width - buttonWidth) / 2, y, buttonWidth, 20, text);
    }

    public ButtonWidget squareButton(final int x, final int y, final String text) {
        return new ButtonWidget(x - 10, y - 10, 20, 20, text);
    }

    public ButtonWidget resetButton() {
        return new ButtonWidget(this.width - this.width / 24 - 112, this.height - this.height / 16 - 20, 112, 20, Mappings.MENU_BUTTON_RESET);
    }

    public ExtendedButtonWidget optionButton(final int row, final String text) {
        return new ExtendedButtonWidget(this.getOptionX(), this.getOptionY(row), 100, 20, text);
    }

    public Slider colorSlider(final int row, final double currentValue, final String text) {
        return new RGBASlider(this.getOptionX(), this.getOptionY(row), 100, 20, currentValue, text);
    }

    public ButtonWidget[] addPointButtons(final int rows, final int points) {
        final ButtonWidget[] buttons = new ButtonWidget[rows];
        final int start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (int row = 0; row < rows; row++) {
            buttons[row] = squareButton((this.width + 162) / 2, start + row * this.height / 16 + 4, "+");
            buttons[row].active = points > 0;
        }

        return buttons;
    }

    public ButtonWidget[] removePointButtons(final int rows) {
        final ButtonWidget[] buttons = new ButtonWidget[rows];
        final int start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (int row = 0; row < rows; row++) {
            buttons[row] = squareButton((this.width + 162) / 2 - 20, start + row * this.height / 16 + 4, "-");
        }

        return buttons;
    }

    public int getOptionX() {
        return Math.round(this.width * (1 - 1 / 24F)) - 100;
    }

    public int getOptionY(final int row) {
        return this.height / 16 + Math.max(this.height / 16 * row, 30 * row);
    }

    public void drawMiddleAttribute(final String format, final double value, final int row, final int rows) {
        TEXT_RENDERER.draw(String.format(format, FORMAT.format(value)), (this.width - 182) / 2F, this.getHeight(rows, row), 0xFFFFFF);
    }
}
