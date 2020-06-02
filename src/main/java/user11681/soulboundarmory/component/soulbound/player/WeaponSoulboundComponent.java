package user11681.soulboundarmory.component.soulbound.player;

import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.item.ModItems;

public class WeaponSoulboundComponent extends SoulboundComponentBase {
    public WeaponSoulboundComponent(final PlayerEntity player) {
        super(player);

        this.store(new DaggerStorage(this, ModItems.SOULBOUND_DAGGER));
        this.store(new SwordStorage(this, ModItems.SOULBOUND_SWORD));
        this.store(new GreatswordStorage(this, ModItems.SOULBOUND_GREATSWORD));
        this.store(new StaffStorage(this, ModItems.SOULBOUND_STAFF));
    }

    @Override
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

    @Override
    public ItemStorage<?> getAnyHeldItemStorage() {
        for (final ItemStorage<?> component : this.storages.values()) {
            if (component.isAnyItemEquipped()) {
                return component;
            }
        }

        return null;
    }

    @Nonnull
    @Override
    public ComponentType<SoulboundComponent> getComponentType() {
        return Components.WEAPON_COMPONENT;
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
