package user11681.soulboundarmory.event;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import user11681.anvil.event.Listener;
import user11681.anvilevents.event.entity.EnderTeleportEvent;
import user11681.anvilevents.event.entity.EntityDamageEvent;
import user11681.anvilevents.event.entity.EntityLandEvent;
import user11681.anvilevents.event.entity.living.LivingCollisionEvent;
import user11681.anvilevents.event.entity.living.LivingDeathEvent;
import user11681.anvilevents.event.entity.living.LivingKnockbackEvent;
import user11681.anvilevents.event.entity.living.LivingTickEvent;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.config.IConfigComponent;
import user11681.soulboundarmory.component.entity.IEntityData;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;
import user11681.soulboundarmory.entity.SoulboundFireballEntity;
import user11681.soulboundarmory.entity.SoulboundLightningEntity;
import user11681.soulboundarmory.item.ModItems;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.usersmanual.entity.EntityUtil;
import user11681.usersmanual.item.ItemUtil;
import user11681.usersmanual.mixin.duck.entity.BossEntityDuck;

import static user11681.soulboundarmory.component.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static user11681.soulboundarmory.component.statistics.StatisticType.KNOCKBACK;
import static user11681.soulboundarmory.component.statistics.StatisticType.LEVEL;
import static user11681.soulboundarmory.component.statistics.StatisticType.XP;
import static user11681.soulboundarmory.skill.Skills.FREEZING;
import static user11681.soulboundarmory.skill.Skills.NOURISHMENT;

public class EntityEventListeners {
    @Listener
    public static void onLivingAttack(final EntityDamageEvent.Pre event) {
        final Entity entity = event.getEntity();

        if (entity instanceof PlayerEntity && !entity.world.isClient && Components.WEAPON_COMPONENT.get(entity).getStorage(StorageType.GREATSWORD_STORAGE).getLeapForce() > 0) {
            final DamageSource damageSource = event.getSource();

            if (!damageSource.isExplosive() && !damageSource.isProjectile()) {
                event.setFail();
            }
        }

        if (entity instanceof PlayerEntity && !entity.world.isClient) {
            final Entity source = event.getSource().getSource();
            final SoulboundComponent instance = Components.WEAPON_COMPONENT.get(entity);
            final ItemStorage<?> storage;

            if (source instanceof SoulboundDaggerEntity) {
                storage = instance.getStorage(StorageType.DAGGER_STORAGE);
            } else if (source instanceof PlayerEntity) {
                storage = instance.getStorage();
            } else if (source instanceof SoulboundLightningEntity) {
                storage = instance.getStorage(StorageType.SWORD_STORAGE);
            } else if (source instanceof SoulboundFireballEntity) {
                storage = instance.getStorage(StorageType.STAFF_STORAGE);
            } else {
                storage = null;
            }

            if (storage != null) {
                final Random random = entity.world.random;
                final float attackDamage = storage.getAttribute(CRITICAL_STRIKE_PROBABILITY) > random.nextDouble()
                        ? 2 * event.getDamage()
                        : event.getDamage();

                if (storage.hasSkill(NOURISHMENT)) {
                    final SkillContainer leeching = storage.getSkill(NOURISHMENT);

                    final float saturation = (1 + leeching.getLevel()) * attackDamage / 20F;
                    final int food = random.nextInt((int) Math.ceil(saturation) + 1);
                    ((PlayerEntity) entity).getHungerManager().add(food, 2 * saturation);
                }

                event.setDamage(attackDamage);
            }
        }
    }

    @Listener
    public static void onLivingKnockback(final LivingKnockbackEvent event) {
        final Entity entity = event.getEntity();

        if (!entity.world.isClient) {
            Entity attacker = event.getAttacker();
            ItemStorage<?> storage = null;
            final SoulboundComponent component = Components.WEAPON_COMPONENT.get(attacker);

            if (attacker instanceof SoulboundDaggerEntity) {
                attacker = ((SoulboundDaggerEntity) attacker).getOwner();
                storage = component.getStorage(StorageType.DAGGER_STORAGE);
            } else if (attacker instanceof PlayerEntity) {
                storage = component.getStorage();
            }

            if (attacker instanceof PlayerEntity && storage != null) {
                if (SoulboundItemUtil.isSoulWeaponEquipped((PlayerEntity) attacker)) {
                    event.setSpeed((event.getSpeed() * (float) (1 + storage.getAttribute(KNOCKBACK) / 6)));
                }
            }

            if (entity instanceof PlayerEntity) {
                if (component.getStorage(StorageType.GREATSWORD_STORAGE).getLeapForce() > 0) {
                    event.setFail();
                }
            }
        }
    }

