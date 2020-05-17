package transfarmer.soulboundarmory.event;

public class PlayerEventListeners {
/*
    public static void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
        Main.CHANNEL.sendTo(new S2CConfig(), (PlayerEntityMP) event.player);
    }

    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final PlayerEntity player = event.getPlayerEntity();

        if (!(player instanceof FakePlayer) && !player.world.getGameRules().getBoolean("keepInventory")) {
            ISoulboundItemComponent weapons = WeaponProvider.get(player);
            IItem type = weapons.getItemType();

            if (type != null && weapons.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof SoulboundWeaponItem && SoulboundItemUtil.addItemStack(item.getItem(), player));
            }

            weapons = ToolProvider.get(player);
            type = weapons.getItemType();

            if (type != null && weapons.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof SoulboundToolItem && SoulboundItemUtil.addItemStack(item.getItem(), player));
            }
        }
    }

    public static void onClone(final Clone event) {
        final PlayerEntity original = event.getOriginal();
        final PlayerEntity player = event.getPlayerEntity();

        final ISoulboundItemComponent originalTools = ToolProvider.get(original);
        final ISoulboundItemComponent originalWeapons = WeaponProvider.get(original);
        final ISoulboundItemComponent newTools = ToolProvider.get(player);
        final ISoulboundItemComponent newWeapons = WeaponProvider.get(player);

        newWeapons.fromTag(originalWeapons.toTag());
        newTools.fromTag(originalTools.toTag());

        IItem type = newWeapons.getItemType();

        if (!player.world.getGameRules().getBoolean("keepInventory")) {
            if (type != null && newWeapons.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                player.addItemStackToInventory(newWeapons.getItemStack(type));
            }

            if ((type = newTools.getItemType()) != null && newTools.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                player.addItemStackToInventory(newTools.getItemStack(type));
            }
        }
    }

    public static void onRightClickBlock(final RightClickBlock event) {
        final PlayerEntity player = event.getPlayerEntity();
        final ItemStack stackMainhand = player.getMainHandStack();

        if (stackMainhand.getItem() instanceof SoulboundWeaponItem && stackMainhand != event.getItemStack()) {
            event.setUseItem(DENY);
        }
    }

    public static void onBreakSpeed(final BreakSpeed event) {
        if (event.getPlayerEntity().getMainHandStack().getItem() instanceof SoulboundItem) {
            final SoulboundItem item = (SoulboundItem) event.getPlayerEntity().getMainHandStack().getItem();
            final ISoulboundItemComponent component = SoulboundItemUtil.getFirstComponent(event.getPlayerEntity(), (Item) item);
            final IItem type = component.getItemType();

            if (item instanceof SoulboundToolItem) {
                if (((SoulboundToolItem) item).isEffectiveAgainst(event.getState())) {
                    float newSpeed = (float) (event.getOriginalSpeed() + component.getAttribute(type, EFFICIENCY));
                    final int efficiency = component.getEnchantment(type, EFFICIENCY);
                    //noinspection ConstantConditions
                    final PotionEffect haste = event.getPlayerEntity().getActivePotionEffect(Potion.getPotionFromIdentifier("haste"));

                    if (efficiency > 0) {
                        newSpeed += 1 + efficiency * efficiency;
                    }

                    if (haste != null) {
                        newSpeed *= haste.getAmplifier() * 0.1;
                    }

                    if (((SoulboundToolItem) item).canHarvestBlock(event.getState(), event.getPlayerEntity())) {
                        event.setNewSpeed(newSpeed);
                    } else {
                        event.setNewSpeed(newSpeed / 4F);
                    }
                } else {
                    event.setNewSpeed((float) ((event.getOriginalSpeed() - 1 + component.getAttribute(type, EFFICIENCY)) / 8));
                }
            } else if (item instanceof SoulboundMeleeWeaponItem) {
                final float newSpeed = (float) component.getAttribute(component.getItemType(), EFFICIENCY);

                event.setNewSpeed(event.getState().getMaterial() == Material.WEB
                        ? Math.max(15, newSpeed)
                        : newSpeed
                );
            }
        }
    }

    public static void onHarvestDrops(final HarvestDropsEvent event) {
        final PlayerEntity player = event.getHarvester();

        if (player != null && player.getMainHandStack().getItem() instanceof SoulboundPickItem && ToolProvider.get(player).hasSkill(PICK, PULL)) {
            event.setDropChance(0);

            for (final ItemStack drop : event.getDrops()) {
                if (!event.getWorld().isClient && !drop.isEmpty() && event.getWorld().getGameRules().getBoolean("doTileDrops") && !event.getWorld().restoringBlockSnapshots) {
                    final Vec3d pos = player.getPositionVector();

                    event.getWorld().spawnEntity(new EntityItem(event.getWorld(), pos.x, pos.y, pos.z, drop));
                }
            }
        }
    }

    @SubscribeEvent(priority = LOW)
    public static void onEntityItemPickup(final EntityItemPickupEvent event) {
        event.setResult(ALLOW);

        SoulboundItemUtil.addItemStack(event.getItem().getItem(), event.getPlayerEntity());
    }

    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase == END) {
            final PlayerEntity player = event.player;

            ToolProvider.get(player).onTick();
            WeaponProvider.get(player).onTick();
        }
    }
*/
}
