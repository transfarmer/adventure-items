package transfarmer.soulboundarmory.capability;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.SoulType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ISoulCapability {
    EntityPlayer getPlayer();

    void setPlayer(EntityPlayer player);

    void setStatistics(int[][] data, float[][] attributes, int[][] enchantments);

    int[][] getData();

    float[][] getAttributes();

    int[][] getEnchantments();

    void setData(int[][] data);

    void setAttributes(float[][] attributes);

    void setEnchantments(int[][] enchantments);

    SoulType getType(ItemStack itemStack);

    SoulType getCurrentType();

    void setCurrentType(SoulType type);

    void setCurrentType(int index);

    int getItemAmount();

    int getDatumAmount();

    int getAttributeAmount();

    int getEnchantmentAmount();

    int getDatum(SoulDatum datum, SoulType type);

    void setDatum(int amount, SoulDatum datum, SoulType type);

    boolean addDatum(int amount, SoulDatum datum, SoulType type);

    float getAttribute(SoulAttribute attribute, SoulType type, boolean total, boolean effective);

    float getAttribute(SoulAttribute attribute, SoulType type, boolean total);

    float getAttribute(SoulAttribute attribute, SoulType type);

    void setAttribute(float value, SoulAttribute attribute, SoulType type);

    void addAttribute(int amount, SoulAttribute attribute, SoulType type);

    void setData(int[] data, SoulType type);

    void setAttributes(float[] attributes, SoulType type);

    int getEnchantment(SoulEnchantment enchantment, SoulType type);

    void setEnchantments(int[] enchantments, SoulType type);

    void addEnchantment(int amount, SoulEnchantment enchantment, SoulType type);

    int getNextLevelXP(SoulType type);

    int getCurrentTab();

    void setCurrentTab(int currentTab);

    int getBoundSlot();

    void bindSlot(int boundSlot);

    void unbindSlot();

    Map<SoulEnchantment, Integer> getEnchantments(SoulType type);

    AttributeModifier[] getAttributeModifiers(SoulType type);

    ItemStack getItemStack(SoulType type);

    ItemStack getItemStack(ItemStack itemStack);

    List<String> getTooltip(SoulType type);

    List<Item> getConsumableItems();

    Class<? extends ISoulItem> getBaseItemClass();

    default boolean hasSoulItem() {
        final Class<? extends ISoulItem> baseItemClass = this.getBaseItemClass();

        for (final ItemStack itemStack : this.getPlayer().inventory.mainInventory) {
            if (baseItemClass.isInstance(itemStack.getItem())) return true;
        }

        return baseItemClass.isInstance(this.getPlayer().getHeldItemOffhand().getItem());
    }

    default ItemStack getEquippedItemStack() {
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

    default void update() {
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
