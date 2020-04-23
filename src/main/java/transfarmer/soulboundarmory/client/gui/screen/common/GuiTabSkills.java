package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.skill.ISkill;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class GuiTabSkills extends GuiTabSoulbound {
    private static final ResourceLocation WINDOW = new ResourceLocation(Main.MOD_ID, "textures/gui/skill/window.png");

    private TextureManager textureManager;

    public GuiTabSkills(final Capability<? extends ISoulbound> key, final List<GuiTab> tabs) {
        super(key, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_SKILLS;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.textureManager = this.mc.getTextureManager();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.textureManager.bindTexture(WINDOW);
        this.drawTexturedModalRect(this.width / 2 - 128, this.height / 2 - 96, 0, 0, 256, 192);
        this.textureManager.deleteTexture(WINDOW);

        for (final ISkill skill : this.capability.getSkills()) {
            this.drawSkill(skill);
        }
    }

    protected void drawSkill(final ISkill skill) {
        final ResourceLocation texture = skill.getTexture();

        try {
            final ImageInputStream stream = ImageIO.createImageInputStream(this.mc.getResourceManager().getResource(texture).getInputStream());
            final BufferedImage image = ImageIO.read(stream);

            this.textureManager.bindTexture(texture);
            this.drawTexturedModalRect(this.width / 2 + new Random().nextInt(10) - 5, this.height / 2, 0, 0, image.getWidth(), image.getHeight());
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }
    }

    @Override
    public void actionPerformed(@NotNull final GuiButton button) {
        super.actionPerformed(button);
    }
}
