package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiExtended;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import java.awt.*;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.MOD_ID;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@SideOnly(CLIENT)
public class GuiXPBar extends Gui implements GuiExtended {
    public static final ResourceLocation XP_BAR = new ResourceLocation(MOD_ID, "textures/gui/xp_bar.png");

    protected ItemStack itemStack;
    protected SoulboundCapability capability;
    protected IItem itemType;
    protected int row;
    protected int length;

    public GuiXPBar() {
    }

    public GuiXPBar(final ItemStack itemStack) {
        this.update(itemStack);
    }

    public GuiXPBar(final SoulboundCapability capability) {
        this.update(capability);
    }

    public void setData(final int row, final int length) {
        this.row = row;
        this.length = length;
    }

    public void drawTooltip(final int tooltipX, final int tooltipY, final ItemStack itemStack) {
        if (this.itemStack != itemStack) {
            this.update(itemStack);
        }

        final int x = tooltipX + 4;
        final int y = tooltipY + this.row * 10;

        this.drawXPBar(x, y, this.length);
    }

    public void drawXPBar(final int x, final int y, final int length) {
        final float ratio = (float) this.capability.getDatum(this.itemType, XP) / this.capability.getNextLevelXP(this.itemType);
        final float effectiveLength = ratio * length;
        final int middleU = (int) Math.min(4, effectiveLength);
        final Color color = new Color(ClientConfig.getRed(), ClientConfig.getGreen(), ClientConfig.getBlue(), ClientConfig.getAlpha());

        GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        TEXTURE_MANAGER.bindTexture(XP_BAR);

        GuiExtended.drawHorizontalInterpolatedTexturedRect(x, y, 0, 0, 4, 177, 182, length, 5);
        GuiExtended.drawHorizontalInterpolatedTexturedRect(x, y, 0, 5, middleU, effectiveLength < 4 ? middleU : (int) (ratio * 177), (int) (ratio * 182), this.capability.canLevelUp(this.itemType)
                ? Math.min(length, (int) (ratio * length))
                : length, 5
        );

        final int level = this.capability.getDatum(this.itemType, LEVEL);

        if (level > 0) {
            final String levelString = String.format("%d", level);
            final int levelX = x + (length - FONT_RENDERER.getStringWidth(levelString)) / 2;
            final int levelY = y - 6;

            FONT_RENDERER.drawString(levelString, levelX + 1, levelY, 0);
            FONT_RENDERER.drawString(levelString, levelX - 1, levelY, 0);
            FONT_RENDERER.drawString(levelString, levelX, levelY + 1, 0);
            FONT_RENDERER.drawString(levelString, levelX, levelY - 1, 0);
            FONT_RENDERER.drawString(levelString, levelX, levelY, color.getRGB());
        }

        GlStateManager.disableLighting();
    }

    public boolean drawXPBar(final ScaledResolution resolution) {
        final EntityPlayer player = MINECRAFT.player;

        if (this.update(SoulItemHelper.getFirstCapability(player, player.getHeldItemMainhand()))) {
            if (this.itemType == null) {
                final int slot = player.inventory.currentItem;
                SoulboundCapability capability;

                if ((capability = WeaponProvider.get(player)).getBoundSlot() == slot || (capability = ToolProvider.get(player)).getBoundSlot() == slot) {
                    this.capability = capability;
                    this.itemType = capability.getItemType();
                }
            }

            if (this.capability != null && this.itemType != null) {
                this.drawXPBar((resolution.getScaledWidth() - 182) / 2, resolution.getScaledHeight() - 29, 182);

                return true;
            }
        }

        return false;
    }

    public boolean update(final ItemStack itemStack) {
        if (this.update(SoulItemHelper.getFirstCapability(MINECRAFT.player, itemStack))) {
            final Item item = itemStack.getItem();

            if (this.itemStack != itemStack && item instanceof ISoulboundItem) {
                this.itemStack = itemStack;

                if (this.capability != null) {
                    this.itemType = this.capability.getItemType(itemStack);
                }

                return true;
            }

            return MINECRAFT.player.inventory.currentItem == this.capability.getBoundSlot();
        }

        return false;
    }

    public boolean update(final SoulboundCapability capability) {
        if (capability != null) {
            this.capability = capability;
            this.itemType = capability.getItemType();
        } else {
            this.itemType = null;
        }

        return this.capability != null;
    }
}
