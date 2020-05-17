package transfarmer.soulboundarmory.event;

public class EntityEventListeners {
/*
    public static void onLivingAttack(final LivingAttackEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof PlayerEntity && WeaponProvider.get(entity).getLeapForce() > 0) {
            final DamageSource damageSource = event.getSource();

            if (!damageSource.damageType.contains("explosion") && !(damageSource instanceof EntityDamageSourceIndirect)) {
                event.setCanceled(true);
            }
        }
    }

    public static void onLivingHurt(final LivingHurtEvent event) {
        final Entity trueSource = event.getSource().getTrueSource();

        if (trueSource instanceof PlayerEntity && !trueSource.world.isClient) {
            final Entity source = event.getSource().getImmediateSource();
            final ISoulboundItemComponent instance = WeaponProvider.get(trueSource);
            final IItem item;

            if (source instanceof SoulboundDaggerEntity) {
                item = DAGGER;
            } else if (source instanceof PlayerEntity) {
                item = instance.getItemType();
            } else if (source instanceof SoulboundLightningEntity) {
                item = SWORD;
            } else if (source instanceof SoulboundFireballEntity) {
                item = STAFF;
            } else {
                return;
            }

            final Random random = trueSource.world.rand;
            final float attackDamage = item != null && instance.getAttribute(item, CRITICAL_STRIKE_PROBABILITY) > random.nextDouble()
                    ? 2 * event.getAmount()
                    : event.getAmount();

            if (instance.hasSkill(item, new NourishmentSkill())) {
                final Skill leeching = (Skill) instance.getSkill(item, NOURISHMENT);

                final float food = (1 + leeching.getLevel()) * attackDamage / 20F;
                final int r = random.nextInt((int) Math.ceil(food) + 1);
                ((PlayerEntity) trueSource).getFoodStats().addStats(r, 2 * food);
            }

            event.setAmount(attackDamage);
        }
    }

    public static void onLivingKnockback(final LivingKnockBackEvent event) {
        final Entity entity = event.getEntity();

        if (!entity.world.isClient) {
            Entity attacker = event.getAttacker();
            IItem weaponType = null;
            final ISoulboundItemComponent instance = WeaponProvider.get(attacker);

            if (attacker instanceof SoulboundDaggerEntity) {
                attacker = ((SoulboundDaggerEntity) attacker).shootingEntity;
                weaponType = DAGGER;
            } else if (attacker instanceof PlayerEntity) {
                weaponType = instance.getItemType();
            }

            if (attacker instanceof PlayerEntity && weaponType != null) {
                if (SoulboundItemUtil.isSoulWeaponEquipped((PlayerEntity) attacker)) {
                    event.setStrength((event.getStrength() * (float) (1 + instance.getAttribute(weaponType, KNOCKBACK) / 6)));
                }
            }

            if (entity instanceof PlayerEntity) {
                final IWeaponComponent component = WeaponProvider.get(entity);

                if (component.getLeapForce() > 0) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void onLivingDeath(final LivingDeathEvent event) {
        final LivingEntity entity = event.getEntityLiving();

        Entity attacker = event.getSource().getTrueSource();

        if (attacker == null) {
            attacker = entity.getCombatTracker().getBestAttacker();
        }

        if ((attacker instanceof PlayerEntity) && !attacker.world.isClient) {
            final IAttributeInstance attackDamage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            final IAttributeInstance armor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
            final Entity immediateSource = event.getSource().getImmediateSource();
            final PlayerEntity player = ((PlayerEntity) attacker);
            final IWeaponComponent component = WeaponProvider.get(attacker);
            final IItem item;
            final String displayName;

            if (immediateSource instanceof SoulboundDaggerEntity) {
                item = DAGGER;
                displayName = ((SoulboundDaggerEntity) immediateSource).itemStack.getDisplayName();
            } else if (immediateSource instanceof SoulboundLightningEntity) {
                item = SWORD;
                displayName = ItemUtil.getEquippedItemStack(player, SOULBOUND_SWORD_ITEM).getDisplayName();
            } else {
                item = component.getItemType((player.getMainHandStack()));
                displayName = player.getMainHandStack().getDisplayName();
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

                if (component.addDatum(item, XP, (int) Math.round(xp)) && MainConfig.instance().getLevelupNotifications()) {
                    attacker.sendMessage(new TextComponentTranslation("message.soulboundarmory.levelup", displayName, component.getDatum(item, LEVEL)));
                }

                component.sync();
            }
        }
    }

    @SubscribeEvent(priority = HIGH)
    public static void onLivingFall(final LivingFallEvent event) {
        final Entity fallen = event.getEntity();

        if (!fallen.world.isClient && fallen instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) fallen;
            final IWeaponComponent component = WeaponProvider.get(player);
            final double leapForce = component.getLeapForce();

            if (leapForce > 0) {
                if (component.hasSkill(GREATSWORD, FREEZING)) {
                    final double radiusXZ = Math.min(4, event.getDistance());
                    final double radiusY = Math.min(2, 0.5F * event.getDistance());
                    final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(
                            player.getX() - radiusXZ, player.getY() - radiusY, player.getZ() - radiusXZ,
                            player.getX() + radiusXZ, player.getY() + radiusY, player.getZ() + radiusXZ
                    ));

                    boolean froze = false;

                    for (final Entity entity : nearbyEntities) {
                        final IEntityData frozenComponent = EntityDatumProvider.get(entity);

                        if (frozenComponent != null && entity.getDistanceSq(entity) <= radiusXZ * radiusXZ) {
                            component.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());

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
                                        player.getX() + x,
                                        player.getY() + player.eyeHeight,
                                        player.getZ() + z,
                                        particles, 0, 0, 0, 0D
                                );
                                world.spawnParticle(EnumParticleTypes.SNOWBALL,
                                        player.getX() + x,
                                        player.getY() + player.eyeHeight,
                                        player.getZ() - z,
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

                component.setLeapDuration(4);
            }
        }
    }

    public static void onLivingUpdate(final LivingUpdateEvent event) {
        final IEntityData entityData = EntityDatumProvider.get(event.getEntity());

        if (entityData != null) {
            entityData.onUpdate();

            if (entityData.isFrozen()) {
                event.setCanceled(true);
            }
        }
    }

    public static void onGetCollsionBoxes(final GetCollisionBoxesEvent event) {
        final Entity entity = event.getEntity();

        if (!event.getWorld().isClient && entity instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) entity;
            final IWeaponComponent component = WeaponProvider.get(player);
            final double leapForce = component.getLeapForce();

            if (leapForce > 0) {
                if (component.hasSkill(GREATSWORD, FREEZING)) {
                    final List<Entity> nearbyEntities = player.world.getEntitiesWithinAABBExcludingEntity(player, event.getAabb());

                    for (final Entity nearbyEntity : nearbyEntities) {
                        component.freeze(nearbyEntity, (int) (20 * leapForce), (float) EntityUtil.getVelocity(player) * (float) leapForce);
                    }
                }

                if (component.getLeapDuration() <= 0 && player.onGround && (player.motionY <= 0.01 || player.isCreative())) {
                    component.setLeapDuration(7);
                }

                if (player.isInLava()) {
                    component.resetLeapForce();
                }
            }
        }
    }

    public static void onRightClickItem(final LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) event.getEntity();
            final IWeaponComponent component = WeaponProvider.get(player);

            if (event.getItem().getItem() instanceof SoulboundDaggerItem) {
                final SoulboundDaggerItem item = (SoulboundDaggerItem) event.getItem().getItem();

                if (item.getMaxUsageRatio((float) component.getAttribute(DAGGER, ATTACK_SPEED), event.getDuration()) == 1) {
                    event.setDuration(event.getDuration() + 1);
                }
//
//                if (event.getDuration() == item.getMaxItemUseDuration()) {
//                    event.setDuration(item.getMaxItemUseDuration() - 1);
//                }
//
//                event.setDuration(Math.round(item.getMaxItemUseDuration() - 20 * item.getMaxUsageRatio(component.getAttribute(ATTACK_SPEED, DAGGER, true, true), event.getDuration())));
            }
        }
    }

    public static void onEnderTeleport(final EnderTeleportEvent event) {
        final IEntityData component = EntityDatumProvider.get(event.getEntity());

        if (component != null && component.cannotTeleport()) {
            event.setCanceled(true);
        }
    }
*/
}
