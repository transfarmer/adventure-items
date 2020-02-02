package transfarmer.soulweapons.event;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Configuration;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponProvider;
import transfarmer.soulweapons.gui.SoulWeaponMenu;
import transfarmer.soulweapons.gui.TooltipXPBar;
import transfarmer.soulweapons.network.ClientWeaponData;
import transfarmer.soulweapons.network.ClientWeaponXP;
import transfarmer.soulweapons.weapon.SoulAttributeModifier;
import transfarmer.soulweapons.weapon.SoulWeaponType;

import java.util.List;
import java.util.Random;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.Configuration.levelupNotifications;
import static transfarmer.soulweapons.Configuration.levelupPointNotifications;
import static transfarmer.soulweapons.Configuration.multipliers;
import static transfarmer.soulweapons.Configuration.onlyPoints;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.client.KeyBindings.WEAPON_MENU;
import static transfarmer.soulweapons.weapon.SoulWeaponType.NONE;

@EventBusSubscriber(modid = Main.MODID)
public class ForgeEventSubscriber {
    public static SoulWeaponType tooltipWeapon = NONE;

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Main.MODID, "soulweapon"), new SoulWeaponProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        updatePlayer(event.player);

        ISoulWeapon instance = event.player.getCapability(CAPABILITY, null);

        if (instance.getCurrentType() != NONE && instance.getLevel() >= Configuration.preservationLevel
            && !event.player.world.getGameRules().getBoolean("keepInventory")) {
            event.player.addItemStackToInventory(instance.getItemStack());
        }
    }

    private static void updatePlayer(EntityPlayer player) {
        ISoulWeapon instance = player.getCapability(CAPABILITY, null);
        Main.CHANNEL.sendTo(new ClientWeaponData(instance.getCurrentType(), instance.getData(), instance.getAttributes()),
            (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void onClone(Clone event) {
        ISoulWeapon originalInstance = event.getOriginal().getCapability(CAPABILITY, null);
        ISoulWeapon instance = event.getEntityPlayer().getCapability(CAPABILITY, null);

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setAttributes(originalInstance.getAttributes());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (!ISoulWeapon.hasSoulWeapon(event.player) || event.phase != END) return;

        ISoulWeapon instance = event.player.getCapability(CAPABILITY, null);
        InventoryPlayer inventory = event.player.inventory;

        if (ISoulWeapon.isSoulWeaponEquipped(event.player)) {
            final SoulWeaponType heldItemType = SoulWeaponType.getType(inventory.getCurrentItem().getItem());

            if (heldItemType != instance.getCurrentType()) {
                instance.setCurrentType(heldItemType);
            }
        }

        if (instance.getCurrentType() != NONE) {
            if (!event.player.isCreative()) {
                for (final Item item : SoulWeaponType.getItems()) {
                    if (item != instance.getItem()) {
                        inventory.clearMatchingItems(item, 0, inventory.getSizeInventory(), null);
                    }
                }
            }

            for (final ItemStack itemStack : inventory.mainInventory) {
                if (!SoulWeaponType.isSoulWeapon(itemStack)) return;

                ItemStack newItemStack = instance.getItemStack(itemStack);

                if (SoulAttributeModifier.areAttributesEqual(itemStack, newItemStack, MAINHAND)) continue;
                if (itemStack.hasDisplayName()) {
                    newItemStack.setStackDisplayName(itemStack.getDisplayName());
                }

                inventory.setInventorySlotContents(inventory.mainInventory.indexOf(itemStack), newItemStack);
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(BreakSpeed event) {
        if (ISoulWeapon.isSoulWeaponEquipped(event.getEntityPlayer())) {
            event.setNewSpeed(event.getState().getMaterial() == Material.WEB ?
                15 : event.getEntityPlayer().getCapability(CAPABILITY, null).getEfficiency());
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        Entity source = event.getSource().getTrueSource();

        if (entity.getEntityWorld().isRemote || !(source instanceof EntityPlayer)) return;

        ISoulWeapon instance = source.getCapability(CAPABILITY, null);

        if (!ISoulWeapon.isSoulWeaponEquipped((EntityPlayer) source)) return;
        if (new Random().nextInt(100) >= instance.getCritical()) return;

        event.setAmount(2 * event.getAmount());
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        Entity attacker = event.getOriginalAttacker();

        if (!attacker.getEntityWorld().isRemote && (attacker instanceof EntityPlayer)) {
            if (ISoulWeapon.isSoulWeaponEquipped((EntityPlayer) attacker)) {
                event.setStrength(event.getStrength() * (1 + attacker.getCapability(CAPABILITY, null).getKnockback() / 8F));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        Entity source = event.getSource().getTrueSource();
        IAttributeInstance attackDamage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        IAttributeInstance armor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);

        if (entity.getEntityWorld().isRemote || attackDamage == null || !(source instanceof EntityPlayer)) return;
        if (attackDamage.getAttributeValue() == 0) return;

        ISoulWeapon instance = source.getCapability(CAPABILITY, null);

        if (!ISoulWeapon.isSoulWeaponEquipped((EntityPlayer) source)) return;

        int xp = (int) Math.round(entity.getMaxHealth()
            * source.world.getDifficulty().getId() * multipliers.difficultyMultiplier
            * (1 + attackDamage.getAttributeValue() * multipliers.attackDamageMultiplier)
            * (1 + armor.getAttributeValue() * multipliers.armorMultiplier));

        if (!entity.isNonBoss()) {
            xp *= 2;
        }

        Main.CHANNEL.sendTo(new ClientWeaponXP(xp), (EntityPlayerMP) source);

        if (instance.addXP(xp)) {
            if (levelupNotifications) {
                source.sendMessage(new TextComponentString(String.format("Your %s leveled up to level %d.",
                    ((EntityPlayerMP) source).getHeldItemMainhand().getDisplayName(), instance.getLevel())));
            }

            if (levelupPointNotifications && (instance.getLevel() % 10 == 1 || onlyPoints)) {
                source.sendMessage(new TextComponentString("You earned an attribute point."));
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (WEAPON_MENU.isKeyDown() && event.phase == END) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            if (player.getHeldItemMainhand().getItem().equals(Items.WOODEN_SWORD) || ISoulWeapon.isSoulWeaponEquipped(player)) {
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu());
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        EntityPlayer player = event.getEntityPlayer();

        if (player == null) return;

        ISoulWeapon instance = player.getCapability(CAPABILITY, null);

        if (instance.getCurrentType() == NONE || !SoulWeaponType.getItems().contains(event.getItemStack().getItem())) return;

        tooltipWeapon = SoulWeaponType.getType(event.getItemStack());

        List<String> tooltip = event.getToolTip();
        String[] newTooltip = instance.getTooltip(event.getItemStack());
        tooltip.remove(4);
        tooltip.remove(3);

        for (int i = 0; i < newTooltip.length; i++) {
            tooltip.add(3 + i, newTooltip[i]);
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onDrawScreen(RenderTooltipEvent.PostText event) {
        if (tooltipWeapon == NONE) return;

        new TooltipXPBar(tooltipWeapon, event.getX(), event.getY());

        tooltipWeapon = NONE;
    }
}
