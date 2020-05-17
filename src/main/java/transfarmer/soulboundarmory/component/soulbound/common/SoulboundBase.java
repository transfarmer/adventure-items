package transfarmer.soulboundarmory.component.soulbound.common;

public abstract class SoulboundBase {
/*
    @Override
    public void onTick() {
        if (this.hasSoulboundItem()) {
            final Class<? extends SoulboundItem> baseItemClass = this.getBaseItemClass();
            final PlayerInventory inventory = this.getEntity().inventory;
            final List<ItemStack> main = CollectionUtil.arrayList(inventory.main, inventory.offHand);
            final IItem currentItem = this.getItemType();

            if (currentItem != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : main) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        final ItemStack newItemStack = this.getItemStack(itemStack);
                        final int index = main.indexOf(itemStack);

                        if (itemStack.getItem() == this.getItem(currentItem) && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (this.getBoundSlot() != -1) {
                                this.bindSlot(firstSlot);
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
    }

    @SuppressWarnings("VariableUseSideOnly")
    @Override
    public void sync() {
        if (!this.isClient) {
            Main.PACKET_REGISTRY.sendToPlayer(this.getEntity(), Packets.S2C_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toTag()));
        } else {
            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SYNC, new ExtendedPacketBuffer(this).writeCompoundTag(this.toClientTag()));
        }
    }
*/
}
