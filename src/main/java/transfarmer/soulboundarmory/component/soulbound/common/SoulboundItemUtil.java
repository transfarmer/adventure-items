package transfarmer.soulboundarmory.component.soulbound.common;

import com.google.common.collect.Multimap;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.farmerlib.item.ItemUtil;
import transfarmer.soulboundarmory.component.config.IConfigComponent;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.item.SoulboundItem;
import transfarmer.soulboundarmory.item.SoulboundWeaponItem;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;

public class SoulboundItemUtil {
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");

    public static ISoulboundItemComponent<? extends Component> getFirstComponent(final PlayerEntity player) {
        for (final ItemStack itemStack : player.getItemsHand()) {
            final ISoulboundItemComponent<? extends Component> component = ISoulboundItemComponent.get(itemStack);

            if (component != null) {
                return component;
            }
        }

        return null;
    }

    public static boolean isSoulWeaponEquipped(final PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof SoulboundWeaponItem
                || player.getOffHandStack().getItem() instanceof SoulboundWeaponItem;
    }

    public static boolean addItemStack(final ItemStack itemStack, final PlayerEntity player) {
        return addItemStack(itemStack, player, true);
    }

    public static boolean addItemStack(final ItemStack itemStack, final PlayerEntity player, boolean hasReservedSlot) {
        final PlayerInventory inventory = player.inventory;

        if (!(itemStack.getItem() instanceof SoulboundItem)) {
            hasReservedSlot = false;
        }

        if (!hasReservedSlot) {
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
                if (!ISoulboundItemComponent.isSlotBound(index) && mergedInventory.get(index).isEmpty()) {
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

        final int boundSlot = ISoulboundItemComponent.get(itemStack).getBoundSlot();

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
            if (itemStack != null && itemStack.getItem() instanceof SoulboundWeaponItem) {
                return true;
            }
        }

        return false;
    }

    public static void removeSoulboundItems(final PlayerEntity player, final Class<? extends SoulboundItem> clazz) {
        for (final ItemStack itemStack : player.inventory.main) {
            if (clazz.isInstance(itemStack.getItem())) {
                player.inventory.removeOne(itemStack);
            }
        }
    }
}
