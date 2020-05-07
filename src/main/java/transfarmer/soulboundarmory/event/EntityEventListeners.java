package transfarmer.soulboundarmory.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.capability.entity.EntityDatumProvider;
import transfarmer.soulboundarmory.capability.entity.IEntityData;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.entity.EntitySoulboundDagger;
import transfarmer.soulboundarmory.entity.EntitySoulboundSmallFireball;
import transfarmer.soulboundarmory.item.ItemSoulboundDagger;
import transfarmer.soulboundarmory.network.C2S.C2SConfig;
import transfarmer.soulboundarmory.skill.SkillLevelable;
import transfarmer.soulboundarmory.skill.common.SkillLeeching;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.EntityUtil;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH;
import static transfarmer.soulboundarmory.capability.entity.EntityDatumProvider.FROZEN;
import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOLS;
import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_SWORD;
import static transfarmer.soulboundarmory.init.Skills.FREEZING;
import static transfarmer.soulboundarmory.init.Skills.LEECHING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.GREATSWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.STAFF;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.CRITICAL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EntityEventListeners {
    @SubscribeEvent
    public static void onLivingAttack(final LivingAttackEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof EntityPlayer && WeaponProvider.get(entity).getLeapForce() > 0) {
            final DamageSource damageSource = event.getSource();

            if (!damageSource.damageType.contains("explosion") && !(damageSource instanceof EntityDamageSourceIndirect)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(final LivingHurtEvent event) {
        final Entity trueSource = event.getSource().getTrueSource();

        if (trueSource instanceof EntityPlayer && !trueSource.world.isRemote) {
            final Entity source = event.getSource().getImmediateSource();
            final SoulboundCapability instance = WeaponProvider.get(trueSource);
            final IItem item;

            if (source instanceof EntitySoulboundDagger) {
                item = DAGGER;
            } else if (source instanceof EntityPlayer) {
                item = instance.getItemType();
            } else if (source instanceof EntitySoulLightningBolt) {
                item = SWORD;
            } else if (source instanceof EntitySoulboundSmallFireball) {
                item = STAFF;
            } else {
                return;
            }

            final Random random = trueSource.world.rand;
            final float attackDamage = item != null && instance.getAttribute(item, CRITICAL) > random.nextDouble()
                    ? 2 * event.getAmount()
                    : event.getAmount();

            if (instance.hasSkill(item, new SkillLeeching())) {
                final SkillLevelable leeching = (SkillLevelable) instance.getSkill(item, LEECHING);

                final float food = (1 + leeching.getLevel()) * attackDamage / 20F;
                final int r = random.nextInt((int) Math.ceil(food) + 1);
                ((EntityPlayer) trueSource).getFoodStats().addStats(r, 2 * food);
            }

            event.setAmount(attackDamage);
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(final LivingKnockBackEvent event) {
        final Entity entity = event.getEntity();

        if (!entity.world.isRemote) {
            Entity attacker = event.getAttacker();
            IItem weaponType = null;
            final SoulboundCapability instance = WeaponProvider.get(attacker);

            if (attacker instanceof EntitySoulboundDagger) {
                attacker = ((EntitySoulboundDagger) attacker).shootingEntity;
                weaponType = DAGGER;
            } else if (attacker instanceof EntityPlayer) {
                weaponType = instance.getItemType();
            }

            if (attacker instanceof EntityPlayer && weaponType != null) {
                if (SoulboundItemUtil.isSoulWeaponEquipped((EntityPlayer) attacker)) {
                    event.setStrength((event.getStrength() * (float) (1 + instance.getAttribute(weaponType, KNOCKBACK_ATTRIBUTE) / 6)));
                }
            }

            if (entity instanceof EntityPlayer) {
                final IWeaponCapability capability = WeaponProvider.get(entity);

                if (capability.getLeapForce() > 0) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        final EntityLivingBase entity = event.getEntityLiving();

        Entity attacker = event.getSource().getTrueSource();

        if (attacker == null) {
            attacker = entity.getCombatTracker().getBestAttacker();
        }

        if ((attacker instanceof EntityPlayer) && !attacker.world.isRemote) {
            final IAttributeInstance attackDamage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            final IAttributeInstance armor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
            final Entity immediateSource = event.getSource().getImmediateSource();
            final EntityPlayer player = ((EntityPlayer) attacker);
            final IWeaponCapability capability = WeaponProvider.get(attacker);
            final IItem item;
            final String displayName;

            if (immediateSource instanceof EntitySoulboundDagger) {
                item = DAGGER;
                displayName = ((EntitySoulboundDagger) immediateSource).itemStack.getDisplayName();
            } else if (immediateSource instanceof EntitySoulLightningBolt) {
                item = SWORD;
                displayName = ItemUtil.getEquippedItemStack(player, SOULBOUND_SWORD).getDisplayName();
            } else {
                item = capability.getItemType((player.getHeldItemMainhand()));
                displayName = player.getHeldItemMainhand().getDisplayName();
            }

            if (item != null) {
                double xp = entity.getMaxHealth()
                        * attacker.world.getDifficulty().getId() * MainConfig.instance().getDifficultyMultiplier()
                        * (1 + armor.getAttributeValue() * MainConfig.instance().getArmorMultiplier());

                xp *= attackDamage != null ? 1 + attackDamage.getAttributeValue() * MainConfig.instance().getAttackDamageMultiplier() : MainConfig.instance().getPassiveMultiplier();

                if (!entity.isNonBoss()) {
                    xp *= MainConfig.instance().getBossMultiplier();
                }

                if (attacker.world.getWorldInfo().isHardcoreModeEnabled()) {
                    xp *= MainConfig.instance().getHardcoreMultiplier();
                }

                if (attackDamage != null && entity.isChild()) {
                    xp *= MainConfig.instance().getBabyMultiplier();
                }

                if (capability.addDatum(item, XP, (int) Math.round(xp)) && MainConfig.instance().getLevelupNotifications()) {
                    attacker.sendMessage(new TextComponentTranslation("message.soulboundarmory.levelup", displayName, capability.getDatum(item, LEVEL)));
                }

                capability.sync();
            }
        }
    }

    @SubscribeEvent(priority = HIGH)
    public static void onLivingFall(final LivingFallEvent event) {
        final Entity fallen = event.getEntity();

        if (!fallen.world.isRemote && fallen instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) fallen;
            final IWeaponCapability capability = WeaponProvider.get(player);
            final double leapForce = capability.getLeapForce();

            if (leapForce > 0) {
                if (capability.hasSkill(GREATSWORD, FREEZING)) {
                    final double radiusXZ = Math.min(4, event.getDistance());
                    final double radiusY = Math.min(2, 0.5F * event.getDistance());
                    final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(
                            player.posX - radiusXZ, player.posY - radiusY, player.posZ - radiusXZ,
                            player.posX + radiusXZ, player.posY + radiusY, player.posZ + radiusXZ
                    ));

                    boolean froze = false;

                    for (final Entity entity : nearbyEntities) {
                        final IEntityData frozenCapability = EntityDatumProvider.get(entity);

                        if (frozenCapability != null && entity.getDistanceSq(entity) <= radiusXZ * radiusXZ) {
                            capability.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());

                            froze = true;
                        }
                    }

                    if (froze) {
                        if (!nearbyEntities.isEmpty()) {
                            final WorldServer world = (WorldServer) player.world;

                            for (double i = 0; i <= 2 * radiusXZ; i += radiusXZ / 48D) {
                                final double x = radiusXZ - i;
                                final double z = Math.sqrt((radiusXZ * radiusXZ - x * x));
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
                }

                if (event.getDistance() <= 16 * leapForce) {
                    event.setCanceled(true);
                } else {
                    final float multiplier = event.getDamageMultiplier() / (float) (Math.max(1, Math.log(4 * leapForce) / Math.log(2)));

                    event.setDamageMultiplier(multiplier);
                }

                capability.setLeapDuration(4);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(final LivingUpdateEvent event) {
        final IEntityData entityData = EntityDatumProvider.get(event.getEntity());

        if (entityData != null) {
            entityData.onUpdate();

            if (entityData.isFrozen()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onGetCollsionBoxes(final GetCollisionBoxesEvent event) {
        final Entity entity = event.getEntity();

        if (!event.getWorld().isRemote && entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) entity;
            final IWeaponCapability capability = WeaponProvider.get(player);
            final double leapForce = capability.getLeapForce();

            if (leapForce > 0) {
                if (capability.hasSkill(GREATSWORD, FREEZING)) {
                    final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, event.getAabb());

                    for (final Entity nearbyEntity : nearbyEntities) {
                        capability.freeze(nearbyEntity, (int) (20 * leapForce), (float) EntityUtil.getVelocity(player) * (float) leapForce);
                    }
                }

                if (capability.getLeapDuration() <= 0 && player.onGround && (player.motionY <= 0.01 || player.isCreative())) {
                    capability.setLeapDuration(7);
                }

                if (player.isInLava()) {
                    capability.resetLeapForce();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(final LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) event.getEntity();
            final IWeaponCapability capability = WeaponProvider.get(player);

            if (event.getItem().getItem() instanceof ItemSoulboundDagger) {
                final ItemSoulboundDagger item = (ItemSoulboundDagger) event.getItem().getItem();

                if (item.getMaxUsageRatio((float) capability.getAttribute(DAGGER, ATTACK_SPEED), event.getDuration()) == 1) {
                    event.setDuration(event.getDuration() + 1);
                }
//
//                if (event.getDuration() == item.getMaxItemUseDuration()) {
//                    event.setDuration(item.getMaxItemUseDuration() - 1);
//                }
//
//                event.setDuration(Math.round(item.getMaxItemUseDuration() - 20 * item.getMaxUsageRatio(capability.getAttribute(ATTACK_SPEED, DAGGER, true, true), event.getDuration())));
            }
        }
    }

    @SubscribeEvent
    public static void onEnderTeleport(final EnderTeleportEvent event) {
        final IEntityData capability = EntityDatumProvider.get(event.getEntity());

        if (capability != null && capability.cannotTeleport()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRegisterEntityEntry(final Register<EntityEntry> event) {
        final IForgeRegistry<EntityEntry> registry = event.getRegistry();

        registry.register(EntityEntryBuilder.create()
                .entity(EntitySoulboundDagger.class)
                .id(new ResourceLocation(Main.MOD_ID, "entity_soul_dagger"), 0)
                .name("soul dagger")
                .tracker(512, 1, true)
                .build()
        );
        registry.register(EntityEntryBuilder.create()
                .entity(EntityReachModifier.class)
                .id(new ResourceLocation(Main.MOD_ID, "entity_reach_extender"), 1)
                .name("reach extender")
                .tracker(16, 1, true)
                .build()
        );
        registry.register(EntityEntryBuilder.create()
                .entity(EntitySoulboundSmallFireball.class)
                .id(new ResourceLocation(Main.MOD_ID, "entity_soulbound_small_fireball"), 2)
                .name("small fireball")
                .tracker(512, 1, true)
                .build()
        );
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        final Entity entity = event.getObject();

        if (entity instanceof EntityPlayer) {
            final ToolProvider tools = new ToolProvider();
            final WeaponProvider weapons = new WeaponProvider();

            event.addCapability(new ResourceLocation(Main.MOD_ID, "soulboundtool"), tools);
            event.addCapability(new ResourceLocation(Main.MOD_ID, "soulboundweapon"), weapons);
            event.addCapability(new ResourceLocation(Main.MOD_ID, "playerconfig"), new PlayerConfigProvider());
            tools.getCapability(TOOLS, null).initPlayer((EntityPlayer) entity);
            weapons.getCapability(WEAPONS, null).initPlayer((EntityPlayer) entity);

            if (entity.world.isRemote) {
                Main.CHANNEL.sendToServer(new C2SConfig());
            }
        }

        if ((entity instanceof EntityLivingBase || entity instanceof IProjectile)
                && !(entity instanceof EntityReachModifier) && !(entity instanceof EntitySoulboundDagger)) {
            final EntityDatumProvider data = new EntityDatumProvider();

            event.addCapability(new ResourceLocation(Main.MOD_ID, "entitydata"), data);
            data.getCapability(FROZEN, null).setEntity(entity);
        }
    }
}
