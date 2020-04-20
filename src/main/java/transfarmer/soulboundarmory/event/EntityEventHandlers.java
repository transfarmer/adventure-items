package transfarmer.soulboundarmory.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.config.IPlayerConfig;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.item.ItemSoulboundDagger;
import transfarmer.soulboundarmory.network.server.C2SConfig;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.SWORD;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.CRITICAL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EntityEventHandlers {
    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        final IFrozen frozenCapability = FrozenProvider.get(entity);
        final IPlayerConfig config = PlayerConfigProvider.get(entity);
        final IItemCapability toolCapability = ToolProvider.get(entity);
        final IItemCapability weaponCapability = WeaponProvider.get(entity);

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
            final IItem weaponType;

            if (trueSource instanceof EntityPlayer) {
                final IItemCapability instance = WeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getItemType();
                } else if (source instanceof EntitySoulLightningBolt) {
                    weaponType = SWORD;
                } else {
                    return;
                }

                final float attackDamage = weaponType != null && instance.getAttribute(weaponType, CRITICAL) > new Random().nextInt(100)
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
            IItem weaponType = null;
            final IItemCapability instance = WeaponProvider.get(attacker);

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

        if (!entity.world.isRemote) {
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
    }

    @SubscribeEvent(priority = HIGH)
    public static void onLivingFall(final LivingFallEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            final IWeapon capability = WeaponProvider.get(player);
            final double leapForce = capability.getLeapForce();

            if (leapForce > 0) {
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

                if (event.getDistance() <= 16 * leapForce) {
                    event.setCanceled(true);
                } else {
                    event.setDamageMultiplier(event.getDamageMultiplier()
                            / (float) (Math.max(1, Math.log(4 * leapForce) / Math.log(2))));
                }

                capability.setLeapDuration(4);
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

//                if (event.getDuration() == item.getMaxItemUseDuration()) {
//                    event.setDuration(item.getMaxItemUseDuration() - 1);
//                }
//
//                event.setDuration(Math.round(item.getMaxItemUseDuration() - 20 * item.getMaxUsageRatio(capability.getAttribute(ATTACK_SPEED, DAGGER, true, true), event.getDuration())));
            }
        }
    }
}
