package transfarmer.soulboundarmory.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
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
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.item.ItemSoulDagger;
import transfarmer.soulboundarmory.network.client.S2CLevelupMessage;
import transfarmer.soulboundarmory.network.server.C2SConfig;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.util.ItemHelper;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH;
import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_SWORD;
import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MOD_ID)
public class EntityEventHandlers {
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

            if (capability.getLeapForce() > 0) {
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
            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            final ISoulWeapon capability = SoulWeaponProvider.get(player);
            final float leapForce = capability.getLeapForce();

            if (leapForce > 0) {
                final double horizontalRadius = Math.min(4, event.getDistance());
                final double verticalRadius = Math.min(2, 0.5F * event.getDistance());
                final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(
                        player.posX - horizontalRadius, player.posY - verticalRadius, player.posZ - horizontalRadius,
                        player.posX + horizontalRadius, player.posY + verticalRadius, player.posZ + horizontalRadius
                ));

                for (final Entity entity : nearbyEntities) {
                    final IFrozen frozenCapability = FrozenProvider.get(entity);

                    if (frozenCapability != null && entity.getDistanceSq(entity) <= horizontalRadius * horizontalRadius) {
                        frozenCapability.freeze(player, 0.4F * event.getDistance(), (int) Math.min(60, 12 * event.getDistance()));
                    }
                }

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
            final ISoulWeapon capability = SoulWeaponProvider.get(player);

            if (event.getItem().getItem() instanceof ItemSoulDagger) {
                final ItemSoulDagger item = (ItemSoulDagger) event.getItem().getItem();

                if (item.getMaxUsageRatio(capability.getAttribute(ATTACK_SPEED, DAGGER, true, true), event.getDuration()) == 1) {
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

    @SubscribeEvent
    public static void onLivingAttack(final LivingAttackEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof EntityPlayer && SoulWeaponProvider.get(entity).getLeapForce() > 0) {
            final DamageSource damageSource = event.getSource();

            if (!damageSource.damageType.equals("explosion") && !damageSource.damageType.equals("explosion.player")
                    && !(damageSource instanceof EntityDamageSourceIndirect)) {
                event.setCanceled(true);
            }
        }
    }
}
