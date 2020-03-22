package transfarmer.soulboundarmory.event;

import net.minecraft.block.material.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.RegistryEvent.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerEvent.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.*;
import net.minecraftforge.event.world.BlockEvent.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.*;
import net.minecraftforge.fml.common.gameevent.TickEvent.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.relauncher.*;
import transfarmer.soulboundarmory.*;
import transfarmer.soulboundarmory.capability.*;
import transfarmer.soulboundarmory.capability.tool.*;
import transfarmer.soulboundarmory.capability.weapon.*;
import transfarmer.soulboundarmory.client.gui.*;
import transfarmer.soulboundarmory.entity.*;
import transfarmer.soulboundarmory.item.*;
import transfarmer.soulboundarmory.network.client.*;
import transfarmer.soulboundarmory.network.client.tool.*;
import transfarmer.soulboundarmory.network.client.weapon.*;
import transfarmer.soulboundarmory.statistics.*;
import transfarmer.soulboundarmory.statistics.tool.*;
import transfarmer.soulboundarmory.util.*;

import java.util.*;
import java.util.concurrent.*;

import static net.minecraft.inventory.EntityEquipmentSlot.*;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.*;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.*;
import static net.minecraftforge.fml.relauncher.Side.*;
import static transfarmer.soulboundarmory.Main.ResourceLocations.*;
import static transfarmer.soulboundarmory.client.KeyBindings.*;
import static transfarmer.soulboundarmory.init.ModItems.*;
import static transfarmer.soulboundarmory.statistics.SoulDatum.*;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulToolDatum.*;
import static transfarmer.soulboundarmory.statistics.SoulType.*;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EventSubscriber {
    private static final Map<Entity, Integer> frozenEntities = new ConcurrentHashMap<>();
    private static final Map<Runnable, Integer> scheduledTasks = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onRegisterEntityEntry(final Register<EntityEntry> entry) {
        entry.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntitySoulDagger.class)
                .id(new ResourceLocation(Main.MOD_ID, "entity_soul_dagger"), 0)
                .name("soul dagger")
                .tracker(512, 1, true)
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

        Main.CHANNEL.sendTo(new CConfig(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerRespawnEvent event) {
        updatePlayer(event.player);

        ISoulCapability capability = SoulWeaponProvider.get(event.player);
        SoulType type = capability.getCurrentType();

        if (!event.player.world.getGameRules().getBoolean("keepInventory")) {
            if (type != null && capability.getDatum(DATA.level, type) >= Configuration.preservationLevel) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }

            capability = SoulToolProvider.get(event.player);
            type = capability.getCurrentType();

            if (type != null && capability.getDatum(DATA.level, type) >= Configuration.preservationLevel) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }
        }
    }

    private static void updatePlayer(final EntityPlayer player) {
        final ISoulWeapon weaponCapability = SoulWeaponProvider.get(player);
        final ISoulCapability toolCapability = SoulToolProvider.get(player);

        Main.CHANNEL.sendTo(new CWeaponData(
                weaponCapability.getCurrentType(),
                weaponCapability.getCurrentTab(),
                weaponCapability.getCooldown(),
                weaponCapability.getBoundSlot(),
                weaponCapability.getData(),
                weaponCapability.getAttributes(),
                weaponCapability.getEnchantments()), (EntityPlayerMP) player
        );
        Main.CHANNEL.sendTo(new CToolData(
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
            SoulType type = capability.getCurrentType();

            if (type != null && capability.getDatum(DATA.level, type) >= Configuration.preservationLevel) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof ItemSoulWeapon);
            }

            capability = SoulToolProvider.get(player);
            type = capability.getCurrentType();

            if (type != null && capability.getDatum(DATA.level, type) >= Configuration.preservationLevel) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof IItemSoulTool);
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
            final SoulType weaponType;

            if (trueSource instanceof EntityPlayer) {
                final ISoulCapability instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getCurrentType();
                } else if (source instanceof EntitySoulLightningBolt) {
                    weaponType = SWORD;
                } else return;

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
                instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                    displayName = ((EntitySoulDagger) source).itemStack.getDisplayName();
                    source = ((EntitySoulDagger) source).shootingEntity;
                } else if (source instanceof EntitySoulLightningBolt) {
                    weaponType = SWORD;
                    source = ((EntitySoulLightningBolt) source).getCaster();
                    displayName = ItemHelper.getEquippedItemStack((EntityPlayer) source, SOULBOUND_SWORD).getDisplayName();
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getCurrentType();
                    displayName = ((EntityPlayerMP) source).getHeldItemMainhand().getDisplayName();
                } else return;
            } else return;

            if (source instanceof EntityPlayer && weaponType != null) {
                if (attackDamage != null || Configuration.passiveXP || entity instanceof EntitySlime) {
                    double attackDamageValue = 0;

                    if (attackDamage != null) {
                        attackDamageValue = attackDamage.getAttributeValue();
                    }

                    int xp = (int) Math.round(entity.getMaxHealth()
                            * source.world.getDifficulty().getId() * Configuration.multipliers.difficultyMultiplier
                            * (1 + attackDamageValue * Configuration.multipliers.attackDamageMultiplier)
                            * (1 + armor.getAttributeValue() * Configuration.multipliers.armorMultiplier));

                    if (!entity.isNonBoss()) {
                        xp *= Configuration.multipliers.bossMultiplier;
                    }

                    if (source.world.getWorldInfo().isHardcoreModeEnabled()) {
                        xp *= Configuration.multipliers.hardcoreMultiplier;
                    }

                    if (attackDamage != null && entity.isChild()) {
                        xp *= Configuration.multipliers.babyMultiplier;
                    }

                    if (instance.addDatum(xp, DATA.xp, weaponType) && Configuration.levelupNotifications) {
                        Main.CHANNEL.sendTo(new CLevelupMessage(displayName, instance.getDatum(DATA.level, weaponType)), (EntityPlayerMP) source);
                    }

                    Main.CHANNEL.sendTo(new CWeaponDatum(xp, TOOL_DATA.xp, weaponType), (EntityPlayerMP) source);
                }
            }
        }
    }

    @SubscribeEvent
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
                            freezeEntity(entity, player, 0.4F * event.getDistance(), event.getDistance());
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

    private static void freezeEntity(final Entity entity, final EntityPlayer player,
                                     final float damage, final float seconds) {
        if (entity.isNonBoss() && (entity instanceof EntityLivingBase || entity instanceof IProjectile)
                && !(entity instanceof EntityReachModifier) && player.world instanceof WorldServer) {
            ((WorldServer) player.world).spawnParticle(EnumParticleTypes.SNOWBALL,
                    entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, 32, 0.1, 0, 0.1, 2D);
            entity.playSound(SoundEvents.BLOCK_SNOW_HIT, 1, 1.2F / (entity.world.rand.nextFloat() * 0.2F + 0.9F));
            entity.extinguish();

            frozenEntities.put(entity, Math.min(60, Math.round(20 * seconds)));

            if (entity instanceof EntityLivingBase) {
                entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(player, null).setDamageIsAbsolute(),
                        damage);
            }

            if (entity instanceof EntityCreeper) {
                ((EntityCreeper) entity).setCreeperState(-1);
            }

            if (entity instanceof IProjectile) {
                entity.motionX = entity.motionZ = 0;
            }

            if (entity instanceof EntityPigZombie) {
            }
        }
    }

    @SubscribeEvent
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
        if (event.phase != END) return;

        final ISoulCapability weaponCapability = SoulWeaponProvider.get(event.player);
        final ISoulCapability toolCapability = SoulToolProvider.get(event.player);

        if (weaponCapability.getPlayer() == null) {
            weaponCapability.setPlayer(event.player);
        }

        if (toolCapability.getPlayer() == null) {
            toolCapability.setPlayer(event.player);
        }

        weaponCapability.update();
        toolCapability.update();
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
    public static void onBreak(final HarvestDropsEvent event) {
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

        if (frozenEntities.containsKey(entity)) {
            final int ticks = frozenEntities.get(entity);

            if (ticks > 0 && entity.isEntityAlive() && !entity.isBurning()) {
                if (entity.hurtResistantTime > 0) {
                    entity.hurtResistantTime--;
                }

                event.setCanceled(true);

                frozenEntities.put(entity, ticks - 1);
            } else {
                frozenEntities.remove(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onGetCollsionBoxes(final GetCollisionBoxesEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            final ISoulWeapon capability = SoulWeaponProvider.get(event.getEntity());

            if (capability.getCharging() > 0) {
                final EntityPlayer player = (EntityPlayer) event.getEntity();
                final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, event.getAabb());

                for (final Entity entity : nearbyEntities) {
                    freezeEntity(entity, player, 3 * (float) Math.sqrt(player.motionX * player.motionX
                            + player.motionY * player.motionY
                            + player.motionZ * player.motionZ), 1
                    );
                }

                if (event.getEntity().motionY == 0 && player.onGround) {
                    scheduledTasks.put(new Runnable() {
                        @Override
                        public void run() {
                            capability.setCharging(0);
                        }
                    }, 7);
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
