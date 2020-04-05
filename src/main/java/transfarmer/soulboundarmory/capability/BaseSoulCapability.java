package transfarmer.soulboundarmory.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.Configuration.maxLevel;

public abstract class BaseSoulCapability implements ISoulCapability {
    protected EntityPlayer player;
    protected SoulType currentType;
    protected SoulDatum datum;
    protected int[][] data;
    protected float[][] attributes;
    protected int[][] enchantments;
    protected int boundSlot;
    protected int currentTab;

    protected BaseSoulCapability(final SoulDatum datum) {
        this.datum = datum;
        this.boundSlot = -1;
        this.data = new int[this.getItemAmount()][this.getDatumAmount()];
        this.attributes = new float[this.getItemAmount()][this.getAttributeAmount()];
        this.enchantments = new int[this.getItemAmount()][this.getEnchantmentAmount()];
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void setPlayer(final EntityPlayer player) {
        this.player = player;
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public void bindSlot(final int boundSlot) {
        this.boundSlot = boundSlot;
    }

    @Override
    public void unbindSlot() {
        this.boundSlot = -1;
    }

    @Override
    public int getCurrentTab() {
        return this.currentTab;
    }

    @Override
    public void setCurrentTab(final int tab) {
        this.currentTab = tab;
    }

    @Override
    public int getDatumAmount() {
        return this.datum.getAmount();
    }

    @Override
    public boolean canLevelUp(final SoulType type) {
        return this.getDatum(this.datum.level, type) < maxLevel || maxLevel < 0;
    }

    @Override
    public boolean hasSoulItem() {
        final Class<? extends ISoulItem> baseItemClass = this.getBaseItemClass();

        for (final ItemStack itemStack : this.getPlayer().inventory.mainInventory) {
            if (baseItemClass.isInstance(itemStack.getItem())) return true;
        }

        return baseItemClass.isInstance(this.getPlayer().getHeldItemOffhand().getItem());
    }

    @Override
    public ItemStack getEquippedItemStack() {
        final Class<? extends ISoulItem> baseItemClass = this.getBaseItemClass();
        final ItemStack mainhandStack = this.getPlayer().getHeldItemMainhand();

        if (baseItemClass.isInstance(mainhandStack.getItem())) {
            return mainhandStack;
        }

        final List<Item> consumableItems = this.getConsumableItems();

        if (consumableItems.contains(mainhandStack.getItem())) {
            return mainhandStack;
        }

        final ItemStack offhandStack = this.getPlayer().getHeldItemOffhand();

        if (baseItemClass.isInstance(offhandStack.getItem())) {
            return offhandStack;
        }

        if (consumableItems.contains(offhandStack.getItem())) {
            return mainhandStack;
        }

        return null;
    }

    @Override
    public void update() {
        if (this.hasSoulItem()) {
            final Class<? extends ISoulItem> baseItemClass = this.getBaseItemClass();
            final InventoryPlayer inventory = this.getPlayer().inventory;
            final ItemStack equippedItemStack = this.getEquippedItemStack();
            final List<ItemStack> mainInventory = new ArrayList<>(this.getPlayer().inventory.mainInventory);
            mainInventory.add(this.getPlayer().getHeldItemOffhand());

            if (equippedItemStack != null && baseItemClass.isInstance(equippedItemStack.getItem())) {
                final SoulType type = SoulType.get(equippedItemStack);

                if (type != this.getCurrentType()) {
                    this.setCurrentType(type);
                }
            }

            if (this.getCurrentType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : mainInventory) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        final ItemStack newItemStack = this.getItemStack(itemStack);
                        final int index = mainInventory.indexOf(itemStack);

                        if (itemStack.getItem() == this.getCurrentType().getItem() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (this.getBoundSlot() != -1) {
                                this.bindSlot(firstSlot);
                            }

                            if (!SoulItemHelper.areDataEqual(itemStack, newItemStack)) {
                                if (itemStack.hasDisplayName()) {
                                    newItemStack.setStackDisplayName(itemStack.getDisplayName());
                                }

                                inventory.setInventorySlotContents(firstSlot, newItemStack);
                            }
                        } else if (!this.getPlayer().isCreative() && index != firstSlot) {
                            inventory.deleteStack(itemStack);
                        }
                    }
                }
            }
        }
    }
}
