package transfarmer.soulweapons.event;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Configuration;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponHelper;
import transfarmer.soulweapons.capability.SoulWeaponProvider;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.entity.EntitySoulDagger;
import transfarmer.soulweapons.gui.SoulWeaponMenu;
import transfarmer.soulweapons.gui.TooltipXPBar;
import transfarmer.soulweapons.network.ClientWeaponData;
import transfarmer.soulweapons.network.ClientWeaponXP;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.Configuration.levelupNotifications;
import static transfarmer.soulweapons.Configuration.multipliers;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.client.KeyBindings.WEAPON_MENU;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.EFFICIENCY;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulweapons.data.SoulWeaponDatum.LEVEL;
import static transfarmer.soulweapons.data.SoulWeaponDatum.XP;

@EventBusSubscriber(modid = Main.MODID)
public class ForgeEventSubscriber {
    public static SoulWeaponType tooltipWeapon = null;

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onRegisterEntityEntry(RegistryEvent.Register<EntityEntry> entry) {
        entry.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntitySoulDagger.class)
            .id(new ResourceLocation(Main.MODID, "entitySoulDagger"), 0)
            .name("soul dagger")
            .tracker(128, 1, true)
            .build()
        );
    }

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

        final ISoulWeapon capability = event.player.getCapability(CAPABILITY, null);

        if (capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
            && !event.player.world.getGameRules().getBoolean("keepInventory")) {
            event.player.addItemStackToInventory(capability.getItemStack(capability.getCurrentType()));
        }
    }

    private static void updatePlayer(EntityPlayer player) {
        ISoulWeapon capability = player.getCapability(CAPABILITY, null);
        Main.CHANNEL.sendTo(new ClientWeaponData(capability.getCurrentType(), capability.getCurrentTab(),
            capability.getData(), capability.getAttributes(), capability.getEnchantments()), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void onPlayerDrops(PlayerDropsEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ISoulWeapon capability = player.getCapability(CAPABILITY, null);

        if (capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
            && !player.world.getGameRules().getBoolean("keepInventory")) {

            event.getDrops().removeIf((EntityItem item) -> SoulWeaponType.isSoulWeapon(item.getItem()));
        }
    }

    @SubscribeEvent
    public static void onClone(Clone event) {
        ISoulWeapon originalInstance = event.getOriginal().getCapability(CAPABILITY, null);
        ISoulWeapon instance = event.getEntityPlayer().getCapability(CAPABILITY, null);

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.set(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (!SoulWeaponHelper.hasSoulWeapon(event.player) || event.phase != END) return;

        ISoulWeapon instance = event.player.getCapability(CAPABILITY, null);
        InventoryPlayer inventory = event.player.inventory;

        if (SoulWeaponHelper.isSoulWeaponEquipped(event.player)) {
            final SoulWeaponType heldItemType = SoulWeaponType.getType(inventory.getCurrentItem().getItem());

            if (heldItemType != instance.getCurrentType()) {
                instance.setCurrentType(heldItemType);
            }
        }

        if (instance.getCurrentType() != null) {
            if (!event.player.isCreative()) {
                for (final Item item : SoulWeaponType.getItems()) {
                    if (item != instance.getCurrentType().getItem()) {
                        inventory.clearMatchingItems(item, 0, inventory.getSizeInventory(), null);
                    }
                }
            }

            for (final ItemStack itemStack : inventory.mainInventory) {
                if (!SoulWeaponType.isSoulWeapon(itemStack)) continue;

                ItemStack newItemStack = instance.getItemStack(itemStack);

                if (SoulWeaponHelper.areDataEqual(itemStack, newItemStack)) continue;
                if (itemStack.hasDisplayName()) {
                    newItemStack.setStackDisplayName(itemStack.getDisplayName());
                }

                inventory.setInventorySlotContents(inventory.mainInventory.indexOf(itemStack), newItemStack);
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(BreakSpeed event) {
        final ISoulWeapon capability = event.getEntityPlayer().getCapability(CAPABILITY, null);

        if (SoulWeaponHelper.isSoulWeaponEquipped(event.getEntityPlayer())) {
            final float breakSpeed = capability.getAttribute(EFFICIENCY, capability.getCurrentType());
            event.setNewSpeed(event.getState().getMaterial() == Material.WEB ?
                Math.max(15, breakSpeed) : breakSpeed
            );
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        final EntityLivingBase entity = event.getEntityLiving();
        final DamageSource source = event.getSource();
        final Entity trueSource = event.getSource().getTrueSource();

        if (!entity.getEntityWorld().isRemote && trueSource instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) trueSource;

            if (source.getImmediateSource() instanceof EntitySoulDagger) {
                ((EntityPlayer) trueSource).attackTargetEntityWithCurrentItem(entity);
                event.setCanceled(true);
                return;
            }

            final ISoulWeapon instance = trueSource.getCapability(CAPABILITY, null);

            if (SoulWeaponHelper.isSoulWeaponEquipped((EntityPlayer) trueSource) && instance.getAttribute(CRITICAL, instance.getCurrentType()) > new Random().nextInt(100)) {
                event.setAmount(2 * event.getAmount());
            }
        }
    }


    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        Entity attacker = event.getOriginalAttacker();

        if (!attacker.getEntityWorld().isRemote && (attacker instanceof EntityPlayer)) {
            if (SoulWeaponHelper.isSoulWeaponEquipped((EntityPlayer) attacker)) {
                ISoulWeapon instance = attacker.getCapability(CAPABILITY, null);
                event.setStrength(event.getStrength() * (1 + instance.getAttribute(KNOCKBACK_ATTRIBUTE, instance.getCurrentType()) / 8F));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        final EntityLivingBase entity = event.getEntityLiving();
        final Entity source = event.getSource().getTrueSource();
        final IAttributeInstance attackDamage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        final IAttributeInstance armor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);

        //noinspection ConstantConditions
        if (!entity.getEntityWorld().isRemote && attackDamage instanceof IAttributeInstance && source instanceof EntityPlayer) {
            if (attackDamage.getAttributeValue() > 0 && SoulWeaponHelper.isSoulWeaponEquipped((EntityPlayer) source)) {
                final ISoulWeapon instance = source.getCapability(CAPABILITY, null);

                int xp = (int) Math.round(entity.getMaxHealth()
                    * source.world.getDifficulty().getId() * multipliers.difficultyMultiplier
                    * (1 + attackDamage.getAttributeValue() * multipliers.attackDamageMultiplier)
                    * (1 + armor.getAttributeValue() * multipliers.armorMultiplier));

                if (!entity.isNonBoss()) {
                    xp *= multipliers.bossMultiplier;
                } if (source.world.getWorldInfo().isHardcoreModeEnabled()) {
                    xp *= multipliers.hardcoreMultiplier;
                }

                if (instance.addDatum(xp, XP, instance.getCurrentType()) && levelupNotifications) {
                    source.sendMessage(new TextComponentString(String.format("Your %s leveled up to level %d.",
                        ((EntityPlayerMP) source).getHeldItemMainhand().getDisplayName(), instance.getDatum(LEVEL, instance.getCurrentType()))));
                }

                Main.CHANNEL.sendTo(new ClientWeaponXP(xp), (EntityPlayerMP) source);
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {

    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (WEAPON_MENU.isKeyDown() && event.phase == END) {
            final EntityPlayer player = Minecraft.getMinecraft().player;

            if (player.getHeldItemMainhand().getItem().equals(Items.WOODEN_SWORD)) {
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu(0));
            } else if (SoulWeaponHelper.isSoulWeaponEquipped(player)) {
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu());
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player == null) return;

        final ISoulWeapon instance = player.getCapability(CAPABILITY, null);

        if (instance.getCurrentType() == null || !SoulWeaponType.getItems().contains(event.getItemStack().getItem())) return;

        tooltipWeapon = SoulWeaponType.getType(event.getItemStack());

        final List<String> tooltip = event.getToolTip();
        final String[] newTooltip = instance.getTooltip(tooltipWeapon);
        final int enchantments = event.getItemStack().getEnchantmentTagList().tagCount();

        tooltip.remove(4 + enchantments);
        tooltip.remove(3 + enchantments);

        for (int i = 0; i < newTooltip.length; i++) {
            tooltip.add(3 + enchantments + i, newTooltip[i]);
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onDrawScreen(RenderTooltipEvent.PostText event) {
        if (tooltipWeapon != null) {
            new TooltipXPBar(tooltipWeapon, event.getX(), event.getY(), event.getStack().getEnchantmentTagList().tagCount());
            tooltipWeapon = null;
        }
    }
}
