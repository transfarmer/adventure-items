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
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.item.ItemSoulboundDagger;
import transfarmer.soulboundarmory.network.server.C2SConfig;
import transfarmer.soulboundarmory.skill.SkillLevelable;
import transfarmer.soulboundarmory.skill.impl.SkillLeeching;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.EntityUtil;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH;
import static transfarmer.soulboundarmory.capability.frozen.FrozenProvider.FROZEN;
import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOLS;
import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_SWORD;
import static transfarmer.soulboundarmory.skill.Skills.FREEZING;
import static transfarmer.soulboundarmory.skill.Skills.LEECHING;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.GREATSWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.CRITICAL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EntityEventHandlers {
    @SubscribeEvent
    public static void onLivingAttack(final LivingAttackEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof EntityPlayer && WeaponProvider.get(entity).getLeapForce() > 0) {
            final DamageSource damageSource = event.getSource();

            if (!damageSource.damageType.equals("explosion") && !damageSource.damageType.equals("explosion.player")
                    && !(damageSource instanceof EntityDamageSourceIndirect)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(final LivingHurtEvent event) {
        if (event.getSource().getTrueSource() != null && !event.getSource().getTrueSource().world.isRemote) {
            final Entity trueSource = event.getSource().getTrueSource();
            final Entity source = event.getSource().getImmediateSource();
            final IItem item;

            if (trueSource instanceof EntityPlayer) {
                final SoulboundCapability instance = WeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    item = DAGGER;
                } else if (source instanceof EntityPlayer) {
                    item = instance.getItemType();
                } else if (source instanceof EntitySoulLightningBolt) {
                    item = SWORD;
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
    }

    @SubscribeEvent
    public static void onLivingKnockback(final LivingKnockBackEvent event) {
        if (!event.getEntity().world.isRemote) {
            Entity attacker = event.getAttacker();
            IItem weaponType = null;
            final SoulboundCapability instance = WeaponProvider.get(attacker);

            if (attacker instanceof EntitySoulDagger) {
                attacker = ((EntitySoulDagger) attacker).shootingEntity;
                weaponType = DAGGER;
            } else if (attacker instanceof EntityPlayer) {
                weaponType = instance.getItemType();
            }

            if (attacker instanceof EntityPlayer && weaponType != null) {
                if (SoulItemHelper.isSoulWeaponEquipped((EntityPlayer) attacker)) {
                    event.setStrength((event.getStrength() * (float) (1 + instance.getAttribute(weaponType, KNOCKBACK_ATTRIBUTE) / 6)));
                }
            }
        }

        if (event.getEntity() instanceof EntityPlayer) {
            final IWeapon capability = WeaponProvider.get(event.getEntity());

            if (capability.getLeapForce() > 0) {
                event.setCanceled(true);
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
                final IWeapon capability = WeaponProvider.get(attacker);
                final IItem item;
                final String displayName;

                if (immediateSource instanceof EntitySoulDagger) {
                    item = DAGGER;
                    displayName = ((EntitySoulDagger) immediateSource).itemStack.getDisplayName();
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
        if (event.getEntity() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            final IWeapon capability = WeaponProvider.get(player);
            final double leapForce = capability.getLeapForce();

            if (leapForce > 0) {
                if (capability.hasSkill(GREATSWORD, FREEZING)) {
                    final double horizontalRadius = Math.min(4, event.getDistance());
                    final double verticalRadius = Math.min(2, 0.5F * event.getDistance());
                    final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(
                            player.posX - horizontalRadius, player.posY - verticalRadius, player.posZ - horizontalRadius,
                            player.posX + horizontalRadius, player.posY + verticalRadius, player.posZ + horizontalRadius
                    ));

                    boolean froze = false;

                    for (final Entity entity : nearbyEntities) {
                        final IFrozen frozenCapability = FrozenProvider.get(entity);

                        if (frozenCapability != null && entity.getDistanceSq(entity) <= horizontalRadius * horizontalRadius) {
                            capability.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());

                            froze = true;
                        }
                    }

                    if (froze) {
                        if (!nearbyEntities.isEmpty() && player.world instanceof WorldServer) {
                            final WorldServer world = (WorldServer) player.world;

                            for (double i = 0; i <= 2 * horizontalRadius; i += horizontalRadius / 48D) {
                                final double x = horizontalRadius - i;
                                final double z = Math.sqrt((horizontalRadius * horizontalRadius - x * x));
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
    public static void onGetCollsionBoxes(final GetCollisionBoxesEvent event) {
        final Entity entity = event.getEntity();

        if (!event.getWorld().isRemote && entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) entity;
            final IWeapon capability = WeaponProvider.get(player);
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
            final IWeapon capability = WeaponProvider.get(player);

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
                && !(entity instanceof EntityReachModifier) && !(entity instanceof EntitySoulDagger)
                && entity.world instanceof WorldServer) {
            final FrozenProvider frozen = new FrozenProvider();

            event.addCapability(new ResourceLocation(Main.MOD_ID, "frozen"), frozen);
            frozen.getCapability(FROZEN, null).setEntity(entity);
        }
    }
}
