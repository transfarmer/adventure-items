package user11681.soulboundarmory.component.soulbound.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.usersmanual.item.ItemUtil;

public abstract class SoulboundComponentBase implements SoulboundComponent {
    protected final PlayerEntity player;
    protected final Map<StorageType<?>, ItemStorage<?>> storages;

    protected ItemStorage<?> lastStorage;

    public SoulboundComponentBase(final PlayerEntity player) {
        this.player = player;
        this.storages = new HashMap<>();
    }

    protected void store(final ItemStorage<?> storage) {
        this.storages.put(storage.getType(), storage);
    }

    @Override
    public PlayerEntity getEntity() {
        return this.player;
    }

    @Override
    public ItemStorage<?> getStorage() {
        return this.lastStorage;
    }

    @Override
    public <T extends ItemStorage<T>> T getStorage(final StorageType<T> type) {
        //noinspection unchecked
        return (T) this.storages.get(type);
    }

    @Override
    public Map<StorageType<?>, ItemStorage<?>> getStorages() {
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

    @Override
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

    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        return tag;
    }
}
