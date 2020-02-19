package transfarmer.soulboundarmory.event;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.entity.EntityReachModifier;
import transfarmer.soulboundarmory.entity.EntitySoulDagger;
import transfarmer.soulboundarmory.entity.EntitySoulLightningBolt;
import transfarmer.soulboundarmory.network.weapon.client.CWeaponData;
import transfarmer.soulboundarmory.network.weapon.client.CWeaponDatum;

import java.util.Random;
import java.util.UUID;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import static transfarmer.soulboundarmory.Configuration.levelupNotifications;
import static transfarmer.soulboundarmory.Configuration.multipliers;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponAttribute.*;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.LEVEL;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.XP;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.DAGGER;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MOD_ID)
public class WeaponEventSubscriber {
    @SubscribeEvent
    public static void onRegisterEntityEntry(final RegistryEvent.Register<EntityEntry> entry) {
        entry.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntitySoulDagger.class)
                .id(new ResourceLocation(Main.MOD_ID, "entity_soul_dagger"), 0)
                .name("soul dagger")
                .tracker(256, 1, true)
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

        final ISoulWeapon capability = SoulWeaponProvider.get(event.player);

        if (capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
                && !event.player.world.getGameRules().getBoolean("keepInventory")) {
            event.player.addItemStackToInventory(capability.getItemStack(capability.getCurrentType()));
        }
    }

    private static void updatePlayer(final EntityPlayer player) {
        final ISoulWeapon capability = SoulWeaponProvider.get(player);

        Main.CHANNEL.sendTo(new CWeaponData(
                capability.getCurrentType(),
                capability.getCurrentTab(),
                capability.getAttackCooldwn(),
                capability.getBoundSlot(),
                capability.getData(),
                capability.getAttributes(),
                capability.getEnchantments()), (EntityPlayerMP) player
        );
    }

    @SubscribeEvent
    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ISoulWeapon capability = SoulWeaponProvider.get(player);
        final SoulWeaponType type = capability.getCurrentType();

        if (type != null && capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
                && !player.world.getGameRules().getBoolean("keepInventory")) {

            event.getDrops().removeIf((EntityItem item) -> SoulWeaponHelper.isSoulWeapon(item.getItem()));
        }
    }

    @SubscribeEvent
    public static void onClone(final Clone event) {
        final ISoulWeapon originalInstance = SoulWeaponProvider.get(event.getOriginal());
        final ISoulWeapon instance = SoulWeaponProvider.get(event.getEntityPlayer());

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setCurrentTab(originalInstance.getCurrentTab());
        instance.setBoundSlot(originalInstance.getBoundSlot());
        instance.set(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase != END) return;

        if (SoulWeaponHelper.hasSoulWeapon(event.player)) {
            ISoulWeapon instance = SoulWeaponProvider.get(event.player);
            InventoryPlayer inventory = event.player.inventory;

            if (SoulWeaponHelper.isSoulWeaponEquipped(event.player)) {
                final SoulWeaponType heldItemType = SoulWeaponType.getType(inventory.getCurrentItem().getItem());

                if (heldItemType != instance.getCurrentType()) {
                    instance.setCurrentType(heldItemType);
                }

                if (instance.getAttackCooldwn() > 0) {
                    instance.addCooldown(-1);
                }
            }

            if (instance.getCurrentType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : inventory.mainInventory) {
                    if (SoulWeaponHelper.isSoulWeapon(itemStack)) {
                        final int index = inventory.mainInventory.indexOf(itemStack);

                        if (itemStack.getItem() == instance.getCurrentType().item && firstSlot == -1) {
                            firstSlot = index;

                            if (instance.getBoundSlot() != -1) {
                                instance.setBoundSlot(index);
                            }

                            continue;
                        }

                        if (!event.player.isCreative() && index != firstSlot) {
                            inventory.deleteStack(itemStack);
                        }
                    }
                }
            }

            for (final ItemStack itemStack : inventory.mainInventory) {
                if (!SoulWeaponHelper.isSoulWeapon(itemStack)) continue;

                final ItemStack newItemStack = instance.getItemStack(itemStack);

                if (!SoulWeaponHelper.areDataEqual(itemStack, newItemStack)) {
                    if (itemStack.hasDisplayName()) {
                        newItemStack.setStackDisplayName(itemStack.getDisplayName());
                    }

                    inventory.setInventorySlotContents(inventory.mainInventory.indexOf(itemStack), newItemStack);
                }
            }
        }

        final ISoulWeapon capability = SoulWeaponProvider.get(event.player);

        if (capability.getCurrentType() != null && capability.getLightningCooldown() > 0) {
            capability.decrementLightningCooldown();
        }
    }

    @SubscribeEvent
    public static void onEntityItemPickup(final EntityItemPickupEvent event) {
        event.setResult(ALLOW);

        SoulItemHelper.addItemStack(event.getItem().getItem(), event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void onBreakSpeed(final BreakSpeed event) {
        final ISoulWeapon capability = SoulWeaponProvider.get(event.getEntityPlayer());

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
            final SoulWeaponType weaponType;

            if (trueSource instanceof EntityPlayer) {
                final ISoulWeapon instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                } else if (source instanceof EntityPlayer) {
                    weaponType = instance.getCurrentType();
                } else if (source instanceof EntitySoulLightningBolt) {
                    weaponType = SWORD;
                } else return;

                if (weaponType != null && instance.getAttribute(CRITICAL, weaponType) > new Random().nextInt(100)) {
                    event.setAmount(2 * event.getAmount());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(final LivingKnockBackEvent event) {
        if (!event.getAttacker().world.isRemote) {
            Entity attacker = event.getAttacker();
            SoulWeaponType weaponType = null;
            final ISoulWeapon instance = SoulWeaponProvider.get(attacker);

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
                instance = SoulWeaponProvider.get(trueSource);

                if (source instanceof EntitySoulDagger) {
                    weaponType = DAGGER;
                    displayName = ((EntitySoulDagger) source).itemStack.getDisplayName();
                    source = ((EntitySoulDagger) source).shootingEntity;
                } else {
                    if (source instanceof EntityPlayer) {
                        weaponType = instance.getCurrentType();
                    } else if (source instanceof EntitySoulLightningBolt) {
                        weaponType = SWORD;
                        source = ((EntitySoulLightningBolt) source).getCaster();
                    } else return;

                    displayName = ((EntityPlayerMP) source).getHeldItemMainhand().getDisplayName();
                }
            } else return;

            if (source instanceof EntityPlayer && weaponType != null) {
                //noinspection ConstantConditions
                if (attackDamage != null || Configuration.passiveXP || entity instanceof EntitySlime) {
                    double attackDamageValue = 0;

                    //noinspection ConstantConditions
                    if (attackDamage != null) {
                        attackDamageValue = attackDamage.getAttributeValue();
                    }

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

    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        final Entity entity = event.getEntity();

        if (entity instanceof EntityLightningBolt && !(entity instanceof EntitySoulLightningBolt)
                && !entity.writeToNBT(new NBTTagCompound()).getUniqueId("casterUUID").equals(new UUID(0, 0))) {
            event.setCanceled(true);
        }
    }
}
