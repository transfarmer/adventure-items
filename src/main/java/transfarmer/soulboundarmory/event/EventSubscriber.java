package transfarmer.soulboundarmory.event;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.client.gui.TooltipXPBar;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolDatum;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponDatum;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.client.tool.CToolData;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponData;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponDatum;
import transfarmer.util.ItemHelper;

import java.util.List;
import java.util.Random;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Configuration.levelupNotifications;
import static transfarmer.soulboundarmory.Configuration.multipliers;
import static transfarmer.soulboundarmory.Main.ResourceLocations.SOULBOUND_TOOL;
import static transfarmer.soulboundarmory.Main.ResourceLocations.SOULBOUND_WEAPON;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;
import static transfarmer.soulboundarmory.statistics.SoulDatum.LEVEL;
import static transfarmer.soulboundarmory.statistics.SoulDatum.XP;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.*;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EventSubscriber {
    @SubscribeEvent
    public static void onRegisterEntityEntry(final Register<EntityEntry> entry) {
        entry.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntitySoulDagger.class)
                .id(new ResourceLocation(Main.MOD_ID, "entity_soul_dagger"), 0)
                .name("soul dagger")
                .tracker(256, 1, true)
                .build()
        );
        entry.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityReachModifier.class)
                .id(new ResourceLocation(Main.MOD_ID, "entity_reach_extender"), 1)
                .name("reach extender")
                .tracker(16, 1, true)
                .build()
        );
    }

    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(SOULBOUND_WEAPON, new SoulWeaponProvider());
            event.addCapability(SOULBOUND_TOOL, new SoulToolProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerRespawnEvent event) {
        updatePlayer(event.player);

        ISoulCapability capability = SoulWeaponProvider.get(event.player);
        IType type = capability.getCurrentType();

        if (!event.player.world.getGameRules().getBoolean("keepInventory")) {
            if (type != null && capability.getDatum(SoulWeaponDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }

            capability = SoulToolProvider.get(event.player);
            type = capability.getCurrentType();

            if (type != null && capability.getDatum(SoulToolDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }
        }
    }

    private static void updatePlayer(final EntityPlayer player) {
        final ISoulWeapon weaponCapability = SoulWeaponProvider.get(player);
        final ISoulTool toolCapability = SoulToolProvider.get(player);

        Main.CHANNEL.sendTo(new CWeaponData(player,
                weaponCapability.getCurrentType(),
                weaponCapability.getCurrentTab(),
                weaponCapability.getAttackCooldown(),
                weaponCapability.getBoundSlot(),
                weaponCapability.getData(),
                weaponCapability.getAttributes(),
                weaponCapability.getEnchantments()), (EntityPlayerMP) player
        );
        Main.CHANNEL.sendTo(new CToolData(player,
                toolCapability.getCurrentType(),
                toolCapability.getCurrentTab(),
                toolCapability.getBoundSlot(),
                toolCapability.getData(),
                toolCapability.getAttributes(),
                toolCapability.getEnchantments()), (EntityPlayerMP) player
        );
    }

    @SubscribeEvent
    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (!player.world.getGameRules().getBoolean("keepInventory")) {
            ISoulCapability capability = SoulWeaponProvider.get(player);
            IType type = capability.getCurrentType();

            if (type != null && capability.getDatum(SoulDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.getDrops().removeIf((final EntityItem item) -> SoulWeaponHelper.isSoulWeapon(item.getItem()));
            }

            capability = SoulToolProvider.get(player);
            type = capability.getCurrentType();

            if (type != null && capability.getDatum(SoulToolDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.getDrops().removeIf((final EntityItem item) -> SoulToolHelper.isSoulTool(item.getItem()));
            }
        }
    }

    @SubscribeEvent
    public static void onClone(final Clone event) {
        ISoulCapability originalInstance = SoulWeaponProvider.get(event.getOriginal());
        ISoulCapability instance = SoulWeaponProvider.get(event.getEntityPlayer());

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setCurrentTab(originalInstance.getCurrentTab());
        instance.bindSlot(originalInstance.getBoundSlot());
        instance.setStatistics(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());

        originalInstance = SoulToolProvider.get(event.getOriginal());
        instance = SoulToolProvider.get(event.getEntityPlayer());

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setCurrentTab(originalInstance.getCurrentTab());
        instance.bindSlot(originalInstance.getBoundSlot());
        instance.setStatistics(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());
    }

    @SubscribeEvent
    public static void onLivingHurt(final LivingHurtEvent event) {
        if (event.getSource().getTrueSource() != null && !event.getSource().getTrueSource().world.isRemote) {
            final Entity trueSource = event.getSource().getTrueSource();
            final Entity source = event.getSource().getImmediateSource();
            final IType weaponType;

            if (trueSource instanceof EntityPlayer) {
                final ISoulWeapon instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getCurrentType();
                } else if (source instanceof EntitySoulLightningBolt) {
                    weaponType = SWORD;
                } else return;

                if (weaponType != null && instance.getAttribute(CRITICAL, weaponType) > new Random().nextInt(100)) {
                    event.setAmount(2 * event.getAmount());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(final LivingKnockBackEvent event) {
        if (!event.getAttacker().world.isRemote) {
            Entity attacker = event.getAttacker();
            IType weaponType = null;
            final ISoulWeapon instance = SoulWeaponProvider.get(attacker);

            if (attacker instanceof EntitySoulDagger) {
                attacker = ((EntitySoulDagger) attacker).shootingEntity;
                weaponType = DAGGER;
            } else if (attacker instanceof EntityPlayer) {
                weaponType = instance.getCurrentType();
            }

            if (attacker instanceof EntityPlayer && weaponType != null) {
                if (SoulWeaponHelper.isSoulWeaponEquipped((EntityPlayer) attacker)) {
                    event.setStrength(event.getStrength() * (1 + instance.getAttribute(KNOCKBACK_ATTRIBUTE, weaponType) / 6));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        if (event.getSource().getTrueSource() != null && !event.getSource().getTrueSource().world.isRemote) {
            final EntityLivingBase entity = event.getEntityLiving();
            final IAttributeInstance attackDamage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            final IAttributeInstance armor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
            final Entity trueSource = event.getSource().getTrueSource();
            final ISoulWeapon instance;
            final String displayName;
            final IType weaponType;
            Entity source = event.getSource().getImmediateSource();

            if (trueSource instanceof EntityPlayer) {
                instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                    displayName = ((EntitySoulDagger) source).itemStack.getDisplayName();
                    source = ((EntitySoulDagger) source).shootingEntity;
                } else {
                    if (source instanceof EntityPlayer) {
                        weaponType = instance.getCurrentType();
                    } else if (source instanceof EntitySoulLightningBolt) {
                        weaponType = SWORD;
                        source = ((EntitySoulLightningBolt) source).getCaster();
                    } else return;

                    displayName = ((EntityPlayerMP) source).getHeldItemMainhand().getDisplayName();
                }
            } else return;

            if (source instanceof EntityPlayer && weaponType != null) {
                //noinspection ConstantConditions
                if (attackDamage != null || Configuration.passiveXP || entity instanceof EntitySlime) {
                    double attackDamageValue = 0;

                    //noinspection ConstantConditions
                    if (attackDamage != null) {
                        attackDamageValue = attackDamage.getAttributeValue();
                    }

                    int xp = (int) Math.round(entity.getMaxHealth()
                            * source.world.getDifficulty().getId() * multipliers.difficultyMultiplier
                            * (1 + attackDamageValue * multipliers.attackDamageMultiplier)
                            * (1 + armor.getAttributeValue() * multipliers.armorMultiplier));

                    if (!entity.isNonBoss()) {
                        xp *= multipliers.bossMultiplier;
                    }

                    if (source.world.getWorldInfo().isHardcoreModeEnabled()) {
                        xp *= multipliers.hardcoreMultiplier;
                    }

                    if (entity instanceof EntityZombie && entity.isChild()) {
                        xp *= multipliers.babyZombieMultiplier;
                    }

                    if (instance.addDatum(xp, XP, weaponType) && levelupNotifications) {
                        source.sendMessage(new TextComponentString(String.format("Your %s leveled up to level %d.",
                                displayName, instance.getDatum(LEVEL, weaponType))));
                    }

                    Main.CHANNEL.sendTo(new CWeaponDatum(xp, XP, weaponType), (EntityPlayerMP) source);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityItemPickup(final EntityItemPickupEvent event) {
        event.setResult(ALLOW);

        SoulItemHelper.addItemStack(event.getItem().getItem(), event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void onHarvestDrops(final HarvestDropsEvent event) {
        if (event.getHarvester() != null) {
            final EntityPlayer player = event.getHarvester();
            final Item item = player.getHeldItemMainhand().getItem();

            if (item instanceof IItemSoulTool) {
                if (((IItemSoulTool) item).canHarvestBlock(event.getState(), player)) {
                    event.setDropChance(1);
                } else if (((IItemSoulTool) item).isEffectiveAgainst(event.getState())) {
                    event.setDropChance(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(final BreakSpeed event) {
        if (event.getEntityPlayer().getHeldItemMainhand().getItem() instanceof ISoulItem) {
            final ISoulItem item = (ISoulItem) event.getEntityPlayer().getHeldItemMainhand().getItem();
            final ISoulCapability capability = SoulItemHelper.getCapability(event.getEntityPlayer(), (Item) item);
            final IType type = capability.getCurrentType();

            if (item instanceof IItemSoulTool) {
                if (((IItemSoulTool) item).isEffectiveAgainst(event.getState())) {
                    float newSpeed = event.getOriginalSpeed() + capability.getAttribute(EFFICIENCY_ATTRIBUTE, type);
                    final int efficiency = capability.getEnchantment(SoulToolEnchantment.SOUL_EFFICIENCY_ENCHANTMENT, type);
                    final PotionEffect haste = event.getEntityPlayer().getActivePotionEffect(Potion.getPotionFromResourceLocation("haste"));

                    if (efficiency > 0) {
                        newSpeed += 1 + efficiency * efficiency;
                    }

                    if (haste != null) {
                        newSpeed *= haste.getAmplifier() * 0.1;
                    }

                    if (((IItemSoulTool) item).canHarvestBlock(event.getState(), event.getEntityPlayer())) {
                        event.setNewSpeed(newSpeed);
                    } else {
                        event.setNewSpeed(newSpeed / 4F);
                    }
                } else {
                    event.setNewSpeed((event.getOriginalSpeed() - 1 + capability.getAttribute(EFFICIENCY_ATTRIBUTE, type)) / 8);
                }
            } else if (item instanceof ItemSoulWeapon) {
                final float breakSpeed = capability.getAttribute(EFFICIENCY, capability.getCurrentType());

                event.setNewSpeed(event.getState().getMaterial() == Material.WEB
                        ? Math.max(15, breakSpeed)
                        : breakSpeed
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase != END) return;

        ISoulWeapon weaponCapability = SoulWeaponProvider.get(event.player);

        if (SoulToolHelper.hasSoulTool(event.player)) {
            ISoulCapability instance = SoulToolProvider.get(event.player);
            InventoryPlayer inventory = event.player.inventory;

            if (SoulToolHelper.isSoulToolEquipped(event.player)) {
                final SoulToolType type = SoulToolType.getType(inventory.getCurrentItem().getItem());

                if (type != instance.getCurrentType()) {
                    instance.setCurrentType(type);
                }
            }

            if (instance.getCurrentType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : inventory.mainInventory) {
                    if (SoulToolHelper.isSoulTool(itemStack)) {
                        final int index = inventory.mainInventory.indexOf(itemStack);

                        if (itemStack.getItem() == instance.getCurrentType().getItem() && firstSlot == -1) {
                            firstSlot = index;

                            if (instance.getBoundSlot() != -1) {
                                instance.bindSlot(index);
                            }

                            continue;
                        }

                        if (!event.player.isCreative() && index != firstSlot) {
                            inventory.deleteStack(itemStack);
                        }
                    }
                }
            }

            for (final ItemStack itemStack : inventory.mainInventory) {
                if (!SoulToolHelper.isSoulTool(itemStack)) continue;

                final ItemStack newItemStack = instance.getItemStack(itemStack);

                if (!SoulToolHelper.areDataEqual(itemStack, newItemStack)) {
                    if (itemStack.hasDisplayName()) {
                        newItemStack.setStackDisplayName(itemStack.getDisplayName());
                    }

                    inventory.setInventorySlotContents(inventory.mainInventory.indexOf(itemStack), newItemStack);
                }
            }
        }

        if (SoulWeaponHelper.hasSoulWeapon(event.player)) {
            InventoryPlayer inventory = event.player.inventory;

            if (SoulWeaponHelper.isSoulWeaponEquipped(event.player)) {
                final IType type = SoulWeaponType.getType(inventory.getCurrentItem().getItem());

                if (type != weaponCapability.getCurrentType()) {
                    weaponCapability.setCurrentType(type);
                }

                if (weaponCapability.getAttackCooldown() > 0) {
                    weaponCapability.addCooldown(-1);
                }
            }

            if (weaponCapability.getCurrentType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : inventory.mainInventory) {
                    if (SoulWeaponHelper.isSoulWeapon(itemStack)) {
                        final int index = inventory.mainInventory.indexOf(itemStack);

                        if (itemStack.getItem() == weaponCapability.getCurrentType().getItem() && firstSlot == -1) {
                            firstSlot = index;

                            if (weaponCapability.getBoundSlot() != -1) {
                                weaponCapability.bindSlot(index);
                            }

                            continue;
                        }

                        if (!event.player.isCreative() && index != firstSlot) {
                            inventory.deleteStack(itemStack);
                        }
                    }
                }
            }

            for (final ItemStack itemStack : inventory.mainInventory) {
                if (!SoulWeaponHelper.isSoulWeapon(itemStack)) continue;

                final ItemStack newItemStack = weaponCapability.getItemStack(itemStack);

                if (!SoulWeaponHelper.areDataEqual(itemStack, newItemStack)) {
                    if (itemStack.hasDisplayName()) {
                        newItemStack.setStackDisplayName(itemStack.getDisplayName());
                    }

                    inventory.setInventorySlotContents(inventory.mainInventory.indexOf(itemStack), newItemStack);
                }
            }
        }

        if (weaponCapability.getCurrentType() != null && weaponCapability.getLightningCooldown() > 0) {
            weaponCapability.decrementLightningCooldown();
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
                    minecraft.displayGuiScreen(new SoulWeaponMenu(0));
                } else if (ItemHelper.isItemEquipped(Items.WOODEN_PICKAXE, player)) {
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
            final ItemStack itemStack = event.getItemStack();

            if (SoulItemHelper.isSoulItem(itemStack)) {
                final ISoulCapability capability = SoulItemHelper.getCapability(player, itemStack.getItem());
                final IType type = capability.getType(itemStack);
                final List<String> tooltip = event.getToolTip();
                final int startIndex = tooltip.indexOf(I18n.format("item.modifiers.mainhand")) + 1;

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    final String[] prior = tooltip.subList(0, startIndex).toArray(new String[0]);
                    final String[] posterior = tooltip.subList(startIndex + itemStack.getAttributeModifiers(MAINHAND).size(), tooltip.size()).toArray(new String[0]);

                    tooltip.clear();
                    tooltip.addAll(Arrays.asList(prior));
                    tooltip.addAll(capability.getTooltip(type));
                    tooltip.addAll(Arrays.asList(posterior));
                });
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        if (event.getStack().getItem() instanceof ISoulItem) {
            new TooltipXPBar(event.getX(), event.getY(), event.getStack());
        }
    }
}
