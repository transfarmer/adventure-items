package transfarmer.soulboundarmory.component.soulbound.common;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlayerSoulboundComponent implements IPlayerSoulboundComponent {
    private final PlayerEntity player;
    protected final List<ISoulboundItemComponent<? extends Component>> components;

    public PlayerSoulboundComponent(final PlayerEntity player) {
        this.player = player;
        this.components = new ArrayList<>();
    }

    @Override
    public List<ISoulboundItemComponent<? extends Component>> getComponents() {
        return this.components;
    }

    @Override
    public ISoulboundItemComponent<? extends Component> getHeldItemComponent() {
        for (final ItemStack itemStack : this.player.getItemsHand()) {
            for (final ISoulboundItemComponent<? extends Component> component : this.components) {
                if (itemStack == component.getItemStack()) {
                    return component;
                }
            }
        }

        return null;
    }

    @Override
    public ISoulboundItemComponent<? extends Component> getAnyHeldItemComponent() {
        for (final ISoulboundItemComponent<? extends Component> component : this.components) {
            if (component.isAnyItemEquipped()) {
                return component;
            }
        }

        return null;
    }

    @Override
    public void tick() {
//        if (this.hasSoulboundItem()) {
//            final Class<? extends SoulboundItem> baseItemClass = this.getBaseItemClass();
//            final PlayerInventory inventory = this.getEntity().inventory;
//            final List<ItemStack> main = CollectionUtil.arrayList(inventory.main, inventory.offHand);
//            final IItem currentItem = this.getItemType();
//
//            if (currentItem != null) {
//                int firstSlot = -1;
//
//                for (final ItemStack itemStack : main) {
//                    if (baseItemClass.isInstance(itemStack.getItem())) {
//                        final ItemStack newItemStack = this.getItemStack(itemStack);
//                        final int index = main.indexOf(itemStack);
//
//                        if (itemStack.getItem() == this.getItem(currentItem) && (firstSlot == -1 || index == 36)) {
//                            firstSlot = index == 36 ? 40 : index;
//
//                            if (this.getBoundSlot() != -1) {
//                                this.bindSlot(firstSlot);
//                            }
//
//                            if (!SoulboundItemUtil.areDataEqual(itemStack, newItemStack)) {
//                                if (itemStack.hasCustomName()) {
//                                    newItemStack.setCustomName(itemStack.getName());
//                                }
//
//                                inventory.setInvStack(firstSlot, newItemStack);
//                            }
//                        } else if (!this.getEntity().isCreative() && (index != firstSlot || firstSlot != -1)) {
//                            inventory.removeOne(itemStack);
//                        }
//                    }
//                }
//            }
//        }
    }

//    @SuppressWarnings("VariableUseSideOnly")
//    @Override
//    public void sync() {
//        if (!this.isClient) {
//            Main.PACKET_REGISTRY.sendToPlayer(this.getEntity(), Packets.S2C_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toTag()));
//        } else {
//            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toClientTag()));
//        }
//    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {}

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        return tag;
    }
}