    @Listener
    public static void onLivingDeath(final LivingDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        Entity attacker = event.getSource().getAttacker();

        if (attacker == null) {
            attacker = entity.getDamageTracker().getBiggestAttacker();
        }

        if ((attacker instanceof PlayerEntity) && !attacker.world.isClient) {
            final EntityAttributeInstance attackDamage = entity.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
            final EntityAttributeInstance armor = entity.getAttributeInstance(EntityAttributes.ARMOR);
            final Entity immediateSource = event.getSource().getSource();
            final PlayerEntity player = ((PlayerEntity) attacker);
            final SoulboundComponent component = Components.WEAPON_COMPONENT.get(attacker);
            final ItemStorage<?> storage;
            final Text displayName;

            if (immediateSource instanceof SoulboundDaggerEntity) {
                storage = component.getStorage(StorageType.DAGGER_STORAGE);
                displayName = ((SoulboundDaggerEntity) immediateSource).itemStack.getName();
            } else if (immediateSource instanceof SoulboundLightningEntity) {
                storage = component.getStorage(StorageType.SWORD_STORAGE);
                displayName = ItemUtil.getEquippedItemStack(player, ModItems.SOULBOUND_SWORD).getName();
            } else {
                storage = component.getStorage();
                displayName = player.getMainHandStack().getName();
            }

            if (storage != null) {
                final Configuration configuration = Configuration.instance();

                double xp = entity.getMaximumHealth()
                        * attacker.world.getDifficulty().getId() * configuration.difficultyMultiplier
                        * (1 + armor.getValue() * configuration.armorMultiplier);

                xp *= attackDamage != null ? 1 + attackDamage.getValue() * configuration.attackDamageMultiplier : configuration.passiveMultiplier;

                if (((BossEntityDuck) entity).isBoss()) {
                    xp *= configuration.bossMultiplier;
                }

                //noinspection ConstantConditions
                if (attacker.world.getServer().isHardcore()) {
                    xp *= configuration.hardcoreMultiplier;
                }

                if (attackDamage != null && entity.isBaby()) {
                    xp *= configuration.babyMultiplier;
                }

                final IConfigComponent configComponent = Components.CONFIG_COMPONENT.get(player);

                if (storage.addDatum(XP, (int) Math.round(xp)) && configComponent.getLevelupNotifications()) {
                    attacker.sendMessage(new TranslatableText("message.soulboundarmory.levelup", displayName, storage.getDatum(LEVEL)));
                }

                component.sync();
            }
        }
    }

    @Listener(priority = 80)
    public static void onLivingFall(final EntityLandEvent event) {
        final Entity fallen = event.getEntity();

        if (!fallen.world.isClient && fallen instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) fallen;
            final SoulboundComponent component = Components.WEAPON_COMPONENT.get(player);
            final GreatswordStorage storage = component.getStorage(StorageType.GREATSWORD_STORAGE);
            final double leapForce = storage.getLeapForce();

            if (leapForce > 0) {
                if (storage.hasSkill(FREEZING)) {
                    final double radiusXZ = Math.min(4, event.getDistance());
                    final double radiusY = Math.min(2, 0.5F * event.getDistance());
                    final List<Entity> nearbyEntities = player.world.getEntities(player, new Box(
                            player.getX() - radiusXZ, player.getY() - radiusY, player.getZ() - radiusXZ,
                            player.getX() + radiusXZ, player.getY() + radiusY, player.getZ() + radiusXZ
                    ));

                    boolean froze = false;

                    for (final Entity entity : nearbyEntities) {
                        final IEntityData frozenComponent = Components.ENTITY_DATA.get(entity);

                        if (entity.squaredDistanceTo(entity) <= radiusXZ * radiusXZ) {
                            storage.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());

                            froze = true;
                        }
                    }

                    if (froze) {
                        if (!nearbyEntities.isEmpty()) {
                            final ServerWorld world = (ServerWorld) player.world;

                            for (double i = 0; i <= 2 * radiusXZ; i += radiusXZ / 48D) {
                                final double x = radiusXZ - i;
                                final double z = Math.sqrt((radiusXZ * radiusXZ - x * x));
                                final int particles = 1;

                                world.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
                                        player.getX() + x,
                                        player.getEyeY(),
                                        player.getZ() + z,
                                        particles, 0, 0, 0, 0D
                                );
                                world.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
                                        player.getX() + x,
                                        player.getEyeY(),
                                        player.getZ() - z,
                                        particles, 0, 0, 0, 0D
                                );
                            }
                        }
                    }
                }

                if (event.getDistance() <= 16 * leapForce) {
                    event.setFail();
                } else {
                    final float multiplier = event.getDistance() / (float) (Math.max(1, Math.log(4 * leapForce) / Math.log(2)));

                    event.setDamageMultiplier(multiplier);
                }

                storage.setLeapDuration(4);
            }
        }
    }

    @Listener
    public static void onLivingUpdate(final LivingTickEvent event) {
        final IEntityData entityData = Components.ENTITY_DATA.get(event.getEntity());

        entityData.tick();

        if (entityData.isFrozen()) {
            event.setFail();
        }
    }

    @Listener
    public static void onGetCollsionBoxes(final LivingCollisionEvent event) {
        final Entity entity = event.getEntity();

        if (!entity.world.isClient && entity instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) entity;
            final SoulboundComponent component = Components.WEAPON_COMPONENT.get(player);
            final GreatswordStorage storage = component.getStorage(StorageType.GREATSWORD_STORAGE);
            final double leapForce = storage.getLeapForce();

            if (leapForce > 0) {
                if (storage.hasSkill(FREEZING)) {
                    for (final Entity nearbyEntity : event.getEntities()) {
                        storage.freeze(nearbyEntity, (int) (20 * leapForce), (float) EntityUtil.getVelocity(player) * (float) leapForce);
                    }
                }

                if (storage.getLeapDuration() <= 0 && player.onGround && (player.getVelocity().y <= 0.01 || player.isCreative())) {
                    storage.setLeapDuration(7);
                }

                if (player.isInLava()) {
                    storage.resetLeapForce();
                }
            }
        }
    }

    @Listener
    public static void onEnderTeleport(final EnderTeleportEvent event) {
        final IEntityData component = Components.ENTITY_DATA.get(event.getEntity());

        if (component.cannotTeleport()) {
            event.setFail();
        }
    }
}
