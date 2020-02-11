package transfarmer.soulweapons.event;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
import transfarmer.soulweapons.entity.EntityReachModifier;
import transfarmer.soulweapons.entity.EntitySoulDagger;
import transfarmer.soulweapons.gui.SoulWeaponMenu;
import transfarmer.soulweapons.gui.TooltipXPBar;
import transfarmer.soulweapons.network.client.CWeaponData;
import transfarmer.soulweapons.network.client.CWeaponDatum;
import transfarmer.util.Item;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
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
import static transfarmer.soulweapons.data.SoulWeaponType.DAGGER;
import static transfarmer.soulweapons.data.SoulWeaponType.GREATSWORD;
import static transfarmer.soulweapons.data.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MODID)
public class ForgeEventSubscriber {
    @SubscribeEvent
    public static void onRegisterEntityEntry(final RegistryEvent.Register<EntityEntry> entry) {
        entry.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntitySoulDagger.class)
            .id(new ResourceLocation(Main.MODID, "entity_soul_dagger"), 0)
            .name("soul dagger")
            .tracker(128, 1, true)
            .build()
        );
        entry.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntityReachModifier.class)
            .id(new ResourceLocation(Main.MODID, "entity_reach_extender"), 1)
            .name("reach extender")
            .tracker(128, 1, true)
            .build()
        );
    }

    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Main.MODID, "soulweapon"), new SoulWeaponProvider());
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

        final ISoulWeapon capability = event.player.getCapability(CAPABILITY, null);

        if (capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
            && !event.player.world.getGameRules().getBoolean("keepInventory")) {
            event.player.addItemStackToInventory(capability.getItemStack(capability.getCurrentType()));
        }
    }

    private static void updatePlayer(final EntityPlayer player) {
        final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
        Main.CHANNEL.sendTo(new CWeaponData(
            capability.getCurrentType(),
            capability.getCurrentTab(),
            capability.getCooldown(),
            capability.getBoundSlot(),
            capability.getData(),
            capability.getAttributes(),
            capability.getEnchantments()), (EntityPlayerMP) player
        );
    }

    @SubscribeEvent
    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ISoulWeapon capability = player.getCapability(CAPABILITY, null);

        if (capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
            && !player.world.getGameRules().getBoolean("keepInventory")) {

            event.getDrops().removeIf((EntityItem item) -> SoulWeaponHelper.isSoulWeapon(item.getItem()));
        }
    }

    @SubscribeEvent
    public static void onClone(final Clone event) {
        final ISoulWeapon originalInstance = event.getOriginal().getCapability(CAPABILITY, null);
        final ISoulWeapon instance = event.getEntityPlayer().getCapability(CAPABILITY, null);

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setCurrentTab(originalInstance.getCurrentTab());
        instance.setBoundSlot(originalInstance.getBoundSlot());
        instance.set(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase != END) return;

        if (SoulWeaponHelper.hasSoulWeapon(event.player)) {
            ISoulWeapon instance = event.player.getCapability(CAPABILITY, null);
            InventoryPlayer inventory = event.player.inventory;

            if (SoulWeaponHelper.isSoulWeaponEquipped(event.player)) {
                final SoulWeaponType heldItemType = SoulWeaponType.getType(inventory.getCurrentItem().getItem());

                if (heldItemType != instance.getCurrentType()) {
                    instance.setCurrentType(heldItemType);
                }

                if (instance.getCooldown() > 0) {
                    instance.addCooldown(-1);
                }
            }

            if (instance.getCurrentType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : inventory.mainInventory) {
                    if (SoulWeaponHelper.isSoulWeapon(itemStack)) {
                        final int index = inventory.mainInventory.indexOf(itemStack);

                        if (!event.player.isCreative()) {
                            if (itemStack.getItem() != instance.getCurrentType().getItem()) {
                                inventory.deleteStack(itemStack);
                            } else {
                                if (firstSlot == -1) {
                                    firstSlot = index;
                                    instance.setBoundSlot(index);
                                } else if (firstSlot != index) {
                                    inventory.deleteStack(itemStack);
                                }
                            }
                        }
                    }
                }
            }

            for (final ItemStack itemStack : inventory.mainInventory) {
                if (!SoulWeaponHelper.isSoulWeapon(itemStack)) continue;

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
    public static void onEntityItemPickup(final EntityItemPickupEvent event) {
        event.setPhase(EventPriority.LOWEST);
        event.setResult(ALLOW);
        final boolean isSoulWeapon = SoulWeaponHelper.isSoulWeapon(event.getItem().getItem());
        final EntityPlayer player = event.getEntityPlayer();
        final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
        final NonNullList<ItemStack> inventory = player.inventory.mainInventory;
        final ItemStack itemStack = event.getItem().getItem();

        if (capability.getBoundSlot() < 0) {
            player.addItemStackToInventory(itemStack);
        } else if (!isSoulWeapon) {
            for (int slot = 0; slot < inventory.size(); slot++) {
                if (slot != capability.getBoundSlot() && addItemStack(slot, itemStack, player)) {
                    return;
                }
            }
        } else {
            if (!addItemStack(capability.getBoundSlot(), itemStack, player)) {
                player.addItemStackToInventory(itemStack);
            }
        }
    }

    private static boolean addItemStack(final int slot, final ItemStack itemStack, final EntityPlayer player) {
        final InventoryPlayer inventory = player.inventory;
        final ItemStack slotStack = inventory.mainInventory.get(slot);

        return (slotStack.getItem() == Items.AIR || slotStack.getItem() == itemStack.getItem() && !itemStack.isItemStackDamageable())
            && player.inventory.add(slot, itemStack);
    }

    @SubscribeEvent
    public static void onBreakSpeed(final BreakSpeed event) {
        final ISoulWeapon capability = event.getEntityPlayer().getCapability(CAPABILITY, null);

        if (SoulWeaponHelper.isSoulWeaponEquipped(event.getEntityPlayer())) {
            final float breakSpeed = capability.getAttribute(EFFICIENCY, capability.getCurrentType());
            event.setNewSpeed(event.getState().getMaterial() == Material.WEB ?
                Math.max(15, breakSpeed) : breakSpeed
            );
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(final LivingHurtEvent event) {
        if (event.getSource().getTrueSource() != null && !event.getSource().getTrueSource().world.isRemote) {
            final Entity trueSource = event.getSource().getTrueSource();
            final Entity source = event.getSource().getImmediateSource();
            final ISoulWeapon instance;
            final SoulWeaponType weaponType;

            if (trueSource instanceof EntityPlayer) {
                instance = trueSource.getCapability(CAPABILITY, null);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getCurrentType();
                } else return;
            } else return;

            if (weaponType != null && instance.getAttribute(CRITICAL, weaponType) > new Random().nextInt(100)) {
                event.setAmount(2 * event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(final LivingKnockBackEvent event) {
        if (!event.getAttacker().world.isRemote) {
            Entity attacker = event.getAttacker();
            SoulWeaponType weaponType = null;
            ISoulWeapon instance = attacker.getCapability(CAPABILITY, null);

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
            final SoulWeaponType weaponType;
            Entity source = event.getSource().getImmediateSource();

            if (trueSource instanceof EntityPlayer) {
                instance = trueSource.getCapability(CAPABILITY, null);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                    displayName = ((EntitySoulDagger) source).itemStack.getDisplayName();
                    source = ((EntitySoulDagger) source).shootingEntity;
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getCurrentType();
                    displayName = ((EntityPlayerMP) source).getHeldItemMainhand().getDisplayName();
                } else return;
            } else return;

            if (source instanceof EntityPlayer && weaponType != null) {
                double attackDamageValue = 0;

                //noinspection ConstantConditions
                if (attackDamage != null) {
                    attackDamageValue = attackDamage.getAttributeValue();
                }

                //noinspection ConstantConditions
                if (attackDamage != null || (Configuration.passiveXP || entity instanceof EntitySlime)) {
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

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END && WEAPON_MENU.isKeyDown()) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final EntityPlayer player = minecraft.player;
            final ISoulWeapon capability = player.getCapability(CAPABILITY, null);

            if (capability.getCurrentType() != null) {
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            } else if (Item.hasWoodenSword(player)) {
                minecraft.displayGuiScreen(new SoulWeaponMenu(0));
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            final ISoulWeapon instance = player.getCapability(CAPABILITY, null);

            if (SoulWeaponType.getItems().contains(event.getItemStack().getItem())) {
                final SoulWeaponType weaponType = SoulWeaponType.getType(event.getItemStack());
                final List<String> tooltip = event.getToolTip();
                final String[] newTooltip = instance.getTooltip(weaponType);
                final int enchantments = event.getItemStack().getEnchantmentTagList().tagCount();

               if (weaponType == GREATSWORD || weaponType == SWORD) {
                   tooltip.remove(5 + enchantments);
               }

                tooltip.remove(4 + enchantments);
                tooltip.remove(3 + enchantments);

                for (int i = 0; i < newTooltip.length; i++) {
                    tooltip.add(3 + enchantments + i, newTooltip[i]);
                }
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        final SoulWeaponType tooltipWeapon = SoulWeaponType.getType(event.getStack());

        if (tooltipWeapon != null) {
            new TooltipXPBar(tooltipWeapon, event.getX(), event.getY(), event.getStack().getEnchantmentTagList().tagCount());
        }
    }
}
