package user11681.soulboundarmory.component.soulbound.player;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.usersmanual.collections.ArrayMap;
import user11681.usersmanual.collections.OrderedArrayMap;
import user11681.usersmanual.item.ItemUtil;

import static user11681.soulboundarmory.component.Components.WEAPON_COMPONENT;

public abstract class SoulboundComponentBase implements EntitySyncedComponent {
    protected final PlayerEntity player;
    protected final ArrayMap<StorageType<?>, ItemStorage<?>> storages;

    protected ItemStorage<?> lastStorage;

    public SoulboundComponentBase(final PlayerEntity player) {
        this.player = player;
        this.storages = new OrderedArrayMap<>();
    }

    public static SoulboundComponentBase get(final Entity entity) {
        return WEAPON_COMPONENT.get(entity);
    }

    public static Optional<SoulboundComponentBase> maybeGet(final Entity entity) {
        return WEAPON_COMPONENT.maybeGet(entity);
    }

    protected void store(final ItemStorage<?> storage) {
        this.storages.put(storage.getType(), storage);
    }

    @Override
    public PlayerEntity getEntity() {
        return this.player;
    }

    public ItemStorage<?> getStorage() {
        return this.lastStorage;
    }

    public ItemStorage<?> getHeldItemStorage() {
        for (final ItemStack itemStack : this.player.getItemsHand()) {
            for (final ItemStorage<?> component : this.storages.values()) {
                if (itemStack.getItem() == component.getItem()) {
                    return component;
                }
            }
        }

        return null;
    }

    public ItemStorage<?> getAnyHeldItemStorage() {
        for (final ItemStorage<?> component : this.storages.values()) {
            if (component.isAnyItemEquipped()) {
                return component;
            }
        }

        return null;
    }

    public <T extends ItemStorage<T>> T getStorage(final StorageType<T> type) {
        //noinspection unchecked
        return (T) this.storages.get(type);
    }

    public ArrayMap<StorageType<?>, ItemStorage<?>> getStorages() {
        return this.storages;
    }

    public boolean hasSoulboundItem() {
        for (final ItemStorage<?> storage : this.storages.values()) {
            if (ItemUtil.hasItem(this.player, storage.getBaseItemClass())) {
                return true;
            }
        }

        return false;
    }

    public void tick() {
        if (this.hasSoulboundItem()) {
            final ItemStorage<?> storage = this.getStorage();

            if (storage != null) {
                final Class<? extends SoulboundItem> baseItemClass = storage.getBaseItemClass();
                final PlayerInventory inventory = this.getEntity().inventory;
                final List<ItemStack> main = ItemUtil.getCombinedSingleInventory(this.player);
                int firstSlot = -1;

                for (final ItemStack itemStack : main) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        final ItemStack newItemStack = storage.getItemStack();
                        final int index = main.indexOf(itemStack);

                        if (itemStack.getItem() == storage.getItem() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (storage.getBoundSlot() != -1) {
                                storage.bindSlot(firstSlot);
                            }

                            if (!SoulboundItemUtil.areDataEqual(itemStack, newItemStack)) {
                                if (itemStack.hasCustomName()) {
                                    newItemStack.setCustomName(itemStack.getName());
                                }

                                inventory.setInvStack(firstSlot, newItemStack);
                            }
                        } else if (!this.getEntity().isCreative() && (index != firstSlot || firstSlot != -1)) {
                            inventory.removeOne(itemStack);
                        }
                    }
                }
            }
        }

        for (final ItemStorage<?> storage : this.storages.values()) {
            storage.tick();
        }
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {
        for (final ItemStorage<?> storage : this.storages.values()) {
            storage.fromTag(tag.getCompound(storage.getType().toString()));
        }
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        for (final ItemStorage<?> storage : this.storages.values()) {
            tag.put(storage.getType().toString(), storage.toTag(new CompoundTag()));
        }

        return tag;
    }
}
