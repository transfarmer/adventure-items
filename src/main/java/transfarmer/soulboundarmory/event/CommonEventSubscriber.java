package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.client.gui.SoulToolTooltipXPBar;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.client.gui.SoulWeaponTooltipXPBar;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.util.ItemHelper;

import java.util.List;

import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.ResourceLocations.SOULBOUND_TOOL;
import static transfarmer.soulboundarmory.ResourceLocations.SOULBOUND_WEAPON;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.GREATSWORD;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MOD_ID)
public class CommonEventSubscriber {
    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(SOULBOUND_WEAPON, new SoulWeaponProvider());
            event.addCapability(SOULBOUND_TOOL, new SoulToolProvider());
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            if (MENU_KEY.isPressed()) {
                final EntityPlayer player = minecraft.player;

                if (SoulWeaponHelper.isSoulWeaponEquipped(player)) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu());
                } else if (SoulToolHelper.isSoulToolEquipped(player)) {
                    minecraft.displayGuiScreen(new SoulToolMenu());
                } else if (ItemHelper.isItemEquipped(Items.WOODEN_SWORD, player)) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu(-1));
                } else if (ItemHelper.isItemEquipped(Items.WOODEN_PICKAXE, player)){
                    minecraft.displayGuiScreen(new SoulToolMenu(-1));
                }
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            try {
                if (event.getItemStack().getItem() instanceof ItemSoulWeapon) {
                    final ISoulWeapon capability = SoulWeaponProvider.get(player);

                    final SoulWeaponType weaponType = SoulWeaponType.getType(event.getItemStack());
                    final List<String> tooltip = event.getToolTip();
                    final String[] newTooltip = capability.getTooltip(weaponType);
                    final int enchantments = event.getItemStack().getEnchantmentTagList().tagCount();

                    if (weaponType == GREATSWORD || weaponType == SWORD) {
                        tooltip.remove(5 + enchantments);
                    }

                    tooltip.remove(4 + enchantments);
                    tooltip.remove(3 + enchantments);

                    for (int i = 0; i < newTooltip.length; i++) {
                        tooltip.add(3 + enchantments + i, newTooltip[i]);
                    }
                } else if (event.getItemStack().getItem() instanceof IItemSoulTool) {
                    final ISoulTool capability = SoulToolProvider.get(player);

                    if (event.getItemStack().getItem() instanceof IItemSoulTool) {
                        final SoulToolType weaponType = SoulToolType.getType(event.getItemStack());
                        final List<String> tooltip = event.getToolTip();
                        final String[] newTooltip = capability.getTooltip(weaponType);
                        final int enchantments = event.getItemStack().getEnchantmentTagList().tagCount();

                        tooltip.remove(5 + enchantments);
                        tooltip.remove(4 + enchantments);
                        tooltip.remove(3 + enchantments);

                        for (int i = 0; i < newTooltip.length; i++) {
                            tooltip.add(3 + enchantments + i, newTooltip[i]);
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace();
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        final SoulWeaponType tooltipWeapon = SoulWeaponType.getType(event.getStack());

        if (tooltipWeapon != null) {
            new SoulWeaponTooltipXPBar(tooltipWeapon, event.getX(), event.getY(), event.getStack().getEnchantmentTagList().tagCount());
        } else {
            final SoulToolType tooltipTool = SoulToolType.getType(event.getStack());

            if (tooltipTool != null) {
                new SoulToolTooltipXPBar(tooltipTool, event.getX(), event.getY(), event.getStack().getEnchantmentTagList().tagCount());
            }
        }
    }
}
