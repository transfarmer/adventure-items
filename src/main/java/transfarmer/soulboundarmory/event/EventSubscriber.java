package transfarmer.soulboundarmory.event;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.config.IPlayerConfig;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.client.gui.TooltipXPBar;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulDagger;
import transfarmer.soulboundarmory.item.ItemSoulPick;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.client.S2CLevelupMessage;
import transfarmer.soulboundarmory.network.server.C2SConfig;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.util.ItemHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.LOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_SWORD;
import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulToolDatum.TOOL_DATA;
import static transfarmer.soulboundarmory.statistics.SoulType.PICK;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EventSubscriber {
    private static final Map<Runnable, Integer> scheduledTasks = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        final IFrozen frozenCapability = FrozenProvider.get(entity);
        final IPlayerConfig config = PlayerConfigProvider.get(entity);
        final ISoulCapability toolCapability = SoulToolProvider.get(entity);
        final ISoulCapability weaponCapability = SoulWeaponProvider.get(entity);

        if (frozenCapability != null && frozenCapability.getEntity() == null) {
            frozenCapability.setEntity(entity);
        }

        if (toolCapability != null && toolCapability.getPlayer() == null) {
            toolCapability.initPlayer((EntityPlayer) entity);
        }

        if (weaponCapability != null && weaponCapability.getPlayer() == null) {
            weaponCapability.initPlayer((EntityPlayer) entity);
        }

        if (config != null && event.getWorld().isRemote) {
            Main.CHANNEL.sendToServer(new C2SConfig());
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(final LivingHurtEvent event) {
        if (event.getSource().getTrueSource() != null && !event.getSource().getTrueSource().world.isRemote) {
            final Entity trueSource = event.getSource().getTrueSource();
            final Entity source = event.getSource().getImmediateSource();
            final SoulType weaponType;

            if (trueSource instanceof EntityPlayer) {
                final ISoulCapability instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getCurrentType();
                } else if (source instanceof EntitySoulLightningBolt) {
                    weaponType = SWORD;
                } else {
                    return;
                }

                final float attackDamage = weaponType != null && instance.getAttribute(CRITICAL, weaponType) > new Random().nextInt(100)
                        ? 2 * event.getAmount()
                        : event.getAmount();

                if (weaponType == SWORD) {
                    ((EntityPlayer) trueSource).getFoodStats().addStats((int) attackDamage / 5, attackDamage / 5);
                }

                event.setAmount(attackDamage);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(final LivingKnockBackEvent event) {
        if (!event.getEntity().world.isRemote) {
            Entity attacker = event.getAttacker();
            SoulType weaponType = null;
            final ISoulCapability instance = SoulWeaponProvider.get(attacker);

            if (attacker instanceof EntitySoulDagger) {
                attacker = ((EntitySoulDagger) attacker).shootingEntity;
                weaponType = DAGGER;
            } else if (attacker instanceof EntityPlayer) {
                weaponType = instance.getCurrentType();
            }

            if (attacker instanceof EntityPlayer && weaponType != null) {
                if (SoulItemHelper.isSoulWeaponEquipped((EntityPlayer) attacker)) {
                    event.setStrength(event.getStrength() * (1 + instance.getAttribute(KNOCKBACK_ATTRIBUTE, weaponType) / 6));
                }
            }
        }

        if (event.getEntity() instanceof EntityPlayer) {
            final ISoulWeapon capability = SoulWeaponProvider.get(event.getEntity());

            if (capability.getCharging() > 0) {
                event.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        if (event.getSource().getTrueSource() != null && !event.getSource().getTrueSource().world.isRemote) {
            final EntityLivingBase entity = event.getEntityLiving();
            final IAttributeInstance attackDamage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            final IAttributeInstance armor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
            final Entity trueSource = event.getSource().getTrueSource();
            final ISoulWeapon instance;
            final SoulType weaponType;
            final String displayName;
            Entity source = event.getSource().getImmediateSource();

            if (trueSource instanceof EntityPlayer) {
                final EntityPlayer player = ((EntityPlayer) trueSource);
                instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                    displayName = ((EntitySoulDagger) source).itemStack.getDisplayName();
                    source = ((EntitySoulDagger) source).shootingEntity;
                } else if (source instanceof EntitySoulLightningBolt) {
                    weaponType = SWORD;
                    source = ((EntitySoulLightningBolt) source).getCaster();
                    displayName = ItemHelper.getEquippedItemStack(player, SOULBOUND_SWORD).getDisplayName();
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getType((player.getHeldItemMainhand()));
                    displayName = player.getHeldItemMainhand().getDisplayName();
                } else {
                    return;
                }
            } else {
                return;
            }

            if (source instanceof EntityPlayer && weaponType != null) {
                double xp = entity.getMaxHealth()
                        * source.world.getDifficulty().getId() * MainConfig.instance().getDifficultyMultiplier()
                        * (1 + armor.getAttributeValue() * MainConfig.instance().getArmorMultiplier());

                xp *= attackDamage != null ? 1 + attackDamage.getAttributeValue() * MainConfig.instance().getAttackDamageMultiplier() : MainConfig.instance().getPassiveMultiplier();

                if (!entity.isNonBoss()) {
                    xp *= MainConfig.instance().getBossMultiplier();
                }

                if (source.world.getWorldInfo().isHardcoreModeEnabled()) {
                    xp *= MainConfig.instance().getHardcoreMultiplier();
                }

                if (attackDamage != null && entity.isChild()) {
                    xp *= MainConfig.instance().getBabyMultiplier();
                }

                if (instance.addDatum((int) Math.round(xp), DATA.xp, weaponType) && MainConfig.instance().getLevelupNotifications()) {
                    Main.CHANNEL.sendTo(new S2CLevelupMessage(displayName, instance.getDatum(DATA.level, weaponType)), (EntityPlayerMP) source);
                }

                instance.sync();
            }
        }
    }

    @SubscribeEvent(priority = HIGH)
    public static void onLivingFall(final LivingFallEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            final ISoulWeapon capability = SoulWeaponProvider.get(event.getEntity());

            if (capability.getCharging() > 0) {
                if (capability.getCharging() >= 0.999) {
                    final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
                    final double radius = Math.min(6, 2 * event.getDistance());
                    final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player,
                            new AxisAlignedBB(player.posX - radius, player.posY - radius, player.posZ - radius,
                                    player.posX + radius, player.posY + radius, player.posZ + radius));
                    for (final Entity entity : nearbyEntities) {
                        if (entity.getDistanceSq(entity) <= radius * radius) {
                            final IFrozen frozenCapability = FrozenProvider.get(entity);

                            if (frozenCapability != null) {
                                frozenCapability.freeze(player, 0.4F * event.getDistance(), (int) Math.min(60, 12 * event.getDistance()));
                            }
                        }
                    }

                    if (!nearbyEntities.isEmpty() && player.world instanceof WorldServer) {
                        final WorldServer world = (WorldServer) player.world;

                        for (double i = 0; i <= 2 * radius; i += radius / 64F) {
                            final double x = radius - i;
                            final double z = Math.sqrt((radius * radius - x * x));
                            final int particles = 1;

                            world.spawnParticle(EnumParticleTypes.SNOWBALL,
                                    player.posX + x,
                                    player.posY + player.eyeHeight,
                                    player.posZ + z,
                                    particles, 0, 0, 0, 0D
                            );

                            world.spawnParticle(EnumParticleTypes.SNOWBALL,
                                    player.posX + x,
                                    player.posY + player.eyeHeight,
                                    player.posZ - z,
                                    particles, 0, 0, 0, 0D
                            );
                        }
                    }
                }

                if (event.getDistance() <= 16 * capability.getCharging()) {
                    event.setCanceled(true);
                } else if (capability.getCharging() > 0) {
                    event.setDamageMultiplier(event.getDamageMultiplier()
                            / (float) (Math.max(1, Math.log(4 * capability.getCharging()) / Math.log(2))));
                }

                capability.setCharging(0);
            }
        }
    }

    @SubscribeEvent(priority = LOW)
    public static void onEntityItemPickup(final EntityItemPickupEvent event) {
        event.setResult(ALLOW);

        SoulItemHelper.addItemStack(event.getItem().getItem(), event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void onBreakSpeed(final BreakSpeed event) {
        if (event.getEntityPlayer().getHeldItemMainhand().getItem() instanceof ISoulItem) {
            final ISoulItem item = (ISoulItem) event.getEntityPlayer().getHeldItemMainhand().getItem();
            final ISoulCapability capability = SoulItemHelper.getCapability(event.getEntityPlayer(), (Item) item);
            final SoulType type = capability.getCurrentType();

            if (item instanceof IItemSoulTool) {
                if (((IItemSoulTool) item).isEffectiveAgainst(event.getState())) {
                    float newSpeed = event.getOriginalSpeed() + capability.getAttribute(EFFICIENCY_ATTRIBUTE, type);
                    final int efficiency = capability.getEnchantment(SoulToolEnchantment.SOUL_EFFICIENCY, type);
                    //noinspection ConstantConditions
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
                final float newSpeed = capability.getAttribute(EFFICIENCY_ATTRIBUTE, capability.getCurrentType());

                event.setNewSpeed(event.getState().getMaterial() == Material.WEB
                        ? Math.max(15, newSpeed)
                        : newSpeed
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase != END) {
            return;
        }

        final ISoulCapability weaponCapability = SoulWeaponProvider.get(event.player);
        final ISoulCapability toolCapability = SoulToolProvider.get(event.player);

        weaponCapability.update();
        toolCapability.update();
    }

    @SubscribeEvent
    public static void onRightClickItem(final LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) event.getEntity();
            final ISoulWeapon capability = SoulWeaponProvider.get(player);

            if (event.getItem().getItem() instanceof ItemSoulDagger) {
                final ItemSoulDagger item = (ItemSoulDagger) event.getItem().getItem();

                if (item.getMaxUsageRatio(capability.getAttribute(ATTACK_SPEED, DAGGER, true, true), event.getDuration()) == 1) {
                    event.setDuration(event.getDuration() + 1);
                }

                /*
                if (event.getDuration() == item.getMaxItemUseDuration()) {
                    event.setDuration(item.getMaxItemUseDuration() - 1);
                }

                event.setDuration(Math.round(item.getMaxItemUseDuration() - 20 * item.getMaxUsageRatio(capability.getAttribute(ATTACK_SPEED, DAGGER, true, true), event.getDuration())));
                */
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(final RightClickBlock event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ItemStack stackMainhand = player.getHeldItemMainhand();

        if (stackMainhand.getItem() instanceof ItemSoulWeapon && stackMainhand != event.getItemStack()) {
            event.setUseItem(DENY);
        }
    }

    @SubscribeEvent
    public static void onHarvestDrops(final HarvestDropsEvent event) {
        final EntityPlayer player = event.getHarvester();

        if (player != null && player.getHeldItemMainhand().getItem() instanceof ItemSoulPick && SoulToolProvider.get(player).getDatum(TOOL_DATA.skills, PICK) >= 1) {
            event.setDropChance(0);

            for (final ItemStack drop : event.getDrops()) {
                if (!event.getWorld().isRemote && !drop.isEmpty() && event.getWorld().getGameRules().getBoolean("doTileDrops") && !event.getWorld().restoringBlockSnapshots) {
                    final Vec3d pos = player.getPositionVector();

                    event.getWorld().spawnEntity(new EntityItem(event.getWorld(), pos.x, pos.y, pos.z, drop));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(final LivingAttackEvent event) {
        if (event.getEntity() instanceof EntityPlayer
                && SoulWeaponProvider.get(event.getEntity()).getCharging() > 0) {
            final DamageSource damageSource = event.getSource();

            if (!damageSource.damageType.equals("explosion") && !damageSource.damageType.equals("explosion.player")
                    && !(damageSource instanceof EntityDamageSourceIndirect)) {
                event.setCanceled(true);
            }
        }
    }

    /*
    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onLeftClickBlock(final LeftClickBlock event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ISoulCapability capability = SoulItemHelper.getCapability(player, player.getHeldItemOffhand());
        final Minecraft minecraft = Minecraft.getMinecraft();

        if (capability instanceof SoulTool && capability.getDatum(SKILLS, capability.getCurrentType()) >= 1) {
            final BlockPos blockPos = minecraft.objectMouseOver.getBlockPos();

            if (!minecraft.world.isAirBlock(blockPos)) {
                ModPlayerControllerMP.modController.clickBlockOffhand(blockPos, event.getFace());
            }
        }
    }
    */

    @SubscribeEvent
    public static void onLivingUpdate(final LivingUpdateEvent event) {
        final EntityLivingBase entity = event.getEntityLiving();
        final IFrozen capability = FrozenProvider.get(entity);

        if (capability != null) {
            event.setCanceled(capability.update());
        }

    }

    @SubscribeEvent
    public static void onGetCollsionBoxes(final GetCollisionBoxesEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            final ISoulWeapon capability = SoulWeaponProvider.get(event.getEntity());
            final double charging = capability.getCharging();

            if (charging > 0) {
                final EntityPlayer player = (EntityPlayer) event.getEntity();
                final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, event.getAabb());

                for (final Entity entity : nearbyEntities) {
                    final IFrozen frozenCapability = FrozenProvider.get(entity);

                    if (frozenCapability != null) {
                        frozenCapability.freeze(player, 3 * (float) Math.sqrt(player.motionX * player.motionX
                                + player.motionY * player.motionY
                                + player.motionZ * player.motionZ) * (float) charging, (int) (30 * charging));
                    }
                }

                if (player.onGround && (event.getEntity().motionY == 0 || player.isCreative())) {
                    scheduledTasks.put(new Runnable() {
                        @Override
                        public void run() {
                            capability.setCharging(0);
                        }
                    }, 7);
                }

                if (player.isInLava()) {
                    capability.setCharging(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(final WorldTickEvent event) {
        if (event.phase == END) {
            for (final Runnable task : scheduledTasks.keySet()) {
                final Integer ticks = scheduledTasks.get(task);

                if (ticks > 0) {
                    scheduledTasks.put(task, ticks - 1);
                } else {
                    task.run();
                    scheduledTasks.remove(task);
                }
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            if (MENU_KEY.isPressed()) {
                final EntityPlayer player = minecraft.player;

                if (player.getHeldItemMainhand().getItem() instanceof ItemSoulWeapon) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu());
                } else if (player.getHeldItemMainhand().getItem() instanceof IItemSoulTool) {
                    minecraft.displayGuiScreen(new SoulToolMenu());
                } else if (player.getHeldItemMainhand().getItem() == Items.WOODEN_SWORD) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu(0));
                } else if (player.getHeldItemMainhand().getItem() == Items.WOODEN_PICKAXE) {
                    minecraft.displayGuiScreen(new SoulToolMenu(-1));
                } else if (player.getHeldItemOffhand().getItem() instanceof ItemSoulWeapon) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu());
                } else if (player.getHeldItemOffhand().getItem() instanceof IItemSoulTool) {
                    minecraft.displayGuiScreen(new SoulToolMenu());
                } else if (player.getHeldItemOffhand().getItem() == Items.WOODEN_SWORD) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu(0));
                } else if (player.getHeldItemOffhand().getItem() == Items.WOODEN_PICKAXE) {
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

            if (itemStack.getItem() instanceof ISoulItem) {
                final ISoulCapability capability = SoulItemHelper.getCapability(player, itemStack.getItem());
                final SoulType type = capability.getType(itemStack);
                final List<String> tooltip = event.getToolTip();
                final int startIndex = tooltip.indexOf(I18n.format("item.modifiers.mainhand")) + 1;

                final String[] prior = tooltip.subList(0, startIndex).toArray(new String[0]);
                final String[] posterior = tooltip.subList(startIndex + itemStack.getAttributeModifiers(MAINHAND).size(), tooltip.size()).toArray(new String[0]);

                tooltip.clear();
                tooltip.addAll(Arrays.asList(prior));
                tooltip.addAll(capability.getTooltip(type));
                tooltip.addAll(Arrays.asList(posterior));
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
