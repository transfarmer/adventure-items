package transfarmer.soulboundarmory.component.soulbound.common;

import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import transfarmer.farmerlib.util.CollectionUtil;
import transfarmer.farmerlib.util.ItemUtil;
import transfarmer.soulboundarmory.component.config.IConfigComponent;
import transfarmer.soulboundarmory.component.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.component.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.item.SoulboundTool;
import transfarmer.soulboundarmory.item.SoulboundWeapon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;

public class SoulboundItemUtil {
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");

    public static ISoulboundComponent getFirstHeldCapability(final PlayerEntity player) {
        final ISoulboundComponent capability = getFirstCapability(player, player.getMainHandStack());

        if (capability == null) {
            return getFirstCapability(player, player.getOffHandStack());
        }

        return capability;
    }

    public static ISoulboundComponent getFirstCapability(final PlayerEntity player,
                                                         @Nonnull final ItemStack itemStack) {
        return getFirstCapability(player, itemStack.getItem());
    }

    public static ISoulboundComponent getFirstCapability(final PlayerEntity player, @Nullable Item item) {
        final boolean passedNull = item == null;
        ISoulboundComponent capability = null;

        if (passedNull) {
            item = player.getMainHandStack().getItem();
        }

        if (item instanceof ItemSoulbound) {
            if (item instanceof SoulboundWeapon) {
                capability = WeaponProvider.get(player);
            } else if (item instanceof SoulboundTool) {
                capability = ToolProvider.get(player);
            }
        }

        if (capability == null) {
            if (item == Items.WOODEN_SWORD) {
                capability = WeaponProvider.get(player);
            } else if (item == Items.WOODEN_PICKAXE) {
                capability = ToolProvider.get(player);
            }
        }

        if (passedNull && capability == null) {
            capability = getFirstCapability(player, player.getOffHandStack());
        }

        return capability;
    }

    public static boolean isSoulWeaponEquipped(final PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof SoulboundWeapon
                || player.getOffHandStack().getItem() instanceof SoulboundWeapon;
    }

    public static boolean addItemStack(final ItemStack itemStack, final PlayerEntity player) {
        return addItemStack(itemStack, player, true);
    }

    public static boolean addItemStack(final ItemStack itemStack, final PlayerEntity player, boolean hasReservedSlot) {
        final PlayerInventory inventory = player.inventory;

        if (!(itemStack.getItem() instanceof ItemSoulbound)) {
            hasReservedSlot = false;
        }

        if (!hasReservedSlot) {
            final int weaponBoundSlot = WeaponProvider.get(player).getBoundSlot();
            final int toolBoundSlot = ToolProvider.get(player).getBoundSlot();
            int slot = inventory.getOccupiedSlotWithRoomForStack(itemStack);

            if (slot == -1) {
                slot = inventory.getEmptySlot();
            }

            final int size = inventory.main.size();
            final List<ItemStack> mergedInventory = CollectionUtil.merge(DefaultedList.of(), inventory.main, inventory.offHand);

            if (slot != -1) {
                final ItemStack slotStack = slot != 40 ? mergedInventory.get(slot) : mergedInventory.get(size);
                final int transferred = Math.min(slotStack.getMaxCount() - slotStack.getCount(), itemStack.getCount());

                itemStack.setCount(itemStack.getCount() - transferred);
                slotStack.setCount(slotStack.getCount() + transferred);

                if (!itemStack.isEmpty()) {
                    return addItemStack(itemStack, player, false);
                }

                return true;
            }

            for (int index = 0; index < mergedInventory.size(); index++) {
                if (index != weaponBoundSlot && index != toolBoundSlot && mergedInventory.get(index).isEmpty()) {
                    if (index != size) {
                        return inventory.insertStack(index, itemStack);
                    }

                    if (player.getOffHandStack().isEmpty() && IConfigComponent.get(player).getAddToOffhand()) {
                        inventory.setInvStack(size + 4, itemStack);
                        itemStack.setCount(0);
                    }
                }
            }

            return false;
        }

        final int boundSlot = getFirstCapability(player, itemStack.getItem()).getBoundSlot();

        if (boundSlot >= 0) {
            if (inventory.getInvStack(boundSlot).isEmpty()) {
                return inventory.insertStack(boundSlot, itemStack);
            }
        } else {
            final ItemStack mainhandStack = player.getMainHandStack();

            if (mainhandStack.isEmpty()) {
                return inventory.insertStack(ItemUtil.getSlotFor(inventory, mainhandStack), itemStack);
            }
        }

        return addItemStack(itemStack, player, false);
    }

    public static boolean areDataEqual(final ItemStack itemStack0, final ItemStack itemStack1) {
        final Multimap<String, EntityAttributeModifier> attributeModifiers = itemStack0.getAttributeModifiers(MAINHAND);

        for (final String key : attributeModifiers.keySet()) {
            final Collection<EntityAttributeModifier> modifiers = attributeModifiers.get(key);

            for (final EntityAttributeModifier modifier0 : modifiers) {

                for (final EntityAttributeModifier modifier1 : itemStack1.getAttributeModifiers(MAINHAND).get(key)) {
                    if (!modifier0.getId().equals(modifier1.getId()) || modifier0.getAmount() != modifier1.getAmount()) {
                        return false;
                    }
                }
            }
        }

        final ListTag enchantmentTagList0 = itemStack0.getEnchantments();
        final ListTag enchantmentTagList1 = itemStack1.getEnchantments();
        final int listEntries = Math.max(enchantmentTagList0.size(), enchantmentTagList1.size());

        for (int i = 0; i < listEntries; i++) {
            final CompoundTag tagCompound0 = enchantmentTagList0.getCompound(i);
            final CompoundTag tagCompound1 = enchantmentTagList1.getCompound(i);

            if (tagCompound0.getSize() == tagCompound1.getSize()) {
                final int compoundEntries = Math.max(tagCompound0.getSize(), tagCompound1.getSize());

                for (int j = 0; j < compoundEntries; j++) {
                    for (final String key : tagCompound1.getKeys()) {
                        if (tagCompound0.getShort(key) != tagCompound1.getShort(key)) {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public static boolean hasSoulWeapon(final PlayerEntity player) {
        final ItemStack[] inventory = player.inventory.main.toArray(new ItemStack[player.inventory.getInvSize() + 1]);
        inventory[player.inventory.getInvSize()] = player.getOffHandStack();

        for (final ItemStack itemStack : inventory) {
            if (itemStack != null && itemStack.getItem() instanceof SoulboundWeapon) {
                return true;
            }
        }

        return false;
    }

    public static void removeSoulboundItems(final PlayerEntity player, final Class<? extends ItemSoulbound> clazz) {
        for (final ItemStack itemStack : player.inventory.main) {
            if (clazz.isInstance(itemStack.getItem())) {
                player.inventory.removeOne(itemStack);
            }
        }
    }
}
